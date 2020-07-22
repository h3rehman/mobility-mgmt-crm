package repository.event.presenter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PresenterRepository extends JpaRepository<Presenter, Long>{

}
