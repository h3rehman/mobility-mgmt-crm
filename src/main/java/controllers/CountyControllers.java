package controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import repository.organization.county.County;
import repository.organization.county.CountyRepository;

@RestController
@RequestMapping("/api")
public class CountyControllers {
	
	@Autowired
	CountyRepository countyRepository;
	
	
	@GetMapping("/counties")
	public List<County> getAllCounties(){
		List<County> counties = countyRepository.findAll();
		return counties;
	}

}
