package services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;

import repository.event.presenter.Presenter;
import repository.event.presenter.PresenterRepository;
import user.User;

@Service
public class SAMLUserService implements SAMLUserDetailsService {
	
	@Autowired
	PresenterRepository presenterRepository;

	@Override
	public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
		final String firstName = credential.getAttributeAsString("firstName");
		final String lastName = credential.getAttributeAsString("lastName");
		final String upn = credential.getAttributeAsString("upn");  
		final String nameId = credential.getNameID().getValue();
		
//		String nameIdSplits [] = nameId.split("@");
//		final String username = nameIdSplits[0];
		
		final String username = credential.getAttributeAsString("username");
		
		System.out.println("### User Attribute First Name: " + firstName);
		System.out.println("### User Attribute Last Name: " + lastName);
		System.out.println("### User Attribute UPN: " + upn);
		System.out.println("### User NameID: " + nameId);
		System.out.println("### UserName: " + username);
		
		
		Presenter presenter = presenterRepository.findByusername(username);
		if (presenter != null) {
			return presenter;
		}
		//Create new Presenter if it does not exist
		System.out.println("#### No User Found as Presenter... ########");
		return null;
		
//		 return new User(nameId, firstName, lastName, nameId);
	}

	
	
}