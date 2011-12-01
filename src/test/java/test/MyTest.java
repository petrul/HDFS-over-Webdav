package test;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.Test;

public class MyTest {

	@Test
	public void test() throws Exception {
		assertTrue(true);
	}

	
	static Logger LOG = Logger.getLogger(MyTest.class);
}
