package org.guppy4j.web.http;

/**
 * Serves requests with responses
 */
public interface IServer {

    /**
     * Common mime type for dynamic content: plain text
     */
    String MIME_PLAINTEXT = "text/plain";

    /**
     * Common mime type for dynamic content: html
     */
    String MIME_HTML = "text/html";

    /**
     * Pseudo-Parameter to use to store the actual query string in the parameters map for later re-processing.
     */
    String QUERY_STRING_PARAMETER = "HttpServer.QUERY_STRING";

    /**
     * @param request An HTTP request
     * @return An HTTP response
     */
    Response serve(IRequest request);
}