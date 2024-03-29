package controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
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
	
	DataSource dataSource;
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	ContactRepository contactRepository;

	@Autowired
	ContactControllers (DataSource dataSource){
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@GetMapping("/all-contacts-export")
	public List<Contact> getAllContacts(){
		List<Contact> contacts = contactRepository.findAll();
		return contacts;
	}
	
	@GetMapping("/contacts-sorted-default")
	public Page<Contact> getDefaultSortedContacts(){
		final int pageNumber = 0;
		final int pageElements = 10;
		
		Pageable pageable = PageRequest.of(pageNumber, pageElements, Sort.by("firstName").ascending());
		
		return contactRepository.findAll(pageable);
	}
	
	@GetMapping("/contacts-filtered-sorted/{pageNumber}/{pageElements}/{fieldName}/{sortOrder}/{from}/{to}")
	public Page<Contact> getCustomSortedFilteredContacts(@PathVariable Integer pageNumber, @PathVariable Integer pageElements, 
			@PathVariable String fieldName, @PathVariable String sortOrder, @PathVariable String from, @PathVariable String to){
		
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
		
		LocalDateTime fromLastContactDate= null;
		LocalDateTime toLastContactDate = null;
		
		if (!from.equalsIgnoreCase("null") && !to.equalsIgnoreCase("null")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			fromLastContactDate = LocalDateTime.parse(from, formatter);
			toLastContactDate = LocalDateTime.parse(to, formatter);
		}
		
		if (fromLastContactDate != null && toLastContactDate != null) {
			contacts = contactRepository.findByLastContactDateBetween(fromLastContactDate, toLastContactDate, pageable);
		}
		else {
			contacts = contactRepository.findAll(pageable);		
		}
		return contacts;
	}
	
	@GetMapping("/all-contact-names")
	public List<Map<String, Object>> getContactNames () {
		String sql = "SELECT ContactID AS contactId, firstname AS firstName, lastname AS lastName, title, phone, altphone AS altPhone, email "
				   + "FROM Contact";
		return jdbcTemplate.queryForList(sql);
	}

}
