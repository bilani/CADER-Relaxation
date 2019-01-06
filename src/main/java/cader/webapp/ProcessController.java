package cader.webapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.ontology.OntModel;
import org.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonView;

import org.apache.jena.ontology.OntModel;

import cader.services.QueryRelaxer;
import cader.services.SetDatabase;

/**
 * @author blackstorm
 *
 */
@Controller
public class ProcessController {

	/**
	 * @param name
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/process", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<byte[]> process(@RequestParam(name="selecteddb", required=false, defaultValue="100") String selecteddb,
    		@RequestParam(name="choice", required=false, defaultValue="1") String choice,
    		@RequestParam(name="request", required=false, defaultValue="") String myrequest,
    		Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        model.addAttribute("name", "Query Relxation" );
        
		Integer isQuery = Integer.parseInt(choice); //to determine if the request is about a file or a querry
		String database = selecteddb;
		//myrequest = "SELECT * WHERE { ?Y13 <http://swat.cse.lehigh.edu/onto/univ-bench.owl#doctoralDegreeFrom> ?Y14 . ?Y14 <http://swat.cse.lehigh.edu/onto/univ-bench.owl#hasAlumnus> ?Y15 . ?Y15 <http://swat.cse.lehigh.edu/onto/univ-bench.owl#title> 'Dr' }";
		
		//if(isQuery != null && database != null) {
			OntModel modelOnt = (new SetDatabase(database)).getModel();
		
			//if(isQuery == 2) { //it's a query
				String query = myrequest;
				QueryRelaxer relaxer = new QueryRelaxer(query, modelOnt);
				File processingResult = new File(relaxer.getFullResults());
				
				try {
					 byte[] array = Files.readAllBytes(processingResult.toPath());
					 String fileName = "attachment; filename=Result.pdf";

					 HttpHeaders responseHeaders = new HttpHeaders();
					 responseHeaders.set("charset", "utf-8");
					 responseHeaders.setContentType(MediaType.valueOf("application/pdf"));
					 responseHeaders.setContentLength(array.length);
					 responseHeaders.set("Content-disposition", fileName);

					 return new ResponseEntity<byte[]>(array, responseHeaders, HttpStatus.OK);
				}catch(Exception e) {
					System.out.println(e.getMessage());
					return null;
				}
				//return new FileSystemResource(processingResult); 
				//return selecteddb;
			//} else { //it's a file
				//return new FileSystemResource(new File(database));
			//}
			
		//}else {
			//return new String("<br>" + selecteddb + "<br>"+ choice +"<br>" + myrequest);
			//return new FileSystemResource(new File(database));
		//}
    }
	
	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	public @ResponseBody
	String ping() {
		return "Server OK";
	}

}