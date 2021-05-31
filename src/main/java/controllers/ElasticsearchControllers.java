package controllers;

import java.io.IOException;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import repository.elasticsearch.EventES;

@RestController
@RequestMapping("/api/elasticsearch")
public class ElasticsearchControllers {

	@Autowired
	SearchOperations searchOperations;
	
	@Autowired
	RestHighLevelClient restHighLevelClient;
	
	private final String EVENT_INDEX = "events";
	private final String ORG_INDEX = "organizations";
	private final String CONTACT_INDEX = "contacts";
		
	@GetMapping("/explicit/{queryString}")
	public SearchHits<EventES> lookupSingleEntity (@PathVariable String queryString){
		
		QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(queryString, "eventName", "location")
				.fuzziness(Fuzziness.AUTO) // <- Fuzziness is the number of character changes that need to be made to a string to make it same as another string. 
				.prefixLength(3);  // <- prefix length improves performance i.e. first 'n' characters should match exactly.  
 		
		Query searchQuery = new NativeSearchQueryBuilder()
				  .withFilter(queryBuilder)
				  .build();

		SearchHits<EventES> eventsEs = searchOperations
				  .search(searchQuery, EventES.class, IndexCoordinates.of(EVENT_INDEX));
		
		return eventsEs;
	}
	
	@GetMapping("/event-wildcard/{queryString}")
	public SearchHits<EventES> wildEntityLookup (@PathVariable String queryString){
		
		QueryBuilder queryBuilder = QueryBuilders.wildcardQuery("eventname", "*$queryString*");
		
		Query searchQuery = new NativeSearchQueryBuilder()
				  .withFilter(queryBuilder)
				  .withPageable(PageRequest.of(0, 5))
				  .build();

		SearchHits<EventES> eventsEs = searchOperations
				  .search(searchQuery, EventES.class, IndexCoordinates.of(EVENT_INDEX));
		
		return eventsEs;
	}

	@GetMapping("/multi-field-index/{queryString}")
	public org.elasticsearch.search.SearchHits multiFieldIndexLookup (@PathVariable String queryString) throws IOException{
		
		QueryBuilder eventQueryBuilder = QueryBuilders.multiMatchQuery(queryString, "eventName", "location", "address")
				.fuzziness(Fuzziness.AUTO)  
				.prefixLength(3)
				.operator(Operator.AND);   
		
		QueryBuilder orgQueryBuilder = QueryBuilders.multiMatchQuery(queryString, "orgname", "address", "email", "phone", "altPhone")
				.fuzziness(Fuzziness.AUTO)  
				.prefixLength(3)
				.operator(Operator.AND);   

		QueryBuilder contactQueryBuilder = QueryBuilders.multiMatchQuery(queryString, "firstName", "lastName", "title", "email", "phone", "altPhone")
				.fuzziness(Fuzziness.AUTO)  
				.prefixLength(3)
				.operator(Operator.AND);
		
		// Create a Bool query 
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
			boolQuery.minimumShouldMatch(1)
				.should(eventQueryBuilder)
				.should(orgQueryBuilder)
				.should(contactQueryBuilder);
			
		// Create a search request 
		SearchRequest searchRequest = new SearchRequest(EVENT_INDEX, ORG_INDEX, CONTACT_INDEX);
		// Create a search Source
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		 searchSourceBuilder.query(boolQuery);
		 searchRequest.source(searchSourceBuilder);
		// Create object to get Response
		SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		// Parsing response 
		org.elasticsearch.search.SearchHits searchHits = searchResponse.getHits();
		
		return searchHits;
	}
	
}
