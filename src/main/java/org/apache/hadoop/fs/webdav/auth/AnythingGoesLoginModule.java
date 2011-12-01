package org.apache.hadoop.fs.webdav.auth;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

public class AnythingGoesLoginModule implements LoginModule {

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map<String, ?> sharedState, Map<String, ?> options) {
		//throw new RuntimeException("unimplemented");
		
	}

	@Override
	public boolean login() throws LoginException {
		//throw new RuntimeException("unimplemented");
		return true;
	}

	@Override
	public boolean commit() throws LoginException {
		return true;
		//throw new RuntimeException("unimplemented");
	}

	@Override
	public boolean abort() throws LoginException {
		return true;
		//throw new RuntimeException("unimplemented");
	}

	@Override
	public boolean logout() throws LoginException {
		return true;
		//throw new RuntimeException("unimplemented");
	}


}
