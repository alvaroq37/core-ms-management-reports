package core.ms.management.reports.rest;

import core.ms.management.reports.impl.ContractReportImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import net.sf.jasperreports.engine.JRException;

@Path("/rest/reports")
@Transactional
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ContractReportRest {

    @Inject
    ContractReportImpl contractReportImpl;

    @Path("/contract/pdf")
    public byte[] contractReportPDF(String reportName) throws JRException {
        return contractReportImpl.pdf(reportName);
    }

}
