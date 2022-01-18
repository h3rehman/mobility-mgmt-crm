package repository.elasticsearch;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import application.Application;

/**
 * 
 * @author Hamood Rehman
 * These tests are written assuming there are no other values in the indices. 
 *
 */

@EnableAutoConfiguration
@SpringBootTest(classes = Application.class)
@ExtendWith(SpringExtension.class)
@Disabled("Disabled until the surefire configuration/dependency issue is resolved to run these tests")
public class ESCRUDTests {

	@Autowired
	EventESRepository eventEsRepository;
	
	@Autowired
	OrganizationESRepository orgEsRepository;
	
	@Autowired
	ContactESRepository contactEsRepository;
	
	@Autowired
	RestHighLevelClient restHighLevelClient;
	
	@Value("${elasticsearch.indices.events}")
	private String EVENT_INDEX;
	
	@Value("${elasticsearch.indices.organizations}")
	private String ORG_INDEX;
	
	@Value("${elasticsearch.indices.contacts}")
	private String CONTACT_INDEX;
	
	@BeforeEach
	void createESElement () {
		EventES eventEs = new EventES();
		eventEs.setId("100");
		eventEs.setEventName("Test Event Name");
		eventEs.setLocation("Test Event Location");
		eventEs.setAddress("999 E Jackson St.");
		
		eventEsRepository.save(eventEs);
		
		OrganizationES orgEs = new OrganizationES();
		orgEs.setId("100");
		orgEs.setOrgname("Test Org Name");
		orgEs.setEmail("org@email.com");
		orgEs.setPhone("708-888-9999");
		orgEs.setAddress("999 E Jackson St.");
		
		orgEsRepository.save(orgEs);
		
		ContactES contactEs = new ContactES();
		contactEs.setId("1000");
		contactEs.setFirstName("Esha");
		contactEs.setLastName("Gupta");
		contactEs.setEmail("esha@email.com");
		contactEs.setPhone("708-888-9999");
		
		contactEsRepository.save(contactEs);
	}
	
	@Test
	void testForReadESElement1 () throws IOException {
		String queryString1 = "Test";
	
		
		//Search Query 1			
		QueryBuilder eventQueryBuilder1 = QueryBuilders.multiMatchQuery(queryString1, "eventName", "location", "address")
				.fuzziness(Fuzziness.AUTO)  
				.prefixLength(3)
				.operator(Operator.AND);   
		
		QueryBuilder orgQueryBuilder1 = QueryBuilders.multiMatchQuery(queryString1, "orgname", "address", "email", "phone", "altPhone")
				.fuzziness(Fuzziness.AUTO)  
				.prefixLength(3)
				.operator(Operator.AND);   

		QueryBuilder contactQueryBuilder1 = QueryBuilders.multiMatchQuery(queryString1, "firstName", "lastName", "title", "email", "phone", "altPhone")
				.fuzziness(Fuzziness.AUTO)  
				.prefixLength(3)
				.operator(Operator.AND);
		
		
		// Create a Bool query 
		BoolQueryBuilder boolQuery1 = QueryBuilders.boolQuery();
			boolQuery1.minimumShouldMatch(1)
				.should(eventQueryBuilder1)
				.should(orgQueryBuilder1)
				.should(contactQueryBuilder1);
		
		// Create a search request 
		SearchRequest searchRequest1 = new SearchRequest(EVENT_INDEX, ORG_INDEX, CONTACT_INDEX);
		// Create a search Source
		SearchSourceBuilder searchSourceBuilder1 = new SearchSourceBuilder();
		 searchSourceBuilder1.query(boolQuery1);
		 searchRequest1.source(searchSourceBuilder1);
		// Create object to get Response
		SearchResponse searchResponse1 = restHighLevelClient.search(searchRequest1, RequestOptions.DEFAULT);
		// Parsing response 
		org.elasticsearch.search.SearchHits searchHits1 = searchResponse1.getHits();
		

		assertNotNull(searchHits1.getHits());
		assertEquals(searchHits1.getTotalHits().value, 24L);
	}
	
