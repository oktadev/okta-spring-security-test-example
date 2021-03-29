package com.okta.developer.listings;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"test", "exclude"})
class ListingsApplicationTests {

	@Test
	void contextLoads() {
	}

}
