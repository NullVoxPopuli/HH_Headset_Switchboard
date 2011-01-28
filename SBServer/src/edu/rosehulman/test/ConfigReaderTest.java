package edu.rosehulman.test;

import static org.junit.Assert.*;
import edu.rosehulman.ConfigReader;
import org.junit.Test;


public class ConfigReaderTest {
	private ConfigReader easy = new ConfigReader("easy.xml");
	
	@Test
	public void testNotNull() {
		assertNotNull(easy);
	}
	
	@Test
	public void testEasySize() {
		assertEquals(2, easy.getClients().size());
	}

}
