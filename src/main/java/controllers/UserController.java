package controllers;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import repository.event.presenter.Presenter;
import repository.event.presenter.PresenterRepository;

@RestController
@RequestMapping("/api")
public class UserController {
	
	@Autowired
	PresenterRepository presenterRepository;
	
	@Value("${onelogin.sp.logoutUrl}")
    private String logoutUrl;
	
	@Value("${onelogin.sp.port}")
	private String port;
	
	@GetMapping("/user")
	public ResponseEntity<?> getUser(@AuthenticationPrincipal Presenter user) {
        if (user == null) {
            return new ResponseEntity<>("", HttpStatus.OK);
        } else {
            return ResponseEntity.ok().body(getUserAttributes(user));
        }
    }
	
	 @PostMapping("/logout")
	 public ResponseEntity<?> logout(HttpServletRequest request) {
	        // send logout URL to client so they can initiate logout
	        Map<String, String> logoutDetails = new HashMap<>();
	        logoutDetails.put("port", port);
	        logoutDetails.put("logoutUrl", logoutUrl);
	        request.getSession(false).invalidate();
	        return ResponseEntity.ok().body(logoutDetails);
	    }
	
	//Get all user attributes in a map
	private static Map<String, Object> getUserAttributes(Presenter user){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("presenterId", user.getPresenterId());
		map.put("firstName", user.getName());
		map.put("lastName", user.getLastName());
		map.put("email", user.getEmail());
		map.put("username", user.getUsername());
		return map;
	}

}
