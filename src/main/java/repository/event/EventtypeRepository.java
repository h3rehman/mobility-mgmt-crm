package repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventtypeRepository extends JpaRepository<Eventtype, Long>{

	public Eventtype findByeventTypeDesc (String eventTypeDesc);
}
