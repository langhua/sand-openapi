/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import org.apache.ofbiz.webapp.control.ExternalLoginKeysManager
import org.apache.ofbiz.webapp.control.LoginWorker

result = LoginWorker.login(request, response)
userLogin = request.getSession().getAttribute("userLogin")
if (result == "success" && userLogin) {
    request.setAttribute("userLogin", userLogin)
    externalLoginKey = ExternalLoginKeysManager.getExternalLoginKey(request)
    if (externalLoginKey) {
        response.setHeader("Set-Cookie", "externalLoginKey=" + externalLoginKey + "; HttpOnly; Path=/; secure")
    }
}

return result
