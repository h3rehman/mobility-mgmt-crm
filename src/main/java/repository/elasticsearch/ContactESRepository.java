package repository.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactESRepository extends ElasticsearchRepository<ContactES, String>{

}
