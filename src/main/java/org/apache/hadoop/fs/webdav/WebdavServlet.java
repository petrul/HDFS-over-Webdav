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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.AccessControlException;
import org.apache.jackrabbit.webdav.*;
import org.apache.jackrabbit.webdav.server.AbstractWebdavServlet;
import org.apache.jackrabbit.webdav.simple.LocatorFactoryImpl;
import org.apache.jackrabbit.webdav.simple.ResourceConfig;
import org.apache.jackrabbit.webdav.simple.ResourceFactoryImpl;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Enumeration;


public class WebdavServlet extends AbstractWebdavServlet {

    private static final long serialVersionUID = 1L;

    /**
     * the default logger
     */
    private static final Log LOG = LogFactory.getLog(WebdavServlet.class);

    /**
     * init param name of the repository prefix
     */
    public static final String INIT_PARAM_RESOURCE_PATH_PREFIX = "resource-path-prefix";

    /**
     * Name of the optional init parameter that defines the value of the
     * 'WWW-Authenticate' header.<p/>
     * If the parameter is omitted the default value
     * {@link #DEFAULT_AUTHENTICATE_HEADER "Basic Realm=Hadoop Webdav Server"}
     * is used.
     *
     * @see #getAuthenticateHeaderValue()
     */
    public static final String INIT_PARAM_AUTHENTICATE_HEADER = "authenticate-header";

    /** the 'missing-auth-mapping' init parameter */
    public final static String INIT_PARAM_MISSING_AUTH_MAPPING = "missing-auth-mapping";

    /**
     * Name of the init parameter that specify a separate configuration used
     * for filtering the resources displayed.
     */
    public static final String INIT_PARAM_RESOURCE_CONFIG = "resource-config";

    /**
     * Servlet context attribute used to store the path prefix instead of
     * having a static field with this servlet. The latter causes problems
     * when running multiple
     */
    public static final String CTX_ATTR_RESOURCE_PATH_PREFIX = "hadoop.webdav.resourcepath";

    private String resourcePathPrefix;

    /**
     * Header value as specified in the {@link #INIT_PARAM_AUTHENTICATE_HEADER} parameter.
     */
    private String authenticate_header;

    /**
     * the resource factory
     */
    private DavResourceFactory resourceFactory;

    /**
     * the locator factory
     */
    private DavLocatorFactory locatorFactory;

    /**
     * the webdav session provider
     */
    private DavSessionProvider davSessionProvider;

    /**
     * The config
     */
    private ResourceConfig config;

    private static Configuration hadoopConfig = null;//new Configuration();


//    private static String currentUserName;

    /**
     * Init this servlet
     *
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        LOG.info("*** servlet initializing");
        super.init();
        resourcePathPrefix = getInitParameter(INIT_PARAM_RESOURCE_PATH_PREFIX);
        if (resourcePathPrefix == null) {
            LOG.debug("Missing path prefix -> setting to empty string.");
            resourcePathPrefix = "";
        } else if (resourcePathPrefix.endsWith("/")) {
            LOG.debug("Path prefix ends with '/' -> removing trailing slash.");
            resourcePathPrefix = resourcePathPrefix.substring(0, resourcePathPrefix.length() - 1);
        }

//        getServletContext().setAttribute(CTX_ATTR_RESOURCE_PATH_PREFIX, resourcePathPrefix);
//        LOG.info(INIT_PARAM_RESOURCE_PATH_PREFIX + " = '" + resourcePathPrefix + "'");

        authenticate_header = getInitParameter(INIT_PARAM_AUTHENTICATE_HEADER);
        if (authenticate_header == null) {
            authenticate_header = DEFAULT_AUTHENTICATE_HEADER;
        }
//        LOG.info("WWW-Authenticate header = '" + authenticate_header + "'");
//
//        LOG.info("INIT_PARAMETERS: ");
//        Enumeration<String> e2 = getInitParameterNames();
//        while (e2.hasMoreElements()) {
//            String name = e2.nextElement();
//            LOG.info("-- " + name + ": ");
//        }
//        LOG.info("ServletInfo: " + getServletInfo());
//        LOG.info("ServletName: " + getServletName());
//
//        LOG.info("SERVLET_CONFIG_PARAMETERS: ");
//        Enumeration<String> e3 = getServletConfig().getInitParameterNames();
//        while (e3.hasMoreElements()) {
//            String name = e3.nextElement();
//            LOG.info("-- " + name + ": ");
//        }
//
//        LOG.info("SERVLET_CONTEXT_PARAMETERS: ");
//        Enumeration<?> e4 = getServletContext().getInitParameterNames();
//        while (e4.hasMoreElements()) {
//            String name = (String) e4.nextElement();
//            LOG.info("-- " + name + ": ");
//        }
//
//        LOG.info("SERVLET_CONTEXT_ATTRIBUTES: ");
//        Enumeration<String> e5 = getServletContext().getAttributeNames();
//        while (e5.hasMoreElements()) {
//            String name = e5.nextElement();
//            LOG.info("-- " + name + ": ");
//        }

        String configParam = getInitParameter(INIT_PARAM_RESOURCE_CONFIG);
        if (configParam != null) {
            try {
                config = new ResourceConfig();
                config.parse(getServletContext().getResource(configParam));
            } catch (MalformedURLException e) {
                LOG.debug("Unable to build resource filter provider.");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isPreconditionValid(WebdavRequest request,
                                          DavResource resource) {
        return !resource.exists() || request.matchesIfHeader(resource);
    }

    /**
     * Returns the <code>DavLocatorFactory</code>. If no locator factory has
     * been set or created a new instance of {@link org.apache.jackrabbit.webdav.simple.LocatorFactoryImpl} is
     * returned.
     *
     * @return the locator factory
     * @see AbstractWebdavServlet#getLocatorFactory()
     */
    @Override
    public DavLocatorFactory getLocatorFactory() {
        if (locatorFactory == null) {
            locatorFactory = new LocatorFactoryImpl(resourcePathPrefix);
        }
        return locatorFactory;
    }

