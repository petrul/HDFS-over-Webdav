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

import org.mortbay.jetty.security.HashUserRealm;

import java.util.*;
import java.io.IOException;

public class WebdavHashUserRealm extends HashUserRealm {
    public WebdavHashUserRealm() {
        super();
    }

    public WebdavHashUserRealm(String name, String config) throws IOException {
        super(name, config);
    }

    public List<String> getUserRoles(String userName) {
        List<String> list = new ArrayList<String>();
        if (userName != null && !"".equals(userName)) {
            @SuppressWarnings("unchecked")
			Set<String> roles = this._roles.keySet();
            for (String role : roles) {
				@SuppressWarnings("unchecked")
				HashSet<String> userHashSet = (HashSet<String>) this._roles.get(role);
                for (String user : userHashSet) {
                    if (userName.equals(user)) {
                        list.add(role);
                    }
                }
            }
        }
        return list;
    }

    public void setConfig(String config) {
        try {
            super.setConfig(config);
            this.loadConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

