package com.turel.beam;

import com.google.api.services.dataflow.model.Job;
import com.google.api.services.dataflow.model.ListJobsResponse;
import com.turel.utils.LogUtils;
import com.turel.utils.RandomString;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.beam.runners.dataflow.DataflowClient;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.Validation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Chaim on 09/07/2017.
 */
@Slf4j
public class DataFlowUtils {

    public enum JobStatus {
        Running,
        QuitePeriod,
        Nothing
    }

    public interface CredentialsOptions extends DataflowPipelineOptions {
        @Description("Resource name of P12 file")
        @Validation.Required
        String getP12KFileName();

        void setP12KFileName(String value);

        @Description("Service Account Id for credentials")
        @Validation.Required
        String getServiceAccountId();

        void setServiceAccountId(String value);
    }

    public interface BigqueryOptions extends CredentialsOptions {
        @Description("BigQuery project as projectId")
        @Validation.Required
        String getBQProject();

        void setBQProject(String value);

        @Description("BigQuery datasetId as datasetId")
        @Validation.Required
        String getDatasetId();

        void setDatasetId(String value);
    }

    public interface JobStatusOptions extends BigqueryOptions {
        @Description("Get Last succeeded job")
        @Default.Boolean(false)
        Boolean getLastSucceeded();

        void setLastSucceeded(Boolean value);

        @Description("List of tables to check status")
        @Validation.Required
        String getTableList();

        void setTableList(String value);

    }

    public interface JobRunOptions extends DataflowPipelineOptions {
        @Description("Quite Period from last job start. In minutes")
        @Default.Integer(5)
        Integer getQuitePeriod();

        void setQuitePeriod(Integer value);

        @Description("Dev mode from local machine")
        @Default.Boolean(false)
        Boolean getDevMode();

        void setDevMode(Boolean value);

        @Description("Max Full Pipelines running")
        @Default.Integer(10)
        Integer getMaxFullPipelines();

        void setMaxFullPipelines(Integer value);


    }

    public static Pair<JobStatus, String> isJobRunning(DataflowPipelineOptions options, String jobName, int jobIntervalinMinutes)
            throws IOException {
        DataflowClient dataflowClient = DataflowClient.create(options);
        ListJobsResponse currentJobs = dataflowClient.listJobs(null);
        final List<Job> jobs = currentJobs.getJobs();
        if (jobs != null) {
            List<Job> runningJobs = jobs.stream()
                    .filter(job -> job.getName().startsWith(jobName))
                    .filter(job -> job.getCurrentState().equals("JOB_STATE_RUNNING"))
                    .collect(Collectors.toList());
            //check if x minutes have passed sine last run
            if (runningJobs.size() == 0) {
                Optional<Job> job_state_done = jobs.stream()
                        .filter(job -> job.getName().startsWith(jobName))
                        .filter(job -> job.getCurrentState().equals("JOB_STATE_DONE"))
                        .max(Comparator
                                .comparingLong(p -> ISODateTimeFormat.dateTimeParser().parseDateTime(p.getCreateTime()).getMillis()));
                if (job_state_done.isPresent()) {
                    long millis = ISODateTimeFormat.dateTimeParser().parseDateTime(job_state_done.get().getCreateTime()).getMillis();
                    long passedMinutes = (System.currentTimeMillis() - millis) / 1000 / 60;
                    if (passedMinutes < jobIntervalinMinutes)
                        return Pair.of(JobStatus.QuitePeriod, job_state_done.get().getName());
                    else
                        return Pair.of(JobStatus.Nothing, "");
                }

            } else
                return Pair.of(JobStatus.Running, runningJobs.get(0).getName());
        }
        return Pair.of(JobStatus.Nothing, "");
    }

    public static Try<String> canJobRun(JobRunOptions options, String jobPrefix, int quitePeriod, Logger LOG) {
        return Try.of(() -> {
            Pair<JobStatus, String> jobRunning = isJobRunning(options, jobPrefix.toLowerCase(), quitePeriod);
            if (jobRunning.getKey() != JobStatus.Nothing) {
                LOG.warn(LogUtils.prefixLog("job {} {} [{}min QP - {}]"), jobPrefix, jobRunning.getKey(), options.getQuitePeriod(),
                        jobRunning.getValue());
                return jobRunning.getValue();
            }
            return "";
        });
    }

