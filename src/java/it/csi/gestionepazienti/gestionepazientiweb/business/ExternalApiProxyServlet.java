package it.csi.gestionepazienti.gestionepazientiweb.business;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.impl.common.IOUtil;

import it.csi.gestionepazienti.gestionepazientiweb.dto.custom.UserLogged;

public class ExternalApiProxyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String USERINFO_SESSIONATTR = "appDatacurrentUser";

	protected static Logger log = Logger.getLogger(ExternalApiProxyServlet.class);

	private static final File FILE_UPLOAD_TEMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
	private static final String STRING_CONTENT_TYPE_HEADER_NAME = "Content-Type";
	private int intMaxFileUploadSize = 5 * 1024 * 1024;

	protected String apiBaseUrl;

	@Override
	public void init(ServletConfig config) throws ServletException {
		this.apiBaseUrl = config.getInitParameter("apiBaseUrl");
		
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		GetMethod getMethod = new GetMethod(createTargetUrlWithParameters(request));

		setHeaderShibboleth(request, getMethod);


		HttpClient httpclient = new HttpClient();
		int result = httpclient.executeMethod(getMethod);
		response.setStatus(result);
		log.info("[ApiProxyServlet::doGet] Content-Type: " + getMethod.getResponseHeader("Content-Type"));
		if (getMethod.getResponseHeader("Content-Type") != null)
			response.setContentType(getMethod.getResponseHeader("Content-Type").getValue());
		log.info("[ApiProxyServlet::doGet] getResponseCharSet: " + getMethod.getResponseCharSet());

		log.info("[ApiProxyServlet::doGet] response.getCharacterEncoding: " + response.getCharacterEncoding());
		log.info("[ApiProxyServlet::doGet] response.getContentType: " + response.getContentType());
		Header contentDisposition = getMethod.getResponseHeader("Content-Disposition");
		if (contentDisposition != null)
			response.setHeader("Content-Disposition", getMethod.getResponseHeader("Content-Disposition").getValue());

		//raffa
//		if (response.getContentType() != null && 
//				(response.getContentType().startsWith("application/octet-stream")
//						||response.getContentType().startsWith("image/")
//						|| response.getContentType().startsWith("text/csv")
//						|| response.getContentType().startsWith("application/xlsx"))) {
		if (response.getContentType() != null && 
				(response.getContentType().startsWith("application/")
						||response.getContentType().startsWith("image/")
						|| response.getContentType().startsWith("text/csv")
						)) {
			ServletOutputStream out = response.getOutputStream();
			InputStream in = getMethod.getResponseBodyAsStream();
			log.info("[ApiProxyServlet::doGet] startcopy");
			IOUtil.copyCompletely(in, out);
//		IOUtils.copyLarge(in, out);
			log.info("[ApiProxyServlet::doGet] stopcopy");
			in.close();
			out.close();
		} else {
			byte[] responsBytes = getMethod.getResponseBody();
			response.setCharacterEncoding("UTF-8");
			String jsonOut = new String(responsBytes, "UTF-8");
			if (isJSONPRequest(request))
				jsonOut = getCallbackMethod(request) + "(" + jsonOut + ")";
			PrintWriter out = response.getWriter();
			out.println(jsonOut);
			out.close();
		}

	}

	private void setHeaderShibboleth(HttpServletRequest request, HttpMethod method) {
		/*							 x_rupar_id'
        - $ref: '#/parameters/header_x_application_id'
        - $ref: '#/parameters/header_x_request_id'
        - $ref: '#/parameters/header_x_forwarded_for'
        - $ref: '#/parameters/header_user_agent
		 */
		// recupero info utente da sessione
		log.info("[[ExternalApiProxyServlet::setHeaderShibboleth]] Begin ");
		String rupar_id = request.getHeader("Shib-Identita-CodiceFiscale");
		log.info("[[ExternalApiProxyServlet::setHeaderShibboleth]] rupar_id from header "+rupar_id);
		if (rupar_id==null)
		{
			UserLogged userLogged = (UserLogged)request.getSession().getAttribute(USERINFO_SESSIONATTR);
			if (userLogged!=null)
				rupar_id = userLogged.getCfUtente();
			log.info("[[ExternalApiProxyServlet::setHeaderShibboleth]] rupar_id from session"+rupar_id);
		}
		
		//String application_id = "Medico MMG"; 
		//Raffa: commento cosi da poter richiamare le api usca da qualsiasi servizio
		String application_id = "APPLICAZIONE_ESTERNA"; 
		String forwarded_for = request.getHeader("X-Forwarded-For");
		String request_id = UUID.randomUUID().toString();
		
		method.setRequestHeader("Shib-Identita-CodiceFiscale", rupar_id);
		method.setRequestHeader("X-Forwarded-For", forwarded_for);
		method.setRequestHeader("X-Applicazione-Codice", application_id);
		method.setRequestHeader("User-Agent", request.getHeader("User-Agent"));
		method.setRequestHeader("X-Request-Id", request_id);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("[ApiProxyServlet::doPost] START");
		try {

			RequestEntity requestBody = null;
			String contentType = request.getContentType();
			String targetUrl = createTargetUrlWithParameters(request);

			PostMethod post = new PostMethod(targetUrl);
			setHeaderShibboleth(request,post);
			
			if(request.getQueryString()==null)
				post.setQueryString((String)null);
			else if(request.getQueryString().equals(""))
				post.setQueryString("");

			if (request.getCookies() != null && request.getCookies().length > 0) {
				String cookies = "";
				for (Cookie cookie : request.getCookies()) {
					cookies += cookie.getName() + "=" + cookie.getValue() + ";";
				}
				post.setRequestHeader("Cookie", cookies);
			}
			log.debug("[ApiProxyServlet::doPost] - targetUrl: " + targetUrl);

			if (contentType.startsWith("multipart/form-data")) {
				requestBody = handleMultipartPost(request, post.getParams());
			} else if (contentType.startsWith("application/x-www-form-urlencoded")) {
				Enumeration<String> parameterNames = request.getParameterNames();
				while (parameterNames.hasMoreElements()) {
					String paramName = parameterNames.nextElement();
					post.addParameter(new NameValuePair(paramName, request.getParameter(paramName)));
				}
				

				
				
				
			} else {
				StringBuffer inBodyRequest = new StringBuffer();
				String line = null;
				try {
					BufferedReader reader = request.getReader();
					while ((line = reader.readLine()) != null) {
						inBodyRequest.append(line);
						log.debug("[ApiProxyServlet::doPost] - request body: " + line);
					}
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				requestBody = new StringRequestEntity(inBodyRequest.toString(), request.getContentType(), request.getCharacterEncoding());
			}

			if(requestBody!=null){
				post.setRequestEntity(requestBody);
				post.setRequestHeader(STRING_CONTENT_TYPE_HEADER_NAME, requestBody.getContentType());
			}
			PrintWriter out = response.getWriter();
			HttpClient httpclient = new HttpClient();
			try {
				int result = httpclient.executeMethod(post);
				log.debug("[ApiProxyServlet::doPost] - post result: " + result);
				response.setStatus(result);
				out.println(post.getResponseBodyAsString());
			} finally {
				post.releaseConnection();
			}

		} catch (IOException e) {
			log.error("[ApiProxyServlet::doPost] ERROR IOException: " + e.getMessage());
			throw e;
		} finally {
			log.debug("[ApiProxyServlet::doPost] END");
		}
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("[ApiProxyServlet::doPut] START");
		try {
			StringBuffer inBodyRequest = new StringBuffer();
			String line = null;
			try {
				BufferedReader reader = request.getReader();
				while ((line = reader.readLine()) != null) {
					inBodyRequest.append(line);
					log.debug("[ApiProxyServlet::doPut] - request body: " + line);
				}
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			String targetUrl = createTargetUrlWithParameters(request);
			PutMethod put = new PutMethod(targetUrl);
			setHeaderShibboleth(request,put);

			RequestEntity requestBody = new StringRequestEntity(inBodyRequest.toString(), " application/json", request.getCharacterEncoding());
			log.debug("[ApiProxyServlet::doPut] - targetUrl: " + targetUrl);

			put.setRequestEntity(requestBody);
			PrintWriter out = response.getWriter();
			HttpClient httpclient = new HttpClient();
			try {
				int result = httpclient.executeMethod(put);
				log.debug("[ApiProxyServlet::doPut] - post result: " + result);
				response.setStatus(result);
				out.println(put.getResponseBodyAsString());
			} finally {
				put.releaseConnection();
			}

		} catch (IOException e) {
			log.error("[ApiProxyServlet::doPut] ERROR IOException: " + e.getMessage());
			throw e;
		} finally {
			log.debug("[ApiProxyServlet::doPut] END");
		}
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// System.out.println(req.getRequestURL());
		// super.doDelete(req, resp);

		DeleteMethod delMethod = new DeleteMethod(createTargetUrlWithParameters(request));

		setHeaderShibboleth(request,delMethod);

		HttpClient httpclient = new HttpClient();
		int result = httpclient.executeMethod(delMethod);
		response.setStatus(result);
		log.info("[ApiProxyServlet::doDelete] Content-Type: " + delMethod.getResponseHeader("Content-Type"));
		if (delMethod.getResponseHeader("Content-Type") != null)
			response.setContentType(delMethod.getResponseHeader("Content-Type").getValue());
		log.info("[ApiProxyServlet::doDelete] getResponseCharSet: " + delMethod.getResponseCharSet());
		response.setCharacterEncoding("UTF-8");
		// if(getMethod.getResponseCharSet()==null)
		// response.setCharacterEncoding("UTF-8");
		// else
		// response.setCharacterEncoding(getMethod.getResponseCharSet());

		log.info("[ApiProxyServlet::doDelete] response.getCharacterEncoding: " + response.getCharacterEncoding());
		log.info("[ApiProxyServlet::doDelete] response.getContentType: " + response.getContentType());
		// for (Header header : getMethod.getResponseHeaders()) {
		// System.out.println(header.getName() + "-"+header.getValue());
		// }
		// Header contentDisposition =
		// getMethod.getResponseHeader("Content-Disposition");
		// if(contentDisposition!=null)
		// response.setHeader("Content-Disposition",
		// getMethod.getResponseHeader("Content-Disposition").getValue());

		// String jsonOut = getMethod.getResponseBodyAsString();
		byte[] responsBytes = delMethod.getResponseBody();
		String jsonOut = new String(responsBytes, "UTF-8");
		// if(getMethod.getResponseHeader("Content-Type")!=null){
		// String contentType =
		// getMethod.getResponseHeader("Content-Type").getValue();
		// if(contentType!= null && contentType.lastIndexOf("charset")<0){
		// byte[] responseISO_8859_1 = getMethod.getResponseBody();
		// String stringISO_8859_1 = new String(responseISO_8859_1,
		// "ISO-8859-1");
		// String stringUTF_8 = new String(responseISO_8859_1, "UTF-8");
		// byte[] responseUTF_8 = new String(responseISO_8859_1,
		// "ISO-8859-1").getBytes("UTF-8");
		// //jsonOut = new String(responseUTF_8);
		// jsonOut =stringUTF_8;
		// }
		// }

		// String jsonOut = getMethod.getResponseBodyAsString();
		if (isJSONPRequest(request))
			jsonOut = getCallbackMethod(request) + "(" + jsonOut + ")";
		PrintWriter out = response.getWriter();
		out.println(jsonOut);
		out.close();
	} 

	private String getCallbackMethod(HttpServletRequest httpRequest) throws ServletException {
		String callback = httpRequest.getParameter("callback");
		if(callback!=null && !Pattern.matches("^[a-zA-Z0-9_.]*", callback))
			throw new ServletException("invalid callback method name");
		
		return callback;
	}
	
	public static void main(String[] args) {
		if(!Pattern.matches("^[a-zA-Z0-9_.]*", "a.a_a3"))
			System.out.println("errore");
		else
			System.out.println("continua");
		System.out.println("Matches: " + Pattern.matches("^[a-zA-Z0-9_.]*", "<script>"));
	}

	private boolean isJSONPRequest(HttpServletRequest httpRequest) throws ServletException {
		String callbackMethod = getCallbackMethod(httpRequest);
		return (callbackMethod != null && callbackMethod.length() > 0);
	}

	protected String cleanParameters(Map<String, String[]> parameterMap) throws UnsupportedEncodingException {
		String parametersOut = "?";
		if (parameterMap != null && parameterMap.size() > 0) {
			int i = 0;
			for (String key : parameterMap.keySet()) {
				i++;
				if (!key.trim().equalsIgnoreCase("callback")) {
					parametersOut += key + "=" + URLEncoder.encode(parameterMap.get(key)[0], "UTF-8").replace("[+]", "%2B").replace("+", "%20");
					if (i < parameterMap.size()) {
						parametersOut += "&";
					}
				}
			}
		}
		if (parametersOut.equals("?"))
			parametersOut = "";

		return parametersOut;
	}

	protected String createTargetUrlWithParameters(HttpServletRequest request) throws IOException {

		// FIXME workaround to force security in the datadiscovery
		// String tenantCode =
		// AuthorizeUtils.getTenantsInSession(request).get(0).getTenantCode();

		Map<String, String[]> parameterMap = new HashMap<String, String[]>(request.getParameterMap());

		String parameters = cleanParameters(parameterMap);
		String path = request.getRequestURI() + parameters;

		path = path.replaceAll(request.getContextPath() + request.getServletPath(), "");

		return apiBaseUrl + path;

	}

	@SuppressWarnings("unused")
	private void allowClientOrigin(HttpServletResponse response, String clientOrigin, String methods) {
		response.setHeader("Access-Control-Allow-Origin", clientOrigin);
		response.setHeader("Access-Control-Allow-Methods", methods);
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		response.setHeader("Access-Control-Max-Age", "86400");

	}

	private MultipartRequestEntity handleMultipartPost(HttpServletRequest httpServletRequest, HttpMethodParams params) throws ServletException {
		MultipartRequestEntity multipartRequestEntity = null;
		DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
		diskFileItemFactory.setSizeThreshold(this.getMaxFileUploadSize());
		diskFileItemFactory.setRepository(FILE_UPLOAD_TEMP_DIRECTORY);
		ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);
		try {
			List<FileItem> listFileItems = servletFileUpload.parseRequest(httpServletRequest);
			List<Part> listParts = new ArrayList<Part>();
			for (FileItem fileItemCurrent : listFileItems) {
				if (fileItemCurrent.isFormField()) {
					String value = "";
					try {
						value = new String(fileItemCurrent.getString().getBytes("iso-8859-1"), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						value = fileItemCurrent.getString();
						e.printStackTrace();
					}
					StringPart stringPart = new StringPart(fileItemCurrent.getFieldName(), value, "UTF-8");
					listParts.add(stringPart);
				} else {
					FilePart filePart = new FilePart(fileItemCurrent.getFieldName(), new ByteArrayPartSource(fileItemCurrent.getName(), fileItemCurrent.get()));
					listParts.add(filePart);
				}
			}

			multipartRequestEntity = new MultipartRequestEntity(listParts.toArray(new Part[] {}), params);
		} catch (FileUploadException fileUploadException) {
			throw new ServletException(fileUploadException);
		}
		return multipartRequestEntity;
	}

	private int getMaxFileUploadSize() {
		return this.intMaxFileUploadSize;
	}
}