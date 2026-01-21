package devfox.board;

import devfox.board.config.TestExternalConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import({TestExternalConfig.class})
@ActiveProfiles("test")
class BoardApplicationTests {

	@Test
	void contextLoads() {
	}

}
