package soaintperu.soaintperu;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

import soaintperu.soaintperu.Demo;

class GreetingControllerTest {

    private WebTestClient testClient;

    @BeforeAll
    void setUp() throws Exception {
        this.testClient = WebTestClient.bindToController(new GreetingController())
            .build();
    }

    @Test
    void greeting() throws Exception {
        this.testClient.get().uri("/greeting") //
            .exchange() //
            .expectStatus().isOk() //
            .expectBody(String.class).isEqualTo("{\"id\":1,\"content\":\"Hello, World!\"}");
    }
    
    

	@Test
	void testLoginArchivoAdvertencia() throws Exception {
		
		Map<String,String> mapConfig = new HashMap<String,String>();
		
		mapConfig.put("FOLDER_LOGS", "logs");
		
		Demo demo = new Demo(true, false, false, false, true, false, mapConfig);
		
		demo.LogMessage("Mensaje de advertencia", true, false, false);	
		File logFile = new File("folderlog/logFile.txt");
		assertTrue(logFile.exists());
	}
}
