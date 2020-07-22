package application;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {

	@BeforeAll
	static void beforeAll() {
		System.out.println("Started BEFORE-All tests...");
	}
	
	@BeforeEach
	void before() {
		System.out.println("Execute BEFORE each test...");
	}
	
	@Test
	void test1() {
		System.out.println("This is test: 1");
		assertEquals(2, 2);
	}
	
	@Test
	void test2() {
		System.out.println("This is test: 2");
	}

	@AfterEach
	void after() {
		System.out.println("Execute AFTER each test...");
	}
	
	@AfterAll
	static void afterAll() {
		System.out.println("Execute AFTER-ALL tests...");
	}
}
