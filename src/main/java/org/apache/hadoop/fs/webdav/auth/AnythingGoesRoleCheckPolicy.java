package org.apache.hadoop.fs.webdav.auth;

import java.security.Principal;
import java.security.acl.Group;

import org.mortbay.jetty.plus.jaas.RoleCheckPolicy;

public class AnythingGoesRoleCheckPolicy implements RoleCheckPolicy {

	@Override
	public boolean checkRole(String roleName, Principal runAsRole, Group roles) {
		return true;
	}

}
