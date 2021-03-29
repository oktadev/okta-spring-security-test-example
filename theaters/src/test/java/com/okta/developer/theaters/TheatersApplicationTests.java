package com.okta.developer.theaters;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"test", "exclude"})
class TheatersApplicationTests {

	@Test
	void contextLoads() {
	}

}
