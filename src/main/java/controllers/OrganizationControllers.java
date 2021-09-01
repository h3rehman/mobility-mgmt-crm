package controllers;

import java.net.URI;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonView;

import repository.organization.View;
import repository.organization.contact.Contact;
import repository.organization.contact.ContactRepository;
import repository.organization.contact.OrganizationContactRepository;
import repository.organization.county.County;
import repository.organization.county.CountyRepository;
import repository.status.Status;
import repository.status.StatusRepository;
import services.OrganizationService;
import repository.event.Event;
import repository.event.Eventtype;
import repository.event.presenter.Presenter;
import repository.organization.Organization;
import repository.organization.OrganizationRepository;


@RestController
@RequestMapping("/api")
public class OrganizationControllers {
	

	DataSource dataSource;
	JdbcTemplate jdbcTemplate;
	@Autowired
	OrganizationService orgService;
	
	@Autowired
	OrganizationRepository orgRepository;
	
	@Autowired
	OrganizationContactRepository orgContactRepository;
	
	@Autowired
	CountyRepository countyRepository;
	
	@Autowired
	StatusRepository statusRepository;
	
	@Autowired
	ContactRepository contactRepository;
	
	@Autowired
	OrganizationControllers (DataSource dataSource){
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@GetMapping("/accounts")
	public String accounts() throws SQLException{
		System.out.println("This is the Accounts Controller....");
		
		String welcome = "<h3> Welcome to the Accounts Page </h3>";
		String sql = "SELECT count(*) FROM AnswerType";
		
		Integer num = jdbcTemplate.queryForObject(sql, Integer.class); 
		String events = num.toString();
		return welcome + "<br><br>" + events;
	}
	
	@GetMapping("/customers")
	public List<Map<String, Object>> getAllCustomers() throws SQLException{
		System.out.println("This is the Customers Controller....");
		
		String sql = "SELECT OrgName "
					+ "FROM Customer";
		
		return jdbcTemplate.queryForList(sql); 	
	}
	
	@GetMapping("/customer-detail")
	@Transactional
	public List<Map<String, Object>> customerDetail() throws SQLException {
		System.out.println("This is the Customer Detail Controller...");
		
		String sqli = "SELECT CustomerId "    //fetching id
				+ "FROM Customer "
				+ "WHERE OrgName = 'Google'";

		String sql = "SELECT FirstName, LastName " //Getting contacts of a customer with the above Id
					+ "FROM Contact con "
					+ "INNER JOIN CustomerContact cuscon on con.ContactId = cuscon.ContactId "
					+ "WHERE cuscon.CustomerId= ?";
		
		Integer customerId = jdbcTemplate.queryForObject(sqli, Integer.class);
		
		return jdbcTemplate.queryForList(sql, customerId);
	}
	
	@GetMapping("/upcoming-appointments")
//	@Transactional
	public List<Map<String, Object>> getFutureAppointments() throws SQLException {
		String sql = "SELECT * "
					+ "FROM Event "
					+ "WHERE StartDatetime >= ?";
				
		return jdbcTemplate.queryForList(sql, LocalDateTime.now());
	}
	@JsonView(View.OrgSummary.class)
	@GetMapping("/organizations")
	public List<Organization> getAllOrgs(){
		return orgService.getAllOrganizations();
	}
	
	
	@GetMapping("/orgs-sorted-default")
	Page<Organization> getOrgsDefault () {
		final int pageNumber = 0;
		final int pageElements = 10;
		Pageable pageable = PageRequest.
				of(pageNumber, pageElements, Sort.by("orgname").ascending());
		Page<Organization> pagedOrgs = orgRepository.findAll(pageable);
		return pagedOrgs;
	}
	
	@GetMapping("/orgs-filtered-sorted/{pageNumber}/{pageElements}/{fieldName}/{sortOrder}")
	Page<Organization> getFilteredSortedOrgs(@PathVariable Integer pageNumber, @PathVariable Integer pageElements,
			@PathVariable String fieldName, @PathVariable String sortOrder,
			@RequestParam(value="county", required=false) String [] counties,
			@RequestParam(value="status", required=false) String [] statuses){
		
		Page<Organization> orgs = null;
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
			pageable = PageRequest.of(pageNumber, pageElements, Sort.by("orgname").ascending());
		}
		
		
		List<County> confirmedCounties = new ArrayList<County>();
		if (counties != null) {
			for (int i=0; i<counties.length; i++) {
				County ct = countyRepository.findBycountyDesc(counties[i]);
				if (ct != null) {
					confirmedCounties.add(ct);
				}
			}
		}
		
		List<Status> confirmedStatuses = new ArrayList<Status>();
		if (statuses != null) {
			for (int i=0; i<statuses.length; i++) {
				Status st = statusRepository.findBystatusDesc(statuses[i]);
				if (st != null) {
					confirmedStatuses.add(st);
				}
			}
		}
		
		//Option 1:
		if (confirmedCounties.size() > 0) {
			//Option 1a:
			if (confirmedStatuses.size() > 0) {
				orgs = orgRepository.findByCountyInAndLastStatusIn(confirmedCounties, confirmedStatuses, pageable);
			}
			//Option 1b:
			else {
				orgs = orgRepository.findAllBycountyIn(confirmedCounties, pageable);
			}
		}
		//Option 2:
		else if (confirmedStatuses.size() > 0) {
			orgs = orgRepository.findAllBylastStatusIn(confirmedStatuses, pageable);
		}
		//Option 3:
		else {
			orgs = orgRepository.findAll(pageable);
		}
		
		return orgs;		
	}
	
