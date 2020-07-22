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
	
	
}
