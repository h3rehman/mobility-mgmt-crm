package repository.elasticsearch;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "organizations")
public class OrganizationES {
	
	@org.springframework.data.annotation.Id
	private String id;
	
	@Field(type = FieldType.Text)
	private String orgname;
	
	@Field(type = FieldType.Text)
	private String address;
	
	@Field(type = FieldType.Text)
	private String email;

	@Field(type = FieldType.Text)
	private String phone;
	
	@Field(type = FieldType.Text)
	private String altPhone;
	
	public OrganizationES(){}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrgname() {
		return orgname;
	}

	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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