    public static String getUserName() {
        String user = System.getProperty("USER");
        if (user == null)
            user = System.getProperty("LOGNAME");
        if (user == null)
            user = System.getProperty("user.name");
        return user;
    }

    private static String normalizeName(String value) {
        return value.replace(":", "-").replace(".", "-").replace("_", "-");
    }

    public static Tuple2<String, String> buildJobName(String bqProject, String appName, String jobName, String jobLabel) {
        if (!StringUtils.isEmpty(jobLabel)) {
            jobLabel = "-" + jobLabel;
        }

        val suffix = (new RandomString(8)).nextString();
        String jobPrefix = appName + "-" + getUserName() + "-" + bqProject + jobLabel;

        jobPrefix = normalizeName(jobPrefix);
        String fullName = StringUtils.isEmpty(suffix) ? jobPrefix : jobPrefix + "-" + suffix;
        return Tuple.of(jobPrefix.toLowerCase(), fullName.toLowerCase());
    }

    private static final long FILE_BYTES_THRESHOLD = 10 * 1024 * 1024; // 10 MB

    protected static List<String> detectClassPathResourcesToStage(ClassLoader classLoader) {
        if (!(classLoader instanceof URLClassLoader)) {
            String message = String.format("Unable to use ClassLoader to detect classpath elements. "
                    + "Current ClassLoader is %s, only URLClassLoaders are supported.", classLoader);
            throw new IllegalArgumentException(message);
        }

        List<String> files = new ArrayList<String>();
        for (URL url : ((URLClassLoader) classLoader).getURLs()) {
            try {
                File file = new File(url.toURI());
                if (file.length() < FILE_BYTES_THRESHOLD) {
                    files.add(file.getAbsolutePath());
                }
            } catch (IllegalArgumentException e) {
                String message = String.format("Unable to convert url (%s) to file.", url);
                throw new IllegalArgumentException(message, e);
            } catch (URISyntaxException e) {
                String message = String.format("Unable to convert url (%s) to file.", url);
                throw new IllegalArgumentException(message, e);
            }

        }
        return files;
    }

    @Slf4j
    public static class RunBatchPipelines {
        private JobRunOptions options;
        private Function<JobRunOptions, Pipeline> createPipelineFactory;
        private Pipeline pipeline;
        private String appName;
        private String jobName;
        private String bqProject;
        private int currentCount;
        private int currentLoopCount;
        private int maxPerPipeline;
        private int totalCount;
        private int jobCount = 0;
        private int expectedJobCount;


        public RunBatchPipelines(JobRunOptions options, String jobName, String appName, String bqProject, int totalCount, Function<JobRunOptions, Pipeline> createPipeline) {
            this.options = options;
            this.bqProject = bqProject;
            this.jobName = jobName;
            this.appName = appName;
            this.totalCount = totalCount;
            this.maxPerPipeline = (int) (totalCount / Math.ceil(totalCount / (options.getMaxFullPipelines() * 1.0)));
            if (this.maxPerPipeline == 0)
                this.maxPerPipeline = 1;
            this.expectedJobCount = totalCount / maxPerPipeline;
            this.createPipelineFactory = createPipeline;
            this.pipeline = null;
        }

        public Pipeline startBatch() {
            return createPipeline();
        }

        public Pipeline checkBatch() {
            if (currentLoopCount >= maxPerPipeline) {
                runJob("************** MaxFullPipelines creating new pipeline - {} - {}/{} **************");
                pipeline = createPipeline();
                currentLoopCount = 0;
                jobCount++;
            }
            currentCount++;
            currentLoopCount++;
            return pipeline;
        }

        private void runJob(String s) {
            log.info(LogUtils.prefixLog(s), currentLoopCount, currentCount, totalCount);
            options.setJobName(DataFlowUtils.buildJobName(bqProject, appName, jobName, "sub-" + jobCount + 1)._2);
            log.info(LogUtils.prefixLog("job_name={}"), options.getJobName());
            pipeline.run();
        }

        public int checkLastBatch() {
            if (jobCount < expectedJobCount) {
                runJob("************** Last Batch MaxFullPipelines creating new pipeline - {} - {}/{} **************");
                currentCount++;
            }
            return currentCount;
        }

        private Pipeline createPipeline() {
            pipeline = createPipelineFactory.apply(options);
            return pipeline;
        }
    }

}
