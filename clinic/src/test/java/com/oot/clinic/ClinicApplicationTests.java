package com.oot.clinic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ClinicApplicationTests {

	@Test
	void contextLoads() {
		assertTrue(true);
	}

}
