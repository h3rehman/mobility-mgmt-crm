package repository.event.presenter;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Presenter")
public class Presenter {

	@Id
	@Column(name = "PresenterID")
	private long presenterId;
	
	private String name;
	
	@Column(name = "isactive")
	private boolean isActive;
	
	@OneToMany
	@JoinColumn(name = "PresenterID")
	public List<Eventpresenter> eventPresenters;
	
	Presenter(){}
	
	@Column(name = "lastname")
	private String lastName;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "username")
	private String username;
	
	Presenter(String name){
		this.name = name;
		this.isActive = true;
	}

	public long getPresenterId() {
		return presenterId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	
}
