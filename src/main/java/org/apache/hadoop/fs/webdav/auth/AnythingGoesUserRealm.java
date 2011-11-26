package org.apache.hadoop.fs.webdav.auth;

import java.security.Principal;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.Response;
import org.mortbay.jetty.security.Credential;
import org.mortbay.jetty.security.SSORealm;
import org.mortbay.jetty.security.UserRealm;

public class AnythingGoesUserRealm implements UserRealm, SSORealm {

	@Override
	public Credential getSingleSignOn(Request request, Response response) {
//		return new Credential();
		return null;
	}

	@Override
	public void setSingleSignOn(Request request, Response response,
			Principal principal, Credential credential) {
		throw new RuntimeException("unimplemented");		
	}

	@Override
	public void clearSingleSignOn(String username) {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public String getName() {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public Principal getPrincipal(String username) {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public Principal authenticate(String username, Object credentials,
			Request request) {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public boolean reauthenticate(Principal user) {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public boolean isUserInRole(Principal user, String role) {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public void disassociate(Principal user) {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public Principal pushRole(Principal user, String role) {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public Principal popRole(Principal user) {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public void logout(Principal user) {
		throw new RuntimeException("unimplemented");
	}

}
