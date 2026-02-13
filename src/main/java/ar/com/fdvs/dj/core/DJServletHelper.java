package ar.com.fdvs.dj.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ar.com.fdvs.dj.core.layout.LayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.output.FormatInfoRegistry;
import ar.com.fdvs.dj.output.ReportWriter;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.HtmlResourceHandler;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.ExporterConfiguration;
import net.sf.jasperreports.export.ReportExportConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.web.util.WebHtmlResourceHandler;

public class DJServletHelper {

    private static final ThreadLocal<Integer> pageTreshold = new ThreadLocal<>();

    static {
        pageTreshold.set(5);
    }

    /**
     * Sets the number of pages to keep the report in memory, if the report surpases such limit, a file will
     * be used.
     * @param treshold
     */
    public static void setPageTreshold(int treshold) {
        if (treshold >= 0) {
            pageTreshold.set(treshold);
        }
    }

    /**
     * Generates the report as HTML and setups everything for a clean response (serving images as well).
     * You have to declare JasperReport servlet in web.xml (net.sf.jasperreports.j2ee.servlets.ImageServlet)
     * <br><br>
     * Web XML must be configured somehow like this:
     * <code><br><br>
     * &lt;servlet&gt;<br>
     * &nbsp;	&lt;servlet-name&gt;image&lt;/servlet-name&gt;<br>
     * &nbsp; &lt;servlet-class&gt;net.sf.jasperreports.j2ee.servlets.ImageServlet&lt;/servlet-class&gt;<br>
     * &lt;/servlet&gt;<br>
     * <p/>
     * &lt;servlet-mapping&gt;<br>
     * &nbsp;	&lt;servlet-name&gt;image&lt;/servlet-name&gt;<br>
     * &nbsp;	&lt;url-pattern&gt;/reports/image&lt;/url-pattern&gt;<br>
     * &lt;/servlet-mapping&gt;<br>
     * </code>
     *
     * @param request
     * @param response
     * @param imageServletUrl the URI to reach net.sf.jasperreports.j2ee.servlets.ImageServlet servlet (in example it would be "reports/image")
     * @param dynamicReport
     * @param layoutManager
     * @param ds
     * @param parameters      Parameters for the DynamicReport
     * @param exporterParams  Extra parameters for JasperReport's HTML exporter (HTMLJRHtmlExporter)
     * @throws JRException
     * @throws IOException
     */
    public static void exportToHtml(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String imageServletUrl,
                                    DynamicReport dynamicReport,
                                    LayoutManager layoutManager,
                                    JRDataSource ds,
                                    Map<String, Object> parameters,
                                    ExporterConfiguration exporterConfiguration,
                                    ReportExportConfiguration reportExportConfguration) throws JRException, IOException {
        if (parameters == null) {
            parameters = new HashMap<>();
        }

        final JasperPrint _jasperPrint = DynamicJasperHelper.generateJasperPrint(dynamicReport, layoutManager, ds, parameters);
        exportToHtml(request, response, imageServletUrl, _jasperPrint, exporterConfiguration, reportExportConfguration);

    }

    public static void exportToHtml(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String imageServletUrl,
                                    JasperPrint jasperPrint,
                                    ExporterConfiguration exporterConfiguration,
                                    ReportExportConfiguration reportExportConfguration) throws JRException, IOException {
        final Exporter exporter = FormatInfoRegistry.getInstance().getExporter(DJConstants.FORMAT_HTML);
        exporter.setConfiguration(exporterConfiguration);
        exporter.setConfiguration(reportExportConfguration);

        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));

        final HtmlExporterOutputContext context = new HtmlExporterOutputContext(jasperPrint, request.getContextPath() + "/" + imageServletUrl + "?image=", response.getOutputStream(), pageTreshold.get());
        exporter.setExporterOutput(context.exporterOutput);
        exporter.exportReport();
        if (context.outputFile != null) {
            try (FileInputStream fis = new FileInputStream(context.outputFile)) {
                ReportWriter.copyStreams(fis, response.getOutputStream());
            }
        }

//        exporterParams.put(JRHtmlExporterParameter.IMAGES_URI, request.getContextPath() + imageServletUrl);

//        final ReportWriter reportWriter = ReportWriterFactory.build(pageTreshold.get()).getReportWriter(jasperPrint,
//                                                                                                        DJConstants.FORMAT_HTML,
//                                                                                                        exporterConfiguration,
//                                                                                                        reportExportConfguration);

//        final Map imagesMap = new HashMap();
//        final Exporter exporter = reportWriter.getExporter();
//        exporter.setParameters(exporterParams);
//        setupParameters(request, imageServletUrl, jasperPrint, imagesMap, exporter);

        //write generated HTML to the http-response (the one you got from the helper)
