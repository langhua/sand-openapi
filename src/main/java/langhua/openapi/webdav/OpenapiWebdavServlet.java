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
package langhua.openapi.webdav;

import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceSet;
import org.apache.catalina.servlets.WebdavServlet;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.FileUtil;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.security.Security;
import org.apache.ofbiz.security.SecurityFactory;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceContainer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serial;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;

public class OpenapiWebdavServlet extends WebdavServlet {
    private static final String MODULE = OpenapiWebdavServlet.class.getName();
    @Serial
    private static final long serialVersionUID = 1L;
    private static Delegator delegator;
    private static LocalDispatcher dispatcher;
    private Security security;

    /**
     * The sorting manager for sorting files and directories.
     */
    protected transient JsonSortManager jsonSortManager;

    private static boolean isMainSet = false;

    public static final String WEBDAV_ROOT = UtilProperties.getPropertyValue("sandav", "sandav.webdav.root", "runtime/openapi/");

    private static Method setMainResourcesMethod;
    static {
        Method[] methods = StandardRoot.class.getDeclaredMethods();
        for (Method method : methods) {
            if ("setMainResources".equals(method.getName())) {
                setMainResourcesMethod = method;
                break;
            }
        }
    }

    @Override
    public void init() throws ServletException {
        try {
            super.init();
            ServletContext context = this.getServletContext();
            String delegatorName = context.getInitParameter("entityDelegatorName");
            delegator = DelegatorFactory.getDelegator(delegatorName);
            String dispatcherName = context.getInitParameter("localDispatcherName");
            dispatcher = ServiceContainer.getLocalDispatcher(dispatcherName, delegator);
            this.security = SecurityFactory.getInstance(delegator);
        } catch (Exception e) {
            Debug.logError(e, "Error while initializing sand-openapi webdav servlet: ", MODULE);
            throw new ServletException(e);
        }

        // reset WebResourceRoot to WEBDAV_ROOT configured
        if (!isMainSet && resources != null && resources instanceof StandardRoot) {
            File file = FileUtil.getFile(WEBDAV_ROOT);
            if (!file.exists()) {
                file.mkdirs();
            }
            WebResourceSet webResourceSet = new DirResourceSet(resources, "/", file.getAbsolutePath(), "/");
            try {
                setMainResourcesMethod.setAccessible(true);
                setMainResourcesMethod.invoke(resources, webResourceSet);
                setMainResourcesMethod.setAccessible(false);
                isMainSet = true;
            } catch (IllegalAccessException | InvocationTargetException e) {
                Debug.logError(e, MODULE);
            }
        }

        jsonSortManager = new JsonSortManager();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Cache-Control", "no-store");
        resp.setHeader(HttpHeaders.SET_COOKIE, "");
        super.service(req, resp);
    }

    @Override
    protected InputStream render(HttpServletRequest request, String contextPath, WebResource resource, String encoding)
            throws IOException, ServletException {
        // return json contents of file tree
        // ref: https://element-plus.org/zh-CN/component/tree.html
        return renderJsonTree(contextPath, resource);
    }

    /**
     * Return an InputStream to a Json representation of the contents of this
     * directory.
     *
     * @param contextPath Context path to which our internal paths are relative
     * @param resource    The associated resource
     *
     * @return the Json data
     *
     */
    protected InputStream renderJsonTree(String contextPath, WebResource resource) {
        // Prepare a writer to a buffered area
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        OutputStreamWriter osWriter = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
        PrintWriter writer = new PrintWriter(osWriter);

        StringBuilder sb = new StringBuilder();

        String directoryWebappPath = resource.getWebappPath();
        WebResource[] entries = resources.listResources(directoryWebappPath);
        jsonSortManager.sort(entries);

        // rewriteUrl(contextPath) is expensive. cache result for later reuse
        String rewrittenContextPath = rewriteUrl(directoryWebappPath);

        // Render the page header
        sb.append("{");
        sb.append("\"resources\":[");
        sb.append("{\"name\":\"" + directoryWebappPath + "\", \"type\":\"Dir\", \"path\":\"");
        sb.append(contextPath + rewrittenContextPath);
        if (entries.length > 0) {
            sb.append("\",\"isLeaf\":\"false\"");
        } else {
            sb.append("\",\"isLeaf\":\"true\"");
        }
        sb.append(",\"resources\":[");
        ouputResources(sb, entries, contextPath);
        sb.append("]}]}");

        // Return an input stream to the underlying bytes
        writer.write(sb.toString());
        writer.flush();
        return new ByteArrayInputStream(stream.toByteArray());
    }

    private void ouputResources(StringBuilder sb, WebResource[] entries, String contextPath) {
        boolean isFirst = true;
        for (WebResource childResource : entries) {
            String filename = childResource.getName();
            if (filename.equalsIgnoreCase("WEB-INF") ||
                    filename.equalsIgnoreCase("META-INF")) {
                continue;
            }

            if (!childResource.exists()) {
                continue;
            }

            if (!isFirst) {
                sb.append(",");
            } else {
                isFirst = false;
            }
            sb.append("{");
            sb.append("\"name\":\"");
            sb.append(childResource.getName());
            sb.append("\",\"type\":\"");
            if (childResource.isDirectory()) {
                sb.append("Dir");
            } else {
                sb.append("File");
            }
            sb.append("\",\"path\":\"");
            sb.append(contextPath);
            sb.append(rewriteUrl(childResource.getWebappPath()));
            if (childResource.isDirectory()) {
                sb.append("/");
                WebResource[] subEntries = resources.listResources(childResource.getWebappPath());
                if (subEntries != null && subEntries.length > 0) {
                    sb.append("\",\"isLeaf\":\"false\"");
                } else {
                    sb.append("\",\"isLeaf\":\"true\"");
                }
            } else {
                sb.append("\",\"isLeaf\":\"true\"");
            }
            sb.append("}");
        }
    }

    /**
     * A class encapsulating the sorting of resources.
     */
    private static class JsonSortManager {
        /**
         * Comparator to use when sorting resources by name.
         */
        protected Comparator<WebResource> resourceNameComparator;

        JsonSortManager() {
            resourceNameComparator = comparingTrueFirst(WebResource::isDirectory);
        }

        public void sort(WebResource[] resources) {
            if(null != resourceNameComparator) {
                Arrays.sort(resources, resourceNameComparator);
            }
        }
    }

    private static Comparator<WebResource> comparingTrueFirst(Function<WebResource,Boolean> keyExtractor) {
        return (s1, s2) -> {
            Boolean r1 = keyExtractor.apply(s1);
            Boolean r2 = keyExtractor.apply(s2);
            if (r1.booleanValue()) {
                if (r2.booleanValue()) {
                    return 0;
                } else {
                    return -1; // r1 (property is true) first
                }
            } else if (r2.booleanValue()) {
                return 1; // r2 (property is true) first
            } else {
                return 0;
            }
        };
    }
}
