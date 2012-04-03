/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.fs.webdav;

import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.webdav.auth.AnythingGoesRoleCheckPolicy;
import org.apache.hadoop.fs.webdav.auth.HdfsJaasConf;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.plus.jaas.JAASUserRealm;
import org.mortbay.jetty.webapp.WebAppContext;

import java.io.File;
import java.net.URL;

public class WebdavServer {
	
	private static final Log LOG = LogFactory.getLog(WebdavServer.class);

    //public static final String WEB_APP_CONTEXT 	= "webAppContext";
    private static final String DEFAULT_FS_NAME = "hdfs://127.0.0.1:8020";
    private static final String DEFAULT_LISTEN_ADDRESS = "0.0.0.0";
    private static final String DEFAULT_BIND_PORT = "19800";

    private Server webServer;

    public WebdavServer(String bindAddress, int port) throws Exception {
    	javax.security.auth.login.Configuration.setConfiguration(new HdfsJaasConf());
        webServer = new Server();
//        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("jetty.xml");
//        if (resourceAsStream == null)
//            throw new IllegalStateException("cannot find classpath:/jetty.xml");
//        XmlConfiguration configuration = new XmlConfiguration(resourceAsStream);
//        configuration.configure(webServer);

        JAASUserRealm realm = new JAASUserRealm();
        realm.setName("hdfs");
        realm.setLoginModuleName("org.apache.hadoop.fs.webdav.auth.AnythingGoesLoginModule");
        realm.setRoleCheckPolicy(new AnythingGoesRoleCheckPolicy());
        realm.setCallbackHandlerClass("org.apache.hadoop.fs.webdav.auth.AnythingGoesCallbackHandler");

        webServer.addUserRealm(realm);

        WebAppContext webappctxt = new WebAppContext("/", "/");
        URL webxml = this.getClass().getClassLoader().getResource("WEB-INF/web.xml");
        if (! new File(webxml.getFile()).exists())
            throw new IllegalStateException(String.format("cannot find web.xml, though it was at %s", webxml));
        LOG.info("using web.xml at " + webxml);
        webappctxt.setDescriptor(webxml.getFile());
        webappctxt.setAttribute("webAppContext", webappctxt);

        webServer.addHandler(webappctxt);


        Connector connector=new SelectChannelConnector();
        connector.setPort(port);
        connector.setHost(bindAddress);
        webServer.setConnectors(new Connector[]{connector});
    }

    public void start() throws Exception {
        webServer.start();
    }

    public static void main(String[] args) throws Exception {
        final String usage = "WebdavServer";
        final String header = "Run a webdav interface to a hadoop filesystem.";
        Options options = new Options();
        options = buildGeneralOptions(options);
        CommandLineParser parser = new GnuParser();
        CommandLine cmd = parser.parse(options, args);
        if (cmd.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(usage, header, options, "");
            return;
        }
        int port = Integer.parseInt(cmd.getOptionValue("port", DEFAULT_BIND_PORT));

        // we use this cheesy way of passing the configuration to the WebdavServlet because
        //  jetty-5 doesn't have a way to send it in the WebdavServlet constructor
        Configuration config = new Configuration();
        String hdfsUrl = cmd.getOptionValue("fs", DEFAULT_FS_NAME);
        if (hdfsUrl != null) {

            config.set(CommonConfigurationKeysPublic.FS_DEFAULT_NAME_KEY, hdfsUrl);
        }
        {
            LOG.info(hdfsUrl);
            FileSystem fs = FileSystem.get(config);
            //fs instanceof
            FileStatus[] res = fs.listStatus(new Path("/"));
            for (FileStatus f: res) {
                LOG.info("" + f.getPath());
            }
            LOG.info("the fs is " + fs + res);
        }
        WebdavServlet.setConf(config);

        String listenAddress = cmd.getOptionValue("l", DEFAULT_LISTEN_ADDRESS);
        WebdavServer server = new WebdavServer(listenAddress, port);

        LOG.info(String.format("will export %s at dav://%s:%d", hdfsUrl, listenAddress, port));

        server.start();
    }

	private static Options buildGeneralOptions(Options opts) {
		Option listenOpt = new Option("l", "listen", true, "address to listen to, default: " + DEFAULT_LISTEN_ADDRESS);
        listenOpt.setArgName("address");
		opts.addOption(listenOpt);

		Option portOpt = new Option("p", "port", true, "port to bind to, default: " + DEFAULT_BIND_PORT);
        portOpt.setArgName("port");
		opts.addOption(portOpt);

		Option fsOpt = new Option("n", "fs", true, "value for fs.default.name, default: " + DEFAULT_FS_NAME);
        fsOpt.setArgName("uri");
		opts.addOption(fsOpt);

		opts.addOption("h", "help", false, "print usage information");
        return opts;
	}
}
