package com.turel.beam.rest;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.turel.beam.DataFlowUtils;
import com.turel.config.GCPStorage;
import com.turel.utils.LogUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Chaim on 07/06/2017.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class ScheduleDataflowController {
    private static final String ENV = "env";
    private static final String JAR_NAME = "jarName";
    private static final String PIPELINE = "pipeline";
    private static final Logger LOG = LoggerFactory.getLogger(ScheduleDataflowController.class);
    private final GCPStorage storage;
    @Value("${dataflow.bucket}")
    private String dataflowBucket;
    @Value("${dataflow.deploy}")
    private String dataflowDeploy;

    private FileCache fileCache = new FileCache();

    @RequestMapping("/")
    public String home() {
        return "Hello World!";
    }

    @RequestMapping("/dataflow/execute")
    public String executeDataflow(HttpServletRequest request) {
        JSONObject root = new JSONObject();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            List<String> appArgs = parameters.keySet().stream().filter(key -> !key.equalsIgnoreCase(PIPELINE))
                    .map(key -> String.format("--%s=%s", key, parameters.get(key)[0])).collect(Collectors.toList());
            String[] appParams = appArgs.toArray(new String[appArgs.size()]);
            String[] pipline = parameters.get(PIPELINE);
            if (pipline == null || pipline.length == 0) {
                throw new RuntimeException("pipeline parameter is missing");
            }
            Class<?> aClass = Class.forName(pipline[0]);
            String flowName = aClass.getCanonicalName();
            Method method = aClass.getMethod("main", String[].class);
            method.invoke(null, (Object) appParams);
            root.put("info", appArgs.toString());
        } catch (Exception e) {
            log.error(e.getMessage());
            root.put("error", e.getMessage());
        }
        return root.toString();
    }

    private String getParam(Map<String, String[]> parameters, String name) {
        final String[] values = parameters.get(name);
        if (values == null || values.length != 1) {
            throw new RuntimeException(String.format("missing %s param", name));
        }
        final String value = values[0];
        return value;
    }

    @RequestMapping("/dataflow/external/execute")
    public String externalExecuteDataflow(HttpServletRequest request) {

        JSONObject root = new JSONObject();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            final String env = getParam(parameters, ENV);
            final String pipeline = getParam(parameters, PIPELINE);
            final String jarName = getParam(parameters, JAR_NAME);
            final String className = pipeline.substring(pipeline.lastIndexOf(".") + 1);

            List<String> appArgs = parameters.keySet().stream()
                    .filter(key -> !key.equalsIgnoreCase(ENV))
                    .filter(key -> !key.equalsIgnoreCase(JAR_NAME))
                    .filter(key -> !key.equalsIgnoreCase(PIPELINE))
                    .map(key -> String.format("--%s=%s", key, parameters.get(key)[0])).collect(Collectors.toList());
            appArgs.add("--runner=DataflowRunner");
            String[] appParams = appArgs.toArray(new String[appArgs.size()]);

            //download jar
            String deployJar = String.format("%s/%s/%s/current/%s", dataflowDeploy, className, env, jarName);
            final String tempDir = System.getenv("TMPDIR");
            final File file = new File(tempDir + jarName);
            final Blob blob = storage.getCloudStorage().get(BlobId.of(dataflowBucket, deployJar));
            if (blob == null) {
                throw new RuntimeException("file not in storage " + dataflowBucket + deployJar);
            }
            final Long updateTime = blob.getUpdateTime();
            if (fileCache.isUpdated(deployJar, updateTime)) {
                LOG.info(LogUtils.prefixLog("action=download_file, file={}"), deployJar);
                storage.downloadFile(dataflowBucket, deployJar, file);
                fileCache.cacheFile(deployJar, updateTime);
            }
            DataFlowUtils.runExternalDataFlow(file.getAbsolutePath(), pipeline,
                    StringUtils.join(appParams, " \\" + System.lineSeparator() + " "));

        } catch (Exception e) {
            LOG.error(LogUtils.prefixLog("action=execute, message={}"), LogUtils.getError(e));
            root.put("error", LogUtils.getError(e));
        }
        return root.toString();
    }

    @RequestMapping("/_ah/health")
    public ResponseEntity<String> healthCheck() {
        return new ResponseEntity<>("Healthy", HttpStatus.OK);
    }

    @RequestMapping("/_ah/start")
    public ResponseEntity<String> startup() {
        LOG.error(LogUtils.prefixLog("action=startup"));
        return new ResponseEntity<>("Start", HttpStatus.OK);
    }

}
