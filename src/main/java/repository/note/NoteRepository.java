package repository.note;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import repository.organization.Organization;

@Repository	
public interface NoteRepository extends JpaRepository<Note, Long> {
	
	public List<Note> findAllByorg(Organization org);
	

	@Query("SELECT n.noteId, n.noteEntry FROM Note n "
		 + "JOIN n.org o "
		 + "WHERE o.orgId = :id")
	public List<Map<String, Object>> findByorg(@Param("id") Long orgId);

}
