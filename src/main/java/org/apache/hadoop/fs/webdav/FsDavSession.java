package org.apache.hadoop.fs.webdav;

import org.apache.jackrabbit.webdav.simple.DavSessionImpl;

/**
 * no generality whatsoever and utter disrepespect for java-beans. I only need a session with
 * the logged-in user so here it is.
 */

public class FsDavSession extends DavSessionImpl {
	
	
	public FsDavSession(String username) {
		super(null);
		this.username = username;
	}

	String username;

}
