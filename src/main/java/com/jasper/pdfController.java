package com.jasper;

import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
public class pdfController {

	@Autowired
    ApplicationContext context;
    	
	@GetMapping(path = "pdf/{jrxml}")
	@ResponseBody
    public void getPdf(@PathVariable String jrxml , HttpServletResponse response, @RequestParam String name, @RequestParam String documentNumber, @RequestParam String dateTime) throws Exception {
		//Get JRXML template from resources folder
		Resource resource = context.getResource("classpath:jasperreports/"+jrxml+".jrxml");
        //Compile to jasperReport
        InputStream inputStream = resource.getInputStream();
        JasperReport report=JasperCompileManager.compileReport(inputStream);		
		//Parameters Set
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("documentNumber", documentNumber);
        params.put("year", dateTime.substring(0, 3));
        params.put("month", dateTime.substring(3, 5));
        params.put("date", dateTime.substring(5, 7));
        params.put("hour", dateTime.substring(7, 9));
        params.put("minute", dateTime.substring(9, 11));

        //Data source Set
        JRDataSource dataSource = new JREmptyDataSource();
        //Make jasperPrint
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, params, dataSource);
        //Media Type
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        //Export PDF Stream
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }
}
