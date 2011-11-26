package test;

import static org.junit.Assert.*;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.HashSet;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.sun.security.auth.UnixNumericUserPrincipal;

public class MyTest {

	@Test
	public void test() throws Exception {
		Principal pr = new UnixNumericUserPrincipal("hadoop");
		HashSet<Principal> usernames = new HashSet<Principal>();
		HashSet<String> passwords = new HashSet<String>();
		HashSet<String> publiccred = new HashSet<String>();
		usernames.add(pr);
		passwords.add("nogood");
		publiccred.add("");
		
		Subject subj = new Subject(true, usernames, publiccred, passwords);
		Subject.doAs(subj, new PrivilegedAction<String>() {

			@Override
			public String run() {
				LoginContext loginContext;
				try {
					loginContext = new LoginContext("my");
					loginContext.login();
				} catch (LoginException e) {
					throw new RuntimeException(e);
				}
				return "hi";
			}
			
		});
		

//		AccessControlContext ctxt = AccessController.getContext();
//		Subject subj = Subject.getSubject(ctxt);
//		System.out.println(subj);
	}

	
	static Logger LOG = Logger.getLogger(MyTest.class);
}
