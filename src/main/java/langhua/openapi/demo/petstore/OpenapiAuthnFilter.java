/*******************************************************************************
 * Copyright 2023 Langhua Ltd.
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
package langhua.openapi.demo.petstore;

import langhua.ofbiz.webapp.control.OAuth2LoginWorker;
import org.apache.catalina.connector.RequestFacade;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.StringUtil;
import org.apache.ofbiz.base.util.UtilHttp;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.security.Security;
import org.apache.ofbiz.security.SecurityConfigurationException;
import org.apache.ofbiz.security.SecurityFactory;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.webapp.WebAppUtil;
import org.apache.ofbiz.webapp.control.JWTManager;
import org.apache.ofbiz.webapp.control.LoginWorker;
import org.apache.tomcat.util.http.Parameters;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class OpenapiAuthnFilter extends OncePerRequestFilter {
    private static final String MODULE = OpenapiAuthnFilter.class.getName();
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Debug.logInfo("==== Openapi Request URI: " + request.getRequestURI(), MODULE);
        /*
          The following is a hard-coded authentication flow:
          1. OAuth2LoginWorker.oauth2CasCheckLogin(), include bearer token authentication
          2. JWTManager.getJWTToken()
          3. Http Basic authentication
         */

        HttpSession session = request.getSession();
        prepareWebapp(request);

        Object userLogin = session.getAttribute("userLogin");
        String result = null;
        if (userLogin == null) {
            try {
                result = OAuth2LoginWorker.oauth2CasCheckLogin(request, response);
                if (!request.isRequestedSessionIdValid()) {
                    sendUnauthorized(request, response);
                    return;
                }
            } catch (Exception e) {
                Debug.logError(e, MODULE);
            }
        }

        userLogin = session.getAttribute("userLogin");
        if (!"success".equals(result) || userLogin == null) {
            result = JWTManager.checkJWTLogin(request, response);
            if (!request.isRequestedSessionIdValid()) {
                sendUnauthorized(request, response);
                return;
            }
        }

        userLogin = session.getAttribute("userLogin");
        if (!result.equals("success") || userLogin == null) {
            try {
                result = checkBasicLogin(request, response);
                if (!request.isRequestedSessionIdValid()) {
                    sendUnauthorized(request, response);
                    return;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                Debug.logError(e, MODULE);
            }
        }

        userLogin = session.getAttribute("userLogin");
        if (!result.equals("success") || userLogin == null) {
            result = LoginWorker.checkLogin(request, response);
            if (!request.isRequestedSessionIdValid()) {
                sendUnauthorized(request, response);
                return;
            }
        }

        userLogin = session.getAttribute("userLogin");
        if (!result.equals("success") || userLogin == null) {
            sendUnauthorized(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendUnauthorized(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("status", HttpServletResponse.SC_UNAUTHORIZED);
        request.setAttribute("_ERROR_MESSAGE_", "Unauthorized. Login failed.");
        request.getRequestDispatcher("/control/loginFailed").forward(request, response);
    }

    public static void prepareWebapp(HttpServletRequest request) {
        UtilHttp.setInitialRequestInfo(request);
        HttpSession session = request.getSession();
        ServletContext servletContext = session.getServletContext();
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        if (delegator == null) {
            delegator = (Delegator) servletContext.getAttribute("delegator");
        }
        if (delegator == null) {
            delegator = WebAppUtil.getDelegator(servletContext);
        }
        if (delegator != null) {
            request.setAttribute("delegator", delegator);
        }

        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        if (dispatcher == null) {
            dispatcher = (LocalDispatcher) session.getAttribute("dispatcher");
        }
        if (dispatcher == null) {
            dispatcher = (LocalDispatcher) servletContext.getAttribute("dispatcher");
        }
        if (dispatcher == null) {
            dispatcher = WebAppUtil.makeWebappDispatcher(servletContext, delegator);
        }
        if (dispatcher != null) {
            request.setAttribute("dispatcher", dispatcher);
            servletContext.setAttribute("dispatcher", dispatcher);
        }

        Security security = (Security) session.getAttribute("security");
        if (security == null) {
            security = (Security) servletContext.getAttribute("security");
            if (security == null && delegator != null) {
                try {
                    security = SecurityFactory.getInstance(delegator);
                } catch (SecurityConfigurationException e) {
                    Debug.logError(e, "Unable to obtain an instance of the security object.", MODULE);
                }
            }
            if (security != null) {
                request.setAttribute("security", security);
                servletContext.setAttribute("security", security);
            }
        }
    }

    public static String checkBasicLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, NoSuchFieldException, IllegalAccessException {
        String authHeader = request.getHeader("authorization");
        if (UtilValidate.isEmpty(authHeader)) {
            authHeader = request.getHeader("Authorization");
        }
        if (UtilValidate.isNotEmpty(authHeader) && authHeader.startsWith("Basic ")) {
            authHeader = authHeader.replace("Basic ", "");
            authHeader = new String(Base64.getDecoder().decode(authHeader), StandardCharsets.UTF_8);
            List<String> splits = StringUtil.split(authHeader, ":");
            Debug.logInfo("  - authorization: " + splits, MODULE);
            if (splits.size() == 2) {
                String username = splits.get(0);
                String password = splits.get(1);
                RequestFacade requestFacade = (RequestFacade) request;
                Field declaredField = requestFacade.getClass().getDeclaredField("request");
                declaredField.setAccessible(true);

                Object requestObject = declaredField.get(request);
                Field coyoteRequest = requestObject.getClass().getDeclaredField("coyoteRequest");
                coyoteRequest.setAccessible(true);

                Object cro = coyoteRequest.get(requestObject);
                Field parametersField = cro.getClass().getDeclaredField("parameters");
                parametersField.setAccessible(true);

                Parameters parameters = (Parameters) parametersField.get(cro);
                parameters.addParameter("USERNAME", username);
                parameters.addParameter("PASSWORD", password);
                Debug.logInfo("  - username: " + username, MODULE);
                Debug.logVerbose("  - password: " + password, MODULE);
                return LoginWorker.checkLogin(request, response);
            }
        }
        return "success";
    }
}