	@GetMapping("/contacts")
	public List<Contact> getContacts(){
		return orgService.getAllContact();
	}
	
	@GetMapping("/contact/{id}")
	public Contact getContact(@PathVariable Long id) throws ClassNotFoundException{
		return orgService.getContactById(id);
	}
	
	
	@GetMapping("/org-names")
	public List<String> getCustomerNames(){
		return orgService.getOrgNames();
	}
	
	@JsonView(View.OrgDetail.class)
	@GetMapping("/organizations/{id}")
	public Organization getOrgDetail(@PathVariable Long id) throws ClassNotFoundException {
		return orgService.getOrgById(id);
	}
	
	@GetMapping("/organization/{orgName}")   
	public Organization detailOrg(@PathVariable String orgName){ 
		return orgService.getOrgByName(orgName);
	}
	
	@GetMapping("/orgs")
	public List<Organization> getOrgByFilter(@RequestParam(required = false) String county, 
			@RequestParam(required = false) String orgName){
		return orgService.filterOrganizations(county, orgName);
	}
	
	@PostMapping("/organization/{countyName}")   ///{countyName}
	@ResponseStatus(HttpStatus.CREATED) // 201
	public ResponseEntity<Void> createOrg(@RequestBody Organization org, @PathVariable String countyName){ 
		
		orgService.addOrg(org, countyName);
 		
		// Build the location URI of the new item
		 URI location = ServletUriComponentsBuilder
		 .fromCurrentRequestUri()
		 .path("/{OrgId}")
		 .buildAndExpand(org.getOrgId())
		 .toUri(); 

		// Explicitly create a 201 Created response
		 return ResponseEntity.created(location).build();	
	}
	

	@PutMapping("/organization/{countyName}")
	@ResponseStatus(HttpStatus.NO_CONTENT) // 204
	public void updateOrg(@RequestBody Organization org, @PathVariable String countyName) {
		orgService.updateOrg(org, countyName);
	}
	
	@PostMapping("/create-update-contact/{orgId}") 
	@ResponseStatus(HttpStatus.CREATED) // 201
	public ResponseEntity<Void> createContact(@RequestBody Contact con, @PathVariable String orgId,
			@AuthenticationPrincipal Presenter user){ 
		if (user != null) {
			orgService.createContact(con, orgId, user);
			
			// Build the location URI of the new item
			URI location = ServletUriComponentsBuilder
					.fromCurrentRequestUri()
					.path("/{contactId}")
					.buildAndExpand(con.getContactId())
					.toUri(); 
			
			// Explicitly create a 201 Created response
			return ResponseEntity.created(location).build();	
		}
		System.out.println("Returned null Presenter.. No Contact created.");
		return null;
	}

	@PutMapping("/create-update-contact/{orgId}")
	@ResponseStatus(HttpStatus.NO_CONTENT) // 204
	public void updateContact(@RequestBody Contact con, @PathVariable String orgId, @AuthenticationPrincipal Presenter user) {
		if (user != null) {
			orgService.updateContact(con, user, orgId);
		}
		System.out.println("User identity cannot be confirmed... Cannot update contact.");
	}

	@DeleteMapping("/orgContact/{orgId}/{contactId}")
	@ResponseStatus(HttpStatus.NO_CONTENT) // 204
	public void deleteOrgContact(@PathVariable Long orgId, @PathVariable Long contactId) {
		String sql = "DELETE from OrganizationContact "
				 	+ "WHERE orgid = ? "
				 	+ "AND contactid = ?";
		jdbcTemplate.update(sql, orgId, contactId);
	}
	
	@GetMapping("/allorgnames")
	public List<Map<String, Object>> getAllOrgNames() throws SQLException {
		String sql = "SELECT OrgID, orgname "
					+ "FROM Organization ";
				
		return jdbcTemplate.queryForList(sql);
	}
	
	@DeleteMapping("/removeOrg/{eventId}/{orgId}")
	@ResponseStatus(HttpStatus.NO_CONTENT) // 204
	public void deleteEventOrg(@PathVariable Long eventId, @PathVariable Long orgId) {
		String sql = "DELETE from eventorganization "
				 	+ "WHERE eventId = ? "
				 	+ "AND OrgID = ?";
		jdbcTemplate.update(sql, eventId, orgId);
	}
	
	@GetMapping("/orgContacts/{orgId}")
	public List<Contact> getOrgContacts(@PathVariable Long orgId){
		if(orgId !=-1) {			
			Optional<Organization> optionalOrg = orgRepository.findById(orgId);
			if (optionalOrg != null) {
				Organization org = optionalOrg.get();
				List<Contact> orgContacts = orgContactRepository.findByOrganization(org);
				return orgContacts;
			}
			else {
				return null;
			}
		}
		return null;
	}
	
	@PutMapping("/associate-org-contact/{orgId}/{contactId}")
	@ResponseStatus(HttpStatus.NO_CONTENT) // 204
	public void associateOrgContact(@PathVariable Long orgId, @PathVariable Long contactId) {
		Optional<Contact> optContact = contactRepository.findById(contactId);
		if (optContact != null) {
			Contact contact = optContact.get();
			orgService.associateOrgContact(orgId, contact);
		}
		else {
			System.out.println("Contact for association with Org. is null.....");
		}
	}
}