    /**
     * Sets the <code>DavLocatorFactory</code>.
     *
     * @param locatorFactory
     * @see AbstractWebdavServlet#setLocatorFactory(DavLocatorFactory)
     */
    @Override
    public void setLocatorFactory(DavLocatorFactory locatorFactory) {
        this.locatorFactory = locatorFactory;
    }

    /**
     * Returns the <code>DavResourceFactory</code>. If no request factory has
     * been set or created a new instance of {@link ResourceFactoryImpl} is
     * returned.
     *
     * @return the resource factory
     * @see org.apache.jackrabbit.server.AbstractWebdavServlet#getResourceFactory()
     */
    @Override
    public DavResourceFactory getResourceFactory() {
        if (resourceFactory == null) {
            resourceFactory = new FSDavResourceFactory(getResourceConfig(),
                                                       getConf(getServletContext()));
        }
        return resourceFactory;
    }

    /**
     * Sets the <code>DavResourceFactory</code>.
     *
     * @param resourceFactory
     * @see AbstractWebdavServlet#setResourceFactory(org.apache.jackrabbit.webdav.DavResourceFactory)
     */
    @Override
    public void setResourceFactory(DavResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

    /**
     * Returns the <code>DavSessionProvider</code>. If no session provider has
     * been set or created a new instance of {@link FakeDavSessionProvider}
     * is returned.
     *
     * @return the session provider
     */
    @Override
    public synchronized DavSessionProvider getDavSessionProvider() {
        if (davSessionProvider == null) {
            davSessionProvider = new FakeDavSessionProvider();
        }
        return davSessionProvider;
    }

    /**
     * Sets the <code>DavSessionProvider</code>.
     *
     * @param sessionProvider
     * @see AbstractWebdavServlet#setDavSessionProvider(org.apache.jackrabbit.webdav.DavSessionProvider)
     */
    @Override
    public synchronized void setDavSessionProvider(DavSessionProvider sessionProvider) {
        this.davSessionProvider = sessionProvider;
    }

    /**
     * Returns the header value retrieved from the {@link #INIT_PARAM_AUTHENTICATE_HEADER}
     * init parameter. If the parameter is missing, the value defaults to
     * {@link #DEFAULT_AUTHENTICATE_HEADER}.
     *
     * @return the header value retrieved from the corresponding init parameter
     * or {@link #DEFAULT_AUTHENTICATE_HEADER}.
     * @see org.apache.jackrabbit.server.AbstractWebdavServlet#getAuthenticateHeaderValue()
     */
    @Override
    public String getAuthenticateHeaderValue() {
        return authenticate_header;
    }

    /**
     * Returns the resource configuration to be applied
     *
     * @return the resource configuration.
     */
    private ResourceConfig getResourceConfig() {
        // fallback if no config present
        if (config == null) {
            config = new ResourceConfig();
        }
        return config;
    }

    /**
     * Returns the caches {@link Configuration} if a {@link Configuration} is 
     * found in the {@link javax.servlet.ServletContext} it is simply returned,
     * otherwise, a new {@link Configuration} is created, and then all the init 
     * parameters found in the {@link javax.servlet.ServletContext} are added to 
     * the {@link Configuration} (the created {@link Configuration} is then 
     * saved into the {@link javax.servlet.ServletContext}).
     *
     * @param application is the ServletContext whose init parameters
     *        must override those of Nutch.
     */

    //TODO for DEBUG only
//    private static List<String> currentUserRoles;

    private static Configuration getConf(ServletContext application) {
        Configuration conf = (Configuration) application.getAttribute("dfs.servlet.conf.key");

        if (conf == null) {
            conf = hadoopConfig;
            Enumeration<String> e = application.getInitParameterNames();
            while (e.hasMoreElements()) {
                String name = e.nextElement();
                conf.set(name, application.getInitParameter(name));
            }
            application.setAttribute("dfs.servlet.conf.key", conf);
        }

//        if (currentUserName != null) {
//            WebAppContext webapp = (WebAppContext) application.getAttribute(WebdavServer.WEB_APP_CONTEXT);
//            WebdavHashUserRealm userRealm = (WebdavHashUserRealm) webapp.getSecurityHandler().getUserRealm();
//            List<String> userRoles = userRealm.getUserRoles(currentUserName);
//            currentUserRoles = userRoles;

// TODO commented these out but I should replace them with modern kerberos-based authentication
//            UnixUserGroupInformation ugi = new UnixUserGroupInformation(currentUserName, userRoles.toArray(new String[0]));
//            UnixUserGroupInformation.saveToConf(conf, UnixUserGroupInformation.UGI_PROPERTY_NAME, ugi);
//        }
        return conf;
    }



    /**
     * This is a cheesy way to set the value of the hadoop config
     * @param config
     */
    public static void setConf(Configuration config) {
        hadoopConfig = config;
    }

    protected void service(HttpServletRequest request,
                           HttpServletResponse response) throws ServletException, IOException {
        WebdavRequest webdavRequest = new WebdavRequestImpl(request, getLocatorFactory());
        // DeltaV requires 'Cache-Control' header for all methods except 'VERSION-CONTROL' and 'REPORT'.
        int methodCode = DavMethods.getMethodCode(request.getMethod());
        boolean noCache = DavMethods.isDeltaVMethod(webdavRequest) && !(DavMethods.DAV_VERSION_CONTROL == methodCode || DavMethods.DAV_REPORT == methodCode);
        WebdavResponse webdavResponse = new WebdavResponseImpl(response, noCache);
        try {
            // make sure there is a authenticated user
            if (!getDavSessionProvider().attachSession(webdavRequest)) {
                return;
            }

            // check matching if=header for lock-token relevant operations
            DavResource resource = getResourceFactory().createResource(webdavRequest.getRequestLocator(), webdavRequest, webdavResponse);
            if (!isPreconditionValid(webdavRequest, resource)) {
                webdavResponse.sendError(DavServletResponse.SC_PRECONDITION_FAILED);
                return;
            }
            if (!execute(webdavRequest, webdavResponse, methodCode, resource)) {
                super.service(request, response);
            }

        } catch (DavException e) {
            if (e.getErrorCode() == HttpServletResponse.SC_UNAUTHORIZED) {
                webdavResponse.setHeader("WWW-Authenticate", getAuthenticateHeaderValue());
                StringBuilder sb = new StringBuilder(e.getStatusPhrase());
                if (e.getCause() != null && e.getCause().getMessage() != null) {
                	sb.append(": " + e.getCause().getMessage());
                }
                webdavResponse.sendError(e.getErrorCode(), sb.toString());
            } else {
                webdavResponse.sendError(e);
            }
        } catch (Exception e) {
        	LOG.error(e, e);
            if (e.getCause() instanceof AccessControlException) {
                LOG.info("EXCEPTION: Can't access to resource. You don't have permissions.");
                MultiStatusResponse msr = new MultiStatusResponse(
                		request.getRequestURL().toString(), 401,
                        "Can't access to resource. You don't have permissions.");

                MultiStatus ms = new MultiStatus();
                ms.addResponse(msr);

                webdavResponse = new WebdavResponseImpl(response);
                webdavResponse.sendMultiStatus(ms);
            } else {
            	new WebdavResponseImpl(response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } finally {
            getDavSessionProvider().releaseSession(webdavRequest);
        }

    }

//	private void debugInfo(HttpServletRequest request) {
//		if (LOG.isDebugEnabled()) LOG.debug(request.getMethod() + " " + request.getRequestURL().toString());
//        if (LOG.isDebugEnabled()) LOG.debug(request.getMethod() + " " + request.getRequestURL().toString());
//        if (LOG.isDebugEnabled()) LOG.debug(request.getMethod() + " " + request.getRequestURI().toString());
//
//        if (LOG.isDebugEnabled()) LOG.debug("  RemoteHost: " + request.getRemoteHost());
//        if (LOG.isDebugEnabled()) LOG.debug("| ATTRIBUTES: ");
//        Enumeration<String> e1 = request.getAttributeNames();
//        while (e1.hasMoreElements()) {
//            String name = (String) e1.nextElement();
//            LOG.debug("|| " + name + ": ");
//        }
//
//        LOG.info("| PARAMETERS: ");
//        Enumeration<String> e2 = request.getParameterNames();
//        while (e2.hasMoreElements()) {
//            String name = (String) e2.nextElement();
//            LOG.info("|| " + name + ": ");
//        }
//        
//        LOG.info("HEADERS: ");
//        Enumeration<String> e6 = request.getHeaderNames();
//        while (e6.hasMoreElements()) {
//            String name = (String) e6.nextElement();
//            LOG.info("-- " + name + ": " + request.getHeader(name));
//        }
//        LOG.info("RemoteUser: " + request.getRemoteUser());
//        LOG.info("AuthType: " + request.getAuthType());
//
//        currentUserName = request.getRemoteUser();
//
//        String roles = "";
//        if (currentUserRoles != null) {
//            for (String roleName : currentUserRoles) {
//                roles += roleName + ", ";
//            }
//            if (roles.length() > 2) {
//                roles = roles.substring(0, roles.length()-2);
//            }
//        }
//        LOG.debug("Roles: " + roles);
//	}
}
