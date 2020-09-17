package controllers;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.providers.ExpiringUsernameAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import repository.event.presenter.Presenter;
import user.User;

@Controller
@RequestMapping("/")
public class HomeController {
	
//	  @GetMapping
//	  public String index(ExpiringUsernameAuthenticationToken userToken, HttpSession session) {
//	    if (userToken != null) {
//	    	
//		User user = (User) userToken.getPrincipal();
//	    session.setAttribute("upn", user.getId());
//	    session.setAttribute("firstName", user.getFirstName());
//	    session.setAttribute("lastName", user.getLastName());
//	    session.setAttribute("email", user.getEmail());
//	    
//	    System.out.println("###### First Name: " + user.getFirstName() + " ##########");
//	    System.out.println("###### Email: " + user.getEmail() + " ##########");
//	    }
//	    
//		return "index";
//	  }
	  
	  @GetMapping
	  public String index(@AuthenticationPrincipal Presenter presenter, HttpSession session) {
		  if (presenter != null) {
			    session.setAttribute("upn", presenter.getUsername());
			    session.setAttribute("firstName", presenter.getName());
			    session.setAttribute("lastName", presenter.getLastName());
			    session.setAttribute("email", presenter.getEmail());
			    
			    System.out.println("###### First Name using AuthPrincipal: " + presenter.getName() + " ##########");
			    System.out.println("###### Email using AuthPrincipal: " + presenter.getEmail() + " ##########");
		  }
		  
	        return "index";
	    }

}
