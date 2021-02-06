package repository.event;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventtypeRepository extends JpaRepository<Eventtype, Long>{

	public Eventtype findByeventTypeDesc (String eventTypeDesc);
	
	/**
	 * The following query gives an exception:
	 * com.fasterxml.jackson.databind.JsonMappingException: 
	 * Null key for a Map not allowed in JSON (use a converting NullKeySerializer?)
	 */
	@Query("SELECT et.eventTypeId, et.eventTypeDesc FROM Eventtype et")
	public List<Map<String, Object>> findAlleventTypes();
}
