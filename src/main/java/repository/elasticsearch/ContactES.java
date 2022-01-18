package repository.elasticsearch;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName= "contacts")
public class ContactES {

	@org.springframework.data.annotation.Id
	private String id;
	
	@Field(type = FieldType.Text)
	private String firstName;
	
	@Field(type = FieldType.Text)
	private String lastName;
	
	@Field(type = FieldType.Text)
	private String title;
	
	@Field(type = FieldType.Text)
	private String email;
	
	@Field(type = FieldType.Text)
	private String phone;
	
	@Field(type = FieldType.Text)
	private String altPhone;
	
	public ContactES () {}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAltPhone() {
		return altPhone;
	}

	public void setAltPhone(String altPhone) {
		this.altPhone = altPhone;
	}
	
	
}
