package com.turel.beam;

import com.google.api.services.bigquery.model.TableRow;
import com.turel.utils.CollectionUtils;
import com.turel.utils.LogUtils;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.beam.runners.dataflow.DataflowRunner;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.metrics.Counter;
import org.apache.beam.sdk.metrics.Metrics;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.Validation;
import org.apache.beam.sdk.transforms.Contextful;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.KV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ExportBQToXmlPipeline implements PipelineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(ExportBQToXmlPipeline.class);
    public static final String APP_NAME = "ExportBQToXm";
    static final String EXPORT_TO_XML = "ExportBQToXm";

    public interface ExportBQToXmlOptions extends DataflowPipelineOptions, DataFlowUtils.BigqueryOptions,
            DataFlowUtils.JobRunOptions {
        @Description("Extract sql")
        @Validation.Required
        String getExtractSql();

        void setExtractSql(String value);

        @Description("Export Storage location")
        @Validation.Required
        String getExportLocation();

        void setExportLocation(String value);

        @Description("Xml Root Element")
        @Validation.Required
        String getRootElement();

        void setRootElement(String value);
    }


    @Override
    public PipelineStatus runPipeline(String[] args) {
        final ExportBQToXmlOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().as(ExportBQToXmlOptions.class);
        if (Boolean.FALSE.equals(options.getDevMode()))
            options.setRunner(DataflowRunner.class);
        return runPipeline(EXPORT_TO_XML, LOG, APP_NAME, options,
                options.getBQProject(), "", () -> {
                    executePipeline(options);
                });

    }

    protected static DoFn<TableRow, KV<String, String>> mapRowToXml() {
        Counter errorCounter = Metrics.counter(ExportBQToXmlPipeline.class, "errors");
        Counter processedCounter = Metrics.counter(ExportBQToXmlPipeline.class, "processed");

        return new DoFn<TableRow, KV<String, String>>() {
            private String nodeName(final String name) {
                val first = Character.toUpperCase(name.charAt(0));
                return first + name.substring(1);

            }

            private void rowToElement(final Map<String, Object> row, final Document doc, final org.w3c.dom.Element rootElement) {
                row.keySet().forEach(e -> {
                    val o = row.get(e);
                    if (o instanceof Map) {
                        org.w3c.dom.Element element = doc.createElement(nodeName(e));
                        rootElement.appendChild(element);
                        rowToElement((Map<String, Object>) o, doc, element);
                    } else if (o instanceof List) {
                        val list = (List) o;
                        list.forEach(i -> {
                            if (i instanceof Map) {
                                rowToElement((Map<String, Object>) i, doc, rootElement);
                            }
                        });
                    } else {
                        val element = doc.createElement(nodeName(e));
                        val textNode = doc.createTextNode(o.toString());
                        element.appendChild(textNode);
                        rootElement.appendChild(element);
                    }
                });
            }

            DocumentBuilder docBuilder;
            Transformer transformer;

            @StartBundle
            @SneakyThrows
            public void init() {
                val docFactory = DocumentBuilderFactory.newInstance();
                docBuilder = docFactory.newDocumentBuilder();
                val tf = TransformerFactory.newInstance();
                transformer = tf.newTransformer();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            }

            @ProcessElement
            public void processElement(ProcessContext c) {
                try {
                    val doc = docBuilder.newDocument();
                    val options = c.getPipelineOptions().as(ExportBQToXmlOptions.class);
                    val rootElement = doc.createElement(options.getRootElement());
                    doc.appendChild(rootElement);
                    final Map<String, Object> element = c.element();
                    final String businessId = (String) CollectionUtils.getRecursiveData(element, "request.businessId");
                    rowToElement(element, doc, rootElement);

                    val writer = new StringWriter();
                    transformer.transform(new DOMSource(doc), new StreamResult(writer));
                    val output = writer.getBuffer().toString().replaceAll("\n|\r", "");
                    LOG.info(LogUtils.prefixLog("action=xml, businessId={}"), businessId);
                    c.output(KV.of(businessId, output));
                    processedCounter.inc();
                } catch (Exception e) {
                    errorCounter.inc();
                    LOG.error(LogUtils.prefixLog(e.getMessage()));
                    c.output(KV.of("error", e.getMessage()));
                }
            }
        };
    }

    @SneakyThrows
    private void executePipeline(ExportBQToXmlOptions options) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        final Pipeline pipeline = Pipeline.create(options);
        pipeline.apply("read data",
                BigQueryIO.readTableRows()
                        .fromQuery(options.getExtractSql())
                        .usingStandardSql())
                .apply("table data map", ParDo.of(mapRowToXml()))
                .apply("to xml", FileIO.<String, KV<String, String>>writeDynamic()
                        .to(options.getExportLocation() + "/" + now.format(formatter))
                        .by(KV::getKey)
                        .via(Contextful.fn(KV::getValue), TextIO.sink())
                        .withDestinationCoder(StringUtf8Coder.of())
                        .withNumShards(1)
                        .withNaming(k -> (window, pane, numShards, shardIndex, compression) -> k + ".xml"));
        pipeline.run();
    }


    public static void main(String[] args) {
        (new ExportBQToXmlPipeline()).runPipeline(args);
    }

}
