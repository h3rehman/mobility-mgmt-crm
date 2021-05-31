package config;

import java.util.List;

import javax.annotation.PostConstruct;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

import repository.elasticsearch.EventES;
import repository.elasticsearch.EventESRepository;
import repository.event.Event;
import repository.event.EventRepository;

@Configuration
public class ElasticsearchRestClientConfig extends AbstractElasticsearchConfiguration {

	
	@Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {

        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()  
            .connectedTo("localhost:9200")
//            .usingSsl()
            .build();

        return RestClients.create(clientConfiguration).rest();                         
    }
	

}
