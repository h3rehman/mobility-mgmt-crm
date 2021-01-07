package controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import repository.status.Status;
import repository.status.StatusRepository;

@RestController
@RequestMapping("/api")
public class StatusControllers {

	@Autowired	
	StatusRepository statusRepository;
	
	@GetMapping("/orgStatusTypes")
	List<Status> getOrgStatusTypes (){
		String orgType = "O";
		return statusRepository.findBystatusType(orgType);
	}
}
