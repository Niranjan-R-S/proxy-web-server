package com.proxy.webserver.model;

import org.apache.logging.log4j.util.Strings;

import java.util.HashMap;

public class RequestParams {
    private String ClientID;
    private String URL;
    private HashMap<?, ?> Headers;
    private String RequestBody;
    private String HttpRequestType;

    public RequestParams(String ClientID, String URL, HashMap<?, ?> Headers, String RequestBody, String HttpRequestType) {
        this.ClientID = ClientID;
        this.URL = URL;
        this.Headers = Headers;
        this.RequestBody = RequestBody;
        this.HttpRequestType = HttpRequestType;
    }

    public String getClientID() {
        return this.ClientID;
    }

    public void setClientID(String clientID) {
        this.ClientID = clientID;
    }

    public String getURL() {
        return this.URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public HashMap<?, ?> getHeaders() {
        return this.Headers;
    }

    public void setHeaders(HashMap<?, ?> headers) {
        this.Headers = headers;
    }

    public String getRequestBody() {
        return this.RequestBody;
    }

    public void setRequestBody(String requestBody) {
        this.RequestBody = requestBody;
    }

    public String getHttpRequestType() {
        return Strings.toRootUpperCase(this.HttpRequestType);
    }

    public void setHttpRequestType(String httpRequestType) {
        this.HttpRequestType = httpRequestType;
    }
}
