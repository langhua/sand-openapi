/*******************************************************************************
 * Copyright 2024 Langhua Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package langhua.openapi.webdav;

import org.apache.catalina.connector.RequestFacade;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilHttp;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.webapp.control.ExternalLoginKeysManager;
import org.apache.ofbiz.webapp.control.LoginWorker;
import org.apache.tomcat.util.http.Parameters;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;

public class OFBizAuthnFilter extends OncePerRequestFilter {
    private static final String MODULE = OFBizAuthnFilter.class.getName();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            if (request.getCookies() != null && request.getCookies().length > 0) {
                for (Cookie cookie : request.getCookies()) {
                    Debug.logInfo("== cookie: [" + cookie.getName() + "=" + cookie.getValue() + ";path=" + cookie.getPath() + "]", MODULE);
                    if (cookie.getName().equals("externalLoginKey") && UtilValidate.isNotEmpty(cookie.getValue())) {
                        RequestFacade requestFacade = (RequestFacade) request;
                        Field declaredField = null;
                        declaredField = requestFacade.getClass().getDeclaredField("request");
                        declaredField.setAccessible(true);

                        Object requestObject = declaredField.get(request);
                        Field coyoteRequest = requestObject.getClass().getDeclaredField("coyoteRequest");
                        coyoteRequest.setAccessible(true);

                        Object cro = coyoteRequest.get(requestObject);
                        Field parametersField = cro.getClass().getDeclaredField("parameters");
                        parametersField.setAccessible(true);

                        Parameters parameters = (Parameters) parametersField.get(cro);
                        parameters.addParameter("externalLoginKey", cookie.getValue());

                        if (UtilValidate.isEmpty(session.getAttribute("_WEBAPP_NAME_"))) {
                            session.setAttribute("_WEBAPP_NAME_", UtilHttp.getApplicationName(request));
                        }
                        ExternalLoginKeysManager.checkExternalLoginKey(request, response);
                        break;
                    }
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Debug.logError(e.getMessage(), MODULE);
        }

        Object userLogin = session.getAttribute("userLogin");
        Debug.logInfo("== userLogin: " + userLogin, MODULE);
        if (userLogin == null) {
            String result = LoginWorker.checkLogin(request, response);
            Debug.logInfo("result: " + result, MODULE);
            if (!result.equals("success")) {
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
