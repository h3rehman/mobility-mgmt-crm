package repository.note;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import repository.event.Event;
import repository.organization.Organization;


@Entity
@Table(name = "Note")
public class Note {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "NoteID")
	Long noteId;
	
	@Column(name = "noteentry")
	private String noteEntry;
	
	
	@Column(name = "createddate", columnDefinition = "TIMESTAMP")
	private LocalDateTime createDate;
	
	
	@Column(name = "lastmodifieddate", columnDefinition = "TIMESTAMP")
	private LocalDateTime lastModifiedDate;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "EventID")
	Event event;
	
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "OrgID")
	Organization org;


	public Long getNoteId() {
		return noteId;
	}


	public void setNoteId(Long noteId) {
		this.noteId = noteId;
	}


	public String getNoteEntry() {
		return noteEntry;
	}


	public void setNoteEntry(String noteEntry) {
		this.noteEntry = noteEntry;
	}


	public LocalDateTime getCreateDate() {
		return createDate;
	}


	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}


	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}


	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}


	public Event getEvent() {
		return event;
	}


	public void setEvent(Event event) {
		this.event = event;
	}


	public Organization getOrg() {
		return org;
	}


	public void setOrg(Organization org) {
		this.org = org;
	}
	
	
}
