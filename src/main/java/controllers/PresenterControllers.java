package controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import repository.event.presenter.Presenter;
import repository.event.presenter.PresenterRepository;

@RestController
@RequestMapping("/api")
public class PresenterControllers {
	
	@Autowired
	PresenterRepository presenterRepository;
	
	
	@GetMapping("/activePresenters")
	public List<Presenter> getAllPresenters(){
		return presenterRepository.findByIsActiveTrue();
	}

}
