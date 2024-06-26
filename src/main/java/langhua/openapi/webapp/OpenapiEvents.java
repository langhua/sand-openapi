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
package langhua.openapi.webapp;

import org.apache.ofbiz.base.lang.JSON;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilHttp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Openapi Events
 */
public class OpenapiEvents {

    public static final String MODULE = OpenapiEvents.class.getName();

    private static final String[] IGNOREATTRS = new String[] {
        // Attributes removed for security reason
        "javax.servlet.request.key_size",
        "_CONTEXT_ROOT_",
        "_FORWARDED_FROM_SERVLET_",
        "javax.servlet.request.ssl_session",
        "javax.servlet.request.ssl_session_id",
        "multiPartMap",
        "javax.servlet.request.cipher_suite",
        "targetRequestUri",
        "_SERVER_ROOT_URL_",
        "_CONTROL_PATH_",
        "thisRequestUri",
        "org.apache.tomcat.util.net.secure_protocol_version",
        "userLogin",
        "accessToken",
        "impersonateLogin",
        "org.apache.logging.log4j.web.Log4jServletFilter.FILTERED",
        "org.apache.tomcat.util.net.secure_requested_protocol_versions",
        "org.apache.tomcat.util.net.secure_requested_ciphers",
        "requestMapMap",
        "javax.servlet.forward.context_path",
        "javax.servlet.forward.servlet_path",
        "javax.servlet.forward.request_uri",
        "javax.servlet.forward.path_info",
        "OpenapiAuthnFilter.FILTERED",
        "javax.servlet.forward.query_string",
        "Set-Cookie",
        "USERNAME",
        "PASSWORD"
    };

    public static String jsonResponse(HttpServletRequest request, HttpServletResponse response) {
        // pull out the service response from the request attribute
        Map<String, Object> attrMap = UtilHttp.getJSONAttributeMap(request);

        for (String ignoreAttr : IGNOREATTRS) {
            if (attrMap.containsKey(ignoreAttr)) {
                attrMap.remove(ignoreAttr);
            }
        }
        int status = 200;
        if (attrMap.containsKey("status")) {
            try {
                status = Integer.parseInt(String.valueOf(attrMap.get("status")));
            } catch (NumberFormatException e) {
                Debug.logError(e, MODULE);
            }
        }
        if (attrMap.containsKey("_ERROR_MESSAGE_")) {
            String message = (String) attrMap.remove("_ERROR_MESSAGE_");
            attrMap.put("error", message);
        }
        if (attrMap.containsKey("_ERROR_MESSAGE_LIST")) {
            Set<String> errors = new HashSet<>();
            @SuppressWarnings("unchecked")
            List<String> errorMessages = (List<String>) attrMap.remove("_ERROR_MESSAGE_LIST");
            for (String errorMessage : errorMessages) {
                errors.add(errorMessage);
            }
            attrMap.put("error_details", errors);
        }
        response.setStatus(status);

        try {
            JSON json = JSON.from(attrMap);
            writeJSONtoResponse(json, request, response);
        } catch (IOException e) {
            Debug.logError(e, MODULE);
            return "error";
        }
        return "success";
    }

    private static void writeJSONtoResponse(JSON json, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        String jsonStr = json.toString();

        // This was added for security reason (OFBIZ-5409), you might need to remove the "//" prefix when handling the JSON response
        // Though normally you simply have to access the data you want, so should not be annoyed by the "//" prefix
//        if ("GET".equalsIgnoreCase(httpMethod)) {
//            Debug.logWarning("for security reason (OFBIZ-5409) the the '//' prefix was added handling the JSON response.  "
//                    + "Normally you simply have to access the data you want, so should not be annoyed by the '//' prefix."
//                    + "You might need to remove it if you use Ajax GET responses (not recommended)."
//                    + "In case, the util.js scrpt is there to help you."
//                    + "This can be customized in general.properties with the http.json.xssi.prefix property", MODULE);
//            Delegator delegator = (Delegator) request.getAttribute("delegator");
//            String xssiPrefix = EntityUtilProperties.getPropertyValue("general", "http.json.xssi.prefix", delegator);
//            jsonStr = xssiPrefix + jsonStr;
//        }

        // set the JSON content type
        response.setContentType("application/json; charset=" + StandardCharsets.UTF_8);
        // jsonStr.length is not reliable for unicode characters
        response.setContentLength(jsonStr.getBytes(StandardCharsets.UTF_8).length);

        // return the JSON String
        Writer out;
        try {
            out = response.getWriter();
            out.write(jsonStr);
            out.flush();
        } catch (IOException e) {
            Debug.logError(e, MODULE);
        }
    }
}