//        reportWriter.writeTo(response);
    }

    public static InputStream exportToHtml(HttpServletRequest request,
                                           String imageServletUrl,
                                           DynamicReport dynamicReport,
                                           LayoutManager layoutManager,
                                           JRDataSource ds,
                                           Map<String, Object> parameters,
                                           ExporterConfiguration exporterConfiguration,
                                           ReportExportConfiguration reportExportConfguration) throws JRException, IOException {
        if (parameters == null) {
            parameters = new HashMap<>();
        }

        final JasperPrint _jasperPrint = DynamicJasperHelper.generateJasperPrint(dynamicReport, layoutManager, ds, parameters);

        return exportToHtml(request, imageServletUrl, _jasperPrint, exporterConfiguration, reportExportConfguration);

    }

    public static InputStream exportToHtml(HttpServletRequest request,
                                           String imageServletUrl,
                                           JasperPrint jasperPrint,
                                           ExporterConfiguration exporterConfiguration,
                                           ReportExportConfiguration reportExportConfguration) throws JRException, IOException {
        final Exporter exporter = FormatInfoRegistry.getInstance().getExporter(DJConstants.FORMAT_HTML);
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setConfiguration(exporterConfiguration);
        exporter.setConfiguration(reportExportConfguration);
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final HtmlExporterOutputContext context = new HtmlExporterOutputContext(jasperPrint, request.getContextPath() + "/" + imageServletUrl + "?image=", stream, pageTreshold.get());
        exporter.setExporterOutput(context.exporterOutput);
        exporter.exportReport();
        if (context.outputFile != null) {
            return new FileInputStream(context.outputFile);
        } else {
            return new ByteArrayInputStream(stream.toByteArray());
        }

//        exporterParams.put(JRHtmlExporterParameter.IMAGES_URI, request.getContextPath() + imageServletUrl);

//        final ReportWriter reportWriter = ReportWriterFactory.build(pageTreshold.get()).getReportWriter(jasperPrint,
//                                                                                                        DJConstants.FORMAT_HTML,
//                                                                                                        exporterConfiguration,
//                                                                                                        reportExportConfguration);

//        final Map imagesMap = new HashMap();
//        final Exporter exporter = reportWriter.getExporter();
//        exporter.setParameters(exporterParams);

//        setupParameters(request, imageServletUrl, jasperPrint, imagesMap, exporter);

        //write generated HTML to the http-response (the one you got from the helper)
//        return reportWriter.write();

    }

    /* leonel
    private static void setupParameters(HttpServletRequest request, String imageServletUrl, JasperPrint jasperPrint, Map imagesMap, Exporter exporter) {
        exporter.setParameter(JRHtmlExporterParameter.IMAGES_MAP, imagesMap);
        exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, request.getContextPath() + "/" + imageServletUrl + "?image=");
        // Needed to support chart images:
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        final SimpleHtmlExporterOutput output = new SimpleHtmlExporterOutput(destFile);
        final HtmlResourceHandler imageHandler = new WebHtmlResourceHandler(request.getContextPath() + "/" + imageServletUrl + "?image=");
        output.setImageHandler(imageHandler);
        exporter.setExporterOutput(output);
        final HttpSession session = request.getSession();
        session.setAttribute(BaseHttpServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, jasperPrint);
        session.setAttribute("net.sf.jasperreports.j2ee.jasper_print", jasperPrint);
    }
    */

//    private static ExporterOutput getExporterOutput(JasperPrint jasperPrint, String imagePathPattern, OutputStream outputStream) throws IOException {
//        final SimpleHtmlExporterOutput output;
//        if (jasperPrint.getPages().size() > pageTreshold.get()) {
//            output = new SimpleHtmlExporterOutput(outputStream);
//        } else {
//            final File file = File.createTempFile("djreport", ".tmp");
//            output = new SimpleHtmlExporterOutput(file);
//        }
//        final HtmlResourceHandler imageHandler = new WebHtmlResourceHandler(imagePathPattern);
//        output.setImageHandler(imageHandler);
//        return output;
//    }

    private static class HtmlExporterOutputContext {
        private final SimpleHtmlExporterOutput exporterOutput;
        private final File outputFile;

        public HtmlExporterOutputContext(JasperPrint jasperPrint, String imagePathPattern, OutputStream outputStream, int pageTreshold) throws IOException {
            if (jasperPrint.getPages().size() > pageTreshold) {
                outputFile = null;
                exporterOutput = new SimpleHtmlExporterOutput(outputStream);
            } else {
                outputFile = File.createTempFile("djreport", ".tmp");
                outputFile.deleteOnExit();
                exporterOutput = new SimpleHtmlExporterOutput(outputFile);
            }
            final HtmlResourceHandler imageHandler = new WebHtmlResourceHandler(imagePathPattern);
            exporterOutput.setImageHandler(imageHandler);
        }
    }
}
