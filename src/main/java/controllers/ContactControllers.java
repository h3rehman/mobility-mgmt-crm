package controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import repository.organization.Organization;
import repository.organization.contact.Contact;
import repository.organization.contact.ContactRepository;

@RestController
@RequestMapping("/api")
public class ContactControllers {
	
	@Autowired
	ContactRepository contactRepository;
	

	@GetMapping("/contacts-sorted-default")
	public Page<Contact> getDefaultSortedContacts(){
		final int pageNumber = 0;
		final int pageElements = 10;
		
		Pageable pageable = PageRequest.of(pageNumber, pageElements, Sort.by("firstName").ascending());
		
		return contactRepository.findAll(pageable);
	}
	
	@GetMapping("/contacts-filtered-sorted/{pageNumber}/{pageElements}/{fieldName}/{sortOrder}")
	public Page<Contact> getCustomSortedFilteredContacts(@PathVariable Integer pageNumber, @PathVariable Integer pageElements, 
			@PathVariable String fieldName, @PathVariable String sortOrder){
		
		Page<Contact> contacts = null;
		Pageable pageable = null;
		if (!fieldName.equalsIgnoreCase("null")) {
			if (sortOrder.equalsIgnoreCase("asce") || sortOrder.equalsIgnoreCase("null") ) {
				pageable = PageRequest.of(pageNumber, pageElements, Sort.by(fieldName).ascending());
			}
			else {
				pageable = PageRequest.of(pageNumber, pageElements, Sort.by(fieldName).descending());
			}
		}
		else { //default sort 
			pageable = PageRequest.of(pageNumber, pageElements, Sort.by("firstName").ascending());
		}
		
		contacts = contactRepository.findAll(pageable);		
		return contacts;
	}

}
