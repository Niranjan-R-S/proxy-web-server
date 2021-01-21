package com.proxy.webserver.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proxy.webserver.model.RequestParams;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RequestClient {
    public static HashMap<String, Object> makeRequest(RequestParams requestParams) throws IOException {
        HashMap<String,Object> proxyResponse = new HashMap<>();
        URL siteUrl = new URL(requestParams.getURL());
        HttpURLConnection connection = (HttpURLConnection) siteUrl.openConnection();
        connection.setDoOutput(true);

        connection.setRequestMethod(requestParams.getHttpRequestType());

        for (Map.Entry<?,?> entry : requestParams.getHeaders().entrySet())
            connection.setRequestProperty(entry.getKey().toString(), entry.getValue().toString());

        InputStream responseStream = connection.getInputStream();

        ObjectMapper mapper = new ObjectMapper();
        HashMap<?,?> response = mapper.readValue(responseStream, HashMap.class);

        proxyResponse.put("statusCode", connection.getResponseCode());
        proxyResponse.put("response", response);

        connection.disconnect();

        return proxyResponse;
    }
}
