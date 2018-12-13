package cader.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class FileUploader {
	private static String UPLOADED_FOLDER = "/tmp/";

	@GetMapping("/insertQuery/{param}")
	public String insertQuery(@PathVariable("param") String param) {

		return param;
	}

	@PostMapping(value="/postFile", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> postFile(@RequestParam("file") MultipartFile file) throws IllegalStateException, IOException{
		String fileName = file.getOriginalFilename();
		File tempDirectory = FileUtils.getTempDirectory();
		File fileTraiter = new File(tempDirectory.getAbsolutePath()+File.separator+fileName);
		file.transferTo(fileTraiter);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/upload") // //new annotation since 4.3
	public String singleFileUpload(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {

		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
			return "redirect:uploadStatus";
		}

		try {

			// Get the file and save it somewhere
			byte[] bytes = file.getBytes();
			Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
			Files.write(path, bytes);

			redirectAttributes.addFlashAttribute("message",
					"You successfully uploaded '" + file.getOriginalFilename() + "'");

		} catch (IOException e) {
			e.printStackTrace();
		}

		return "redirect:/uploadStatus";
	}
}