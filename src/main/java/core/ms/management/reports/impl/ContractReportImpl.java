package core.ms.management.reports.impl;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.w3c.dom.Document;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.SimpleReportContext;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.fill.JRFiller;
import net.sf.jasperreports.engine.fill.SimpleJasperReportSource;
import net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRXmlUtils;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.repo.JasperDesignCache;
import net.sf.jasperreports.repo.SimpleRepositoryResourceContext;

@ApplicationScoped
public class ContractReportImpl {
    private static final String TEST_REPORT_NAME = "CustomersReport";
    private static final String TEST_SUB_REPORT_NAME = "OrdersReport";

    public byte[] pdf(String reportName) throws JRException {
        long start = System.currentTimeMillis();
        JasperPrint jasperPrint = fill(reportName);
        JRPdfExporter exporter = new JRPdfExporter(DefaultJasperReportsContext.getInstance());
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.exportReport();
        Log.info("PDF creation time : " + (System.currentTimeMillis() - start));
        return outputStream.toByteArray();
    }

    private JasperPrint fill(String reportName) throws JRException {
        long start = System.currentTimeMillis();
        JasperReport mainReport = compile(reportName);
        JasperReport subReport = compile(TEST_SUB_REPORT_NAME);
        ReportContext reportContext = new SimpleReportContext();
        Map<String, Object> params = new HashMap<>();
        Document document = JRXmlUtils.parse(JRLoader.getLocationInputStream("northwind.xml"));
        params.put(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, document);
        params.put(JRXPathQueryExecuterFactory.XML_DATE_PATTERN, "yyyy-MM-dd");
        params.put(JRXPathQueryExecuterFactory.XML_NUMBER_PATTERN, "#,##0.##");
        params.put(JRXPathQueryExecuterFactory.XML_LOCALE, Locale.ENGLISH);
        params.put(JRParameter.REPORT_LOCALE, Locale.US);
        params.put(JRParameter.REPORT_CONTEXT, reportContext);
        JasperDesignCache.getInstance(DefaultJasperReportsContext.getInstance(), reportContext).set("OrdersReport.jasper",
                subReport);
        JasperPrint jasperPrint = JRFiller.fill(DefaultJasperReportsContext.getInstance(),
                SimpleJasperReportSource.from(mainReport, null, new SimpleRepositoryResourceContext()),
                params);
        Log.info("Filling time : " + (System.currentTimeMillis() - start));
        return jasperPrint;
    }
    private JasperReport compile(String reportName) throws JRException {
        long start = System.currentTimeMillis();
        JasperDesign jasperDesign = JRXmlLoader
                .load(Thread.currentThread().getContextClassLoader().getResourceAsStream(reportName + ".jrxml"));
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        Log.info("Compilation time : " + (System.currentTimeMillis() - start));
        return jasperReport;
    }
}
