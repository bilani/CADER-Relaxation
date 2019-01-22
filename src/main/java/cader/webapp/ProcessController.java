package cader.webapp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.jena.ontology.OntModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cader.services.FileQuery;
import cader.services.Cader;
import cader.services.GetOntModel;
import cader.services.QARSInitialization;
import cader.services.QARSMFSCompute;
import objects.Algorithms;

/**
 * @author blackstorm
 *
 */
@Controller
public class ProcessController {
	private static final String databaseFolder = "src/main/resources/databases";
	public ArrayList<String> allDatasets = new ArrayList<>();

	public String lastUploaded;
	/**
	 * @param name
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/process", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<byte[]> process(@RequestParam(name="selecteddb", required=false, defaultValue="100") String selecteddb,
    		@RequestParam(name="choice", required=false, defaultValue="1") String choice,
    		@RequestParam(name="algo", required=false, defaultValue="1") String choice_algo,
    		@RequestParam(name="request", required=false, defaultValue="") String myrequest,
    		Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
    	System.out.println("*************************************  ************ ALGO : " + choice_algo + " ********* **************************************************");
    	
        model.addAttribute("name", "Query Relxation" );
        Algorithms algo = choice_algo.equals("1")?(Algorithms.CADER):(choice_algo.equals("2")?(Algorithms.LBA):(Algorithms.MBA));
        
		Integer isQuery = Integer.parseInt(choice); //to determine if the request is about a file or a query
		String database = "" + selecteddb + ".owl";
		
		//export _JAVA_OPTIONS=-Xmx4096m
		
		if(isQuery != null && database != null) {
		
			if(isQuery == 2) { //it's a query
				System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++ ONE SPARQL QUERY PROCESSING ");
				
				String query = myrequest;
				File processingResult = null;
				if(algo == Algorithms.CADER){
					OntModel modelOnt = (new GetOntModel(database)).getModel();
					Cader relaxer = new Cader(query, modelOnt);
					processingResult = new File(relaxer.getFullResults());
				} else if(algo == Algorithms.LBA) {
					new QARSInitialization(database);
					QARSMFSCompute qars = new QARSMFSCompute(query, true);
					processingResult = new File(qars.getFullResults());
				} else if (algo == Algorithms.MBA) {
					new QARSInitialization(database);
					QARSMFSCompute qars = new QARSMFSCompute(query, true);
					processingResult = new File(qars.getFullResults());
				}
				if(processingResult.exists() && !processingResult.isDirectory()) { 
					System.out.println("File found & OKAY !!");
					
					byte[] array = Files.readAllBytes(processingResult.toPath());
					String fileName = "attachment; filename=Result.txt";

					HttpHeaders responseHeaders = new HttpHeaders();
					responseHeaders.set("charset", "utf-8");
					responseHeaders.setContentType(MediaType.valueOf("application/txt"));
					responseHeaders.setContentLength(array.length);
					responseHeaders.set("Content-disposition", fileName);

					return new ResponseEntity<byte[]>(array, responseHeaders, HttpStatus.OK);
					
				}else {
					System.out.println("File not Found :: error !!");
					return null;
				}

			} else { //it's a file
				
				if(lastUploaded != null && !lastUploaded.equals("")) {
					System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++ QUERIES FILE PROCESSING ");
					
					// ZIP is in Results.zip
					FileQuery fquery = new FileQuery(algo, "tmp/" + lastUploaded, database);
					File resultsZip = new File(fquery.getPathOfZipResultsFile());
					byte[] array = Files.readAllBytes(resultsZip.toPath());
					String fileName = "attachment; filename=Results.zip";

					HttpHeaders responseHeaders = new HttpHeaders();
					responseHeaders.setContentType(MediaType.valueOf("application/zip"));
					responseHeaders.setContentLength(array.length);
					responseHeaders.set("Content-disposition", fileName);
					
					//------------
					
					File f = new File("tmp/" + lastUploaded);
					if(f.delete()) {
						System.out.println("File used & deleted");
					}else {
						System.out.println("Couldn't delete the file");
					}
					
					return new ResponseEntity<byte[]>(array, responseHeaders, HttpStatus.OK);
					
				}else {
					System.out.println("No file uploaded");
					return null;
				}
			}
			
		}else {
			if(lastUploaded != null && !lastUploaded.equals("")) {
				File f = new File("tmp/" + lastUploaded);
				
				if(f.delete()) {
					System.out.println("File used & deleted");
				}else {
					System.out.println("Couldn't delete the file");
				}
			}
			//return new String("<br>" + selecteddb + "<br>"+ choice +"<br>" + myrequest);
			//return new FileSystemResource(new File(database));
		}
		return null;
    }
    
	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	public @ResponseBody
	String ping() {
		return "Server OK";
	}
	
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> uploadFiile(
	    @RequestParam("uploadfile") MultipartFile uploadfile) {
	  
	  try {
	  
	    String filename = uploadfile.getOriginalFilename();
	    String directory = "tmp";
	    String filepath = Paths.get(directory, filename).toString();
	    
	    // Save the file 
	    BufferedOutputStream stream =
	        new BufferedOutputStream(new FileOutputStream(new File(filepath)));
	    stream.write(uploadfile.getBytes());
	    stream.close();
	    
	    lastUploaded = filename;
	  }
	  catch (Exception e) {
	    System.out.println(e.getMessage());
	    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	  }
	  
	  return new ResponseEntity<>(HttpStatus.OK);
	} 
	
	@RequestMapping(value = "/uploadDataset", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> uploadDataset(
	    @RequestParam("uploadfile") MultipartFile uploadfile) {
	  
	  try {
	  
	    String filename = uploadfile.getOriginalFilename();
	    String directory = databaseFolder;
	    String filepath = Paths.get(directory, filename).toString();
	    
	    // Save the file 
	    BufferedOutputStream stream =
	        new BufferedOutputStream(new FileOutputStream(new File(filepath)));
	    stream.write(uploadfile.getBytes());
	    stream.close();

	  }
	  catch (Exception e) {
	    System.out.println(e.getMessage());
	    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	  }
	  
	  return new ResponseEntity<>(HttpStatus.OK);
	} 

	@GetMapping(value = "/allResults", produces="application/zip")
    public @ResponseBody byte[] getFile() throws IOException {
		
		File processingResult = new File("tmp/Results.zip");
		if(processingResult.exists() && !processingResult.isDirectory()) { 
	        final InputStream in = new FileInputStream(processingResult);
	        return IOUtils.toByteArray(in);
		}else {
			return null;
		}

    }
	
	@RequestMapping(value = "/allDatasets", method = RequestMethod.GET,
            produces="application/json")
	public @ResponseBody ArrayList<?> getDatasets() {
		//get your datasets list here
		File folder = new File(databaseFolder);
		File[] listOfFiles = folder.listFiles();
		allDatasets = new ArrayList<>();

		for (int i = 0; i < listOfFiles.length; i++) {
		  if (listOfFiles[i].isFile()) {
			  int ind = listOfFiles[i].getName().lastIndexOf('.');
			  if(ind > 0) {
				  String ext = listOfFiles[i].getName().substring(ind+1);
				  if(ext.equals("owl") || ext.equals("rdf")) {
					  allDatasets.add(listOfFiles[i].getName().substring(0, ind));
				  }
			  }
			 
		  }
		}
		
		return allDatasets;
	}
}