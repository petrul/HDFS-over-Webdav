package org.apache.hadoop.fs.webdav.auth;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
import javax.security.auth.login.Configuration;

/**
 * programatic {@link Configuration} so that we don't have to start java with the 
 * -Djava.security.auth.login.config=mylogin.conf .
 *
 */
public class HdfsJaasConf extends Configuration {
	
	@Override
	public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
		
		Map<String, Object> options = new HashMap<String, Object>();
		AppConfigurationEntry entry = new AppConfigurationEntry("org.apache.hadoop.fs.webdav.auth.AnythingGoesLoginModule", LoginModuleControlFlag.REQUIRED, options);
		AppConfigurationEntry[] arr = new AppConfigurationEntry[1];
		arr[0] = entry;
		return arr;

	}

}
