package controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectControllers {
	
	  @Value("${frontend.host}")
	  private String host;
	  
	  @Value("${frontend.protocol}")
	  private String protocol;
	
	  @GetMapping("/")
	  public String redirectToHome() {
	        return "redirect:" + protocol + "://" + host;
	    }

}