	@Test
	void testForReadESElement2 () throws IOException {
		String queryString2 = "999 E Jackson St.";
	
		//Search Query 2
		QueryBuilder eventQueryBuilder2 = QueryBuilders.multiMatchQuery(queryString2, "eventName", "location", "address")
				.fuzziness(Fuzziness.AUTO)  
				.prefixLength(3)
				.operator(Operator.AND);   
		
		QueryBuilder orgQueryBuilder2 = QueryBuilders.multiMatchQuery(queryString2, "orgname", "address", "email", "phone", "altPhone")
				.fuzziness(Fuzziness.AUTO)  
				.prefixLength(3)
				.operator(Operator.AND);   

		QueryBuilder contactQueryBuilder2 = QueryBuilders.multiMatchQuery(queryString2, "firstName", "lastName", "title", "email", "phone", "altPhone")
				.fuzziness(Fuzziness.AUTO)  
				.prefixLength(3)
				.operator(Operator.AND);

		// Create a Bool query 
		BoolQueryBuilder boolQuery2 = QueryBuilders.boolQuery();
			boolQuery2.minimumShouldMatch(1)
				.should(eventQueryBuilder2)
				.should(orgQueryBuilder2)
				.should(contactQueryBuilder2);
		
		// Create a search request 
		SearchRequest searchRequest2 = new SearchRequest(EVENT_INDEX, ORG_INDEX, CONTACT_INDEX);
		// Create a search Source
		SearchSourceBuilder searchSourceBuilder2 = new SearchSourceBuilder();
		 searchSourceBuilder2.query(boolQuery2);
		 searchRequest2.source(searchSourceBuilder2);
		// Create object to get Response
		SearchResponse searchResponse2 = restHighLevelClient.search(searchRequest2, RequestOptions.DEFAULT);
		// Parsing response 
		org.elasticsearch.search.SearchHits searchHits2 = searchResponse2.getHits();
		
		assertNotNull(searchHits2.getHits());
		assertEquals(searchHits2.getTotalHits().value, 2L);
	}
	
	@Test
	void testForReadESElement3 () throws IOException {
		String queryString3 = "708-888-9999";

		//Search Query 3
		QueryBuilder eventQueryBuilder3 = QueryBuilders.multiMatchQuery(queryString3, "eventName", "location", "address")
				.fuzziness(Fuzziness.AUTO)  
				.prefixLength(3)
				.operator(Operator.AND);   
		
		QueryBuilder orgQueryBuilder3 = QueryBuilders.multiMatchQuery(queryString3, "orgname", "address", "email", "phone", "altPhone")
				.fuzziness(Fuzziness.AUTO)  
				.prefixLength(3)
				.operator(Operator.AND);   

		QueryBuilder contactQueryBuilder3 = QueryBuilders.multiMatchQuery(queryString3, "firstName", "lastName", "title", "email", "phone", "altPhone")
				.fuzziness(Fuzziness.AUTO)  
				.prefixLength(3)
				.operator(Operator.AND);

		// Create a Bool query 
		BoolQueryBuilder boolQuery3 = QueryBuilders.boolQuery();
			boolQuery3.minimumShouldMatch(1)
				.should(eventQueryBuilder3)
				.should(orgQueryBuilder3)
				.should(contactQueryBuilder3);
		
		// Create a search request 
		SearchRequest searchRequest3 = new SearchRequest(EVENT_INDEX, ORG_INDEX, CONTACT_INDEX);
		// Create a search Source
		SearchSourceBuilder searchSourceBuilder3 = new SearchSourceBuilder();
		 searchSourceBuilder3.query(boolQuery3);
		 searchRequest3.source(searchSourceBuilder3);
		// Create object to get Response
		SearchResponse searchResponse3 = restHighLevelClient.search(searchRequest3, RequestOptions.DEFAULT);
		// Parsing response 
		org.elasticsearch.search.SearchHits searchHits3 = searchResponse3.getHits();

		assertNotNull(searchHits3.getHits());
		assertEquals(searchHits3.getTotalHits().value, 2L);

	}

	@Test
	void testForReadESElement4 () throws IOException {
		String queryString4 = "Esha";
		
		//Search Query 4
		QueryBuilder eventQueryBuilder4 = QueryBuilders.multiMatchQuery(queryString4, "eventName", "location", "address")
				.fuzziness(Fuzziness.AUTO)  
				.prefixLength(3)
				.operator(Operator.AND);   
		
		QueryBuilder orgQueryBuilder4 = QueryBuilders.multiMatchQuery(queryString4, "orgname", "address", "email", "phone", "altPhone")
				.fuzziness(Fuzziness.AUTO)  
				.prefixLength(3)
				.operator(Operator.AND);   

		QueryBuilder contactQueryBuilder4 = QueryBuilders.multiMatchQuery(queryString4, "firstName", "lastName", "title", "email", "phone", "altPhone")
				.fuzziness(Fuzziness.AUTO)  
				.prefixLength(3)
				.operator(Operator.AND);

		// Create a Bool query 
		BoolQueryBuilder boolQuery4 = QueryBuilders.boolQuery();
			boolQuery4.minimumShouldMatch(1)
				.should(eventQueryBuilder4)
				.should(orgQueryBuilder4)
				.should(contactQueryBuilder4);
		
		// Create a search request 
		SearchRequest searchRequest4 = new SearchRequest(EVENT_INDEX, ORG_INDEX, CONTACT_INDEX);
		// Create a search Source
		SearchSourceBuilder searchSourceBuilder4 = new SearchSourceBuilder();
		 searchSourceBuilder4.query(boolQuery4);
		 searchRequest4.source(searchSourceBuilder4);
		// Create object to get Response
		SearchResponse searchResponse4 = restHighLevelClient.search(searchRequest4, RequestOptions.DEFAULT);
		// Parsing response 
		org.elasticsearch.search.SearchHits searchHits4 = searchResponse4.getHits();

		assertNotNull(searchHits4.getHits());
		assertEquals(searchHits4.getTotalHits().value, 1L);
	}

	
	@Test
	void testForUpdateESElement () {
		Optional<EventES> optEventEs = eventEsRepository.findById("100");
		if (optEventEs != null) {
			EventES eventEs = optEventEs.get();
			eventEs.setEventName("New Event Name");
			eventEsRepository.save(eventEs);
			
			assertEquals(eventEs.getEventName(), "New Event Name");
		}
		
		Optional<OrganizationES> optOrgEs = orgEsRepository.findById("100");
		if (optOrgEs != null) {
			OrganizationES orgEs = optOrgEs.get();
			orgEs.setAddress("New Address");
			orgEsRepository.save(orgEs);
			
			assertEquals(orgEs.getAddress(), "New Address");		
		}
		
		Optional<ContactES> optContEs = contactEsRepository.findById("1000");
		if (optContEs != null) {
			ContactES conEs = optContEs.get();
			conEs.setLastName("Mansha");
			contactEsRepository.save(conEs);
			
			assertEquals(conEs.getLastName(), "Mansha");		
		}
		
	}
	
	@AfterEach
	void deleteESElement () {
		Optional<EventES> optEventEs = eventEsRepository.findById("100");
		if (optEventEs != null) {
			EventES eventEs = optEventEs.get();
			eventEsRepository.delete(eventEs);

		}
		
		Optional<OrganizationES> optOrgEs = orgEsRepository.findById("100");
		if (optOrgEs != null) {
			OrganizationES orgEs = optOrgEs.get();
			orgEsRepository.delete(orgEs);
		}
		
		Optional<ContactES> optContEs = contactEsRepository.findById("1000");
		if (optContEs != null) {
			ContactES conEs = optContEs.get();
			contactEsRepository.delete(conEs);
		}
	}
}
