package user;

public class User {
	private String userPrincipalName;
	private String firstName;
	private String lastName;
	private String email;

	public User(String upn, String fn, String ln, String email) {
		this.userPrincipalName = upn;
		this.firstName = fn;
		this.lastName = ln;
		this.email = email;
	}

	public String getId() {
		return userPrincipalName;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public String getEmail() {
		return email;
	}
}
