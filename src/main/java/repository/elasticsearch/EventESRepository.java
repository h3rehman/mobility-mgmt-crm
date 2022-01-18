package repository.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import repository.event.Event;

@Repository
public interface EventESRepository extends ElasticsearchRepository<EventES, String>{

}
