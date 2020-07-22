package repository.calllog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Notes")
public class Notes {

	@Id
	@Column(name = "noteId")
	long noteId;
	
	@Column(name = "Notes")
	private String notes;
	
	
}
