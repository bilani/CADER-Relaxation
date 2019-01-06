package cader.webapp;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpStatus;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MyErrorController implements ErrorController  {
	
	private static final String PATH = "/error";

	 @RequestMapping(value = PATH)
	 public String error() {
		 return "error";
	 }

	 @Override
	 public String getErrorPath() {
		 return PATH;
	 }
	 
}