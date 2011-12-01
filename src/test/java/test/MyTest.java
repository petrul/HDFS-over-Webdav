package test;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.sun.security.auth.UnixNumericUserPrincipal;

public class MyTest {

	@Test
	public void test() throws Exception {
		assertTrue(true);
	}

	
	static Logger LOG = Logger.getLogger(MyTest.class);
}
