package controllers;

import java.net.URI;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
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

import repository.organization.OrganizationService;
import repository.organization.View;
import repository.organization.contact.Contact;
import repository.organization.Organization;


@RestController
@RequestMapping("/api")
public class OrganizationControllers {
	

	DataSource dataSource;
	JdbcTemplate jdbcTemplate;
	@Autowired
	OrganizationService orgService;
	
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
	@JsonView(View.OrgDetail.class)
	@GetMapping("/organizations")
	public List<Organization> getCustomers(){
		return orgService.getAllOrganizations();
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
	public Organization getCustomerDetail(@PathVariable Long id) throws ClassNotFoundException {
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
		
		System.out.println("##############################");
		System.out.println("Org ID is: " + org.getOrgId());
		System.out.println("##############################");
		
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
		System.out.println("##############################");
		System.out.println("Org ID is: " + org.getOrgId());
		System.out.println("##############################");
		orgService.updateOrg(org, countyName);
	}

	@PutMapping("/contact")
	@ResponseStatus(HttpStatus.NO_CONTENT) // 204
	public void updateContact(@RequestBody Contact con) {
		orgService.updateContact(con);
		
	}

	@DeleteMapping("/orgContact/{orgId}/{contactId}")
	@ResponseStatus(HttpStatus.NO_CONTENT) // 204
	public void deleteOrgContact(@PathVariable Long orgId, @PathVariable Long contactId) {
		String sql = "DELETE from OrganizationContact "
				 	+ "WHERE orgid = ? "
				 	+ "AND contactid = ?";
		jdbcTemplate.update(sql, orgId, contactId);
		System.out.println("########## DisAssociated Contact Id: " + contactId);
	}
	
	@PostMapping("/orgContact/{orgId}") 
	@ResponseStatus(HttpStatus.CREATED) // 201
	public ResponseEntity<Void> createContact(@RequestBody Contact con, @PathVariable String orgId){ 
		
		System.out.println("##############################");
		System.out.println("Creating contact for Org ID is: " + orgId);
		System.out.println("##############################");
		
		orgService.associateContact(con, orgId);
		
		// Build the location URI of the new item
		 URI location = ServletUriComponentsBuilder
		 .fromCurrentRequestUri()
		 .path("/{contactId}")
		 .buildAndExpand(con.getContactId())
		 .toUri(); 

		// Explicitly create a 201 Created response
		 return ResponseEntity.created(location).build();	
	}
	

}
