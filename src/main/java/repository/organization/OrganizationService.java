
package repository.organization;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import repository.organization.contact.Contact;
import repository.organization.contact.ContactRepository;
import repository.organization.contact.OrganizationContact;
import repository.organization.county.County;
import repository.organization.county.CountyRepository;

@Service
public class OrganizationService {
	
	@Autowired
	OrganizationRepository orgRepository;
	
	@Autowired
	ContactRepository contactRepository;
	
	@Autowired
	CountyRepository countyRepository;
	
	
	public List<Organization> getAllOrganizations(){
		return orgRepository.findAll();
	}
	
	public List<String> getOrgNames(){
		return orgRepository.findAllNames();
	}

	public List<Contact> getAllContact(){
		return contactRepository.findAll();
	}
	
	public Organization getOrgByName(String orgName) {
		Organization org = orgRepository.findByorgname(orgName);
		return org;
	}
	
	public Organization getOrgById(Long id) throws ClassNotFoundException {
		Optional<Organization> optionalCustomer = orgRepository.findById(id);
		return optionalCustomer.orElseThrow(() -> new ClassNotFoundException("There is no Organization exist with the id: " + id));
	}
	
	public void deleteOrg(Long id) {
		orgRepository.deleteById(id);
	}
	
	public void addOrg(Organization org, String countyName) { 
		County county = countyRepository.findBycountyDesc(countyName);
		org.setCounty(county);
		orgRepository.save(org);
		System.out.println("$$$$$$$$$$$$$$$$$$AFTER SAVING$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		System.out.println("County ID: " + org.getCounty().getCountyId());
		System.out.println("County Desc: " + org.getCounty().getCountyDesc());
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
	}
	
	public List<Organization> filterOrganizations(String county, String orgName){
		return orgRepository.findBycountyAndorgnameAllIgnoreCase(county, orgName);
	}
	
	public void updateOrg (Organization org, String countyName) {
		County county = countyRepository.findBycountyDesc(countyName);
		Optional<Organization> orgOptional = orgRepository.findById(org.getOrgId());

		if (orgOptional != null) {
			Organization orgOriginal = orgOptional.get();
			orgOriginal.setCounty(county);
			orgOriginal.setOrgname(org.getOrgname());
			orgOriginal.setAddress(org.getAddress());
			orgOriginal.setCity(org.getCity());
			orgOriginal.setEmail(org.getEmail());
			orgOriginal.setPhone(org.getPhone());
			orgOriginal.setZip(org.getZip());
			orgRepository.save(orgOriginal);
		}
		else {
			System.out.println("Organization Returned is Null");
		}
			
	}

	public void updateContact(Contact con) {
		Optional<Contact> optionalCon = contactRepository.findById(con.getContactId());
		
		if (optionalCon != null) {
			Contact originalCon = optionalCon.get();
			originalCon.setFirstName(con.getFirstName());
			originalCon.setLastName(con.getLastName());
			originalCon.setTitle(con.getTitle());
			originalCon.setPhone(con.getPhone());
			originalCon.setEmail(con.getEmail());
			contactRepository.save(originalCon);
		}
		else {
			System.out.println("Contact Returned is Null");
		}	
	}

	public Contact getContactById(Long id) throws ClassNotFoundException {
		Optional<Contact> optionalContact = contactRepository.findById(id);
		return optionalContact.orElseThrow(() -> new ClassNotFoundException("No Contact exist with the id: " + id));
	}
	

}
