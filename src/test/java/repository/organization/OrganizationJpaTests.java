package repository.organization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import application.Application;
import repository.organization.Organization;
import repository.organization.OrganizationRepository;
import repository.organization.contact.Contact;
import repository.organization.contact.ContactRepository;
import repository.organization.contact.OrganizationContact;
import repository.organization.county.County;
import repository.organization.county.CountyRepository;

/**
 * CRUD Tests for Customer Entity (Associated Entity: Contact) 
 */

@EnableAutoConfiguration
@SpringBootTest(classes = Application.class)
@ExtendWith(SpringExtension.class)
@Transactional
public class OrganizationJpaTests {

	@Autowired
	OrganizationRepository orgRepository;
	
	@Autowired
	ContactRepository contactRepository;
	
	@Autowired
	CountyRepository countyRepository;
	
	@Test
//	@Disabled
	public void testReadCustomer() {
		
		assertNotNull(orgRepository.count());
		assertNotNull(orgRepository.findAll());
		assertEquals(9, orgRepository.count());
//		assertTrue(orgRepository.existsById(1L));
//		assertEquals("Google", orgRepository.findById(1L).get().orgname);
	}
	
//	@Test
	@Disabled
	public void testCreateCustomer() {
		Organization org = new Organization();
		org.setOrgname("Splunk");
		org.setPhone("984566321");
		org.setAddress("928 S Ellen St.");
		org.setCity("San Francisco");
		org.setEmail("info@splunk.com");
		org.setZip("98750");
	
		Contact contact1 = new Contact();
		contact1.setFirstName("Merry");
		contact1.setLastName("Richard");
		contact1.setEmail("merry@splunk.com");
		contact1.setPhone("1239874566");
		
		Contact contact2 = new Contact();
		contact2.setFirstName("Melinda");
		contact2.setLastName("Nickel");
		contact2.setEmail("melinda@splunk.com");
		contact2.setPhone("1239874588");
		
		OrganizationContact customerContact1 = new OrganizationContact();
		customerContact1.setOrganization(org);
		customerContact1.setContact(contact1);
		
		OrganizationContact customerContact2 = new OrganizationContact();
		customerContact2.setOrganization(org);
		customerContact2.setContact(contact2);
		
		List<OrganizationContact> customerContacts = new ArrayList<OrganizationContact>();
		customerContacts.add(customerContact1);
		customerContacts.add(customerContact2);
		
//		org.setOrgContacts(customerContacts);
		
		County county = countyRepository.findBycountyDesc("Will");
		org.setCounty(county);
		
		contactRepository.save(contact1);
		contactRepository.save(contact2);
		orgRepository.save(org);
		
		assertNotNull(org.getOrgId());
		System.out.println("######### GOT ORG ID: " + org.getOrgId());
		assertNotNull(contact1.getContactId());
		assertNotNull(contact2.getContactId());
		assertNotNull(customerContact1.getCustomerContactKey().getContactId());
		assertNotNull(customerContact1.getCustomerContactKey().getOrgId());
		assertNotNull(customerContact2.getCustomerContactKey().getContactId());
		assertNotNull(customerContact1.getCustomerContactKey().getOrgId());
		assertEquals(customerContact1.getCustomerContactKey().getContactId().longValue(), contact1.getContactId().longValue()); 
		assertEquals(customerContact1.getCustomerContactKey().getOrgId().longValue(), org.getOrgId().longValue());
		
		assertNotNull(orgRepository.findByorgname("Splunk"));
		assertEquals("Splunk", orgRepository.findByorgname("Splunk").getOrgname());
//		assertEquals(2, orgRepository.findByorgname("Splunk").getOrgContacts().size());
	}
	
//	@Test
	@Disabled
	public void testUpdateCustomerContact() {
		assertEquals("Smith", contactRepository.findByfirstName("Sally").getLastName());
		
//		Organization google = orgRepository.findByorgname("Google");
//		List<OrganizationContact> customerContacts = google.getOrgContacts();
//		for (OrganizationContact co : customerContacts) {
//			if (co.contact.getFirstName().equals("Sally")) {
//				co.contact.setLastName("Dion");
//				break;
//			}
//		}
		assertEquals("Dion", contactRepository.findByfirstName("Sally").getLastName());
		assertFalse(contactRepository.findByfirstName("Sally").getLastName().equals("Smith"));
	}
	
	@Test
//	@Disabled
	public void testAddCustomerAndCounty() throws ClassNotFoundException {
		Organization org = new Organization();
		org.setOrgname("Splunk");
		org.setPhone("984566321");
		org.setAddress("928 S Ellen St.");
		org.setCity("San Francisco");
		org.setEmail("info@splunk.com");
		org.setZip("98750");
		
		County county = countyRepository.findBycountyDesc("Lake");
		org.setCounty(county);
		orgRepository.save(org);
		
		System.out.println("###############");
		System.out.println("Organization Id of Splunk is: " + org.getOrgId());
		System.out.println("###############");
		
		assertEquals("Lake", org.getCounty().getCountyDesc());
		
		Organization org1 = new Organization();
		org1.setOrgname("Massimo");
		org1.setPhone("984566321");
		org1.setAddress("928 S Glen St.");
		org1.setCity("San Diego");
		org1.setEmail("info@adobe.com");
		org1.setZip("98760");
		
		Optional<County> county2 = countyRepository.findById(4L);
		County county2b = county2.orElseThrow(() -> new ClassNotFoundException("There is no County associated with the Provided County Id"));
		org1.setCounty(county2b);
		assertEquals("Lake", org1.getCounty().getCountyDesc());
		
	}
	
	@Test
	public void testUpdateOrg() {
		Organization org = new Organization();
		org.setOrgname("MGM Records");
		org.setPhone("984566321");
		org.setCity("Chicago");
		org.setEmail("info@mgm.com");
		org.setZip("60604");
		
		County county = countyRepository.findBycountyDesc("DuPage");
		org.setCounty(county);
		orgRepository.save(org);
		
		System.out.println("#####  New Org ID: " + org.getOrgId());
		System.out.println("#####  New Org Name: " + org.getOrgId());
		
		Organization updatedOrg = orgRepository.findByorgname("MGM Records");
	
		assertNull(updatedOrg.getAddress());
		assertEquals(org.getOrgId().longValue(), updatedOrg.getOrgId().longValue());
		updatedOrg.setAddress("987 W Jackson");
		orgRepository.save(updatedOrg);
		System.out.println("##### Existing Org ID: " + updatedOrg.getOrgId());
		assertNotNull(updatedOrg.getAddress());
		assertEquals("987 W Jackson", updatedOrg.getAddress());
		
	}

//	@Test
	@Disabled
	public void testDeleteCustomer() {
		Organization netflix = orgRepository.findByorgname("Netflix");
		orgRepository.delete(netflix);
		assertNull(orgRepository.findByorgname("Netflix"));
	}
}
