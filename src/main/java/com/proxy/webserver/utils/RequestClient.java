package com.proxy.webserver.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proxy.webserver.model.RequestParams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RequestClient {
    public static HashMap<String, Object> makeRequest(RequestParams requestParams) throws IOException {
        HashMap<String,Object> proxyResponse = new HashMap<>();

        ArrayList<String> updateMethods = new ArrayList<>();
        updateMethods.add("POST");
        updateMethods.add("PUT");
        updateMethods.add("PATCH");

        URL siteUrl = new URL(requestParams.getURL());
        HttpURLConnection connection = (HttpURLConnection) siteUrl.openConnection();
        connection.setDoOutput(true);

        connection.setRequestMethod(requestParams.getHttpRequestType());

        for (Map.Entry<?,?> entry : requestParams.getHeaders().entrySet())
            connection.setRequestProperty(entry.getKey().toString(), entry.getValue().toString());

        if(updateMethods.contains(requestParams.getHttpRequestType())){
            OutputStream os = connection.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            osw.write(requestParams.getRequestBody());
        }

        InputStream responseStream = connection.getInputStream();

        ObjectMapper mapper = new ObjectMapper();
        HashMap<?,?> response = mapper.readValue(responseStream, HashMap.class);

        proxyResponse.put("statusCode", connection.getResponseCode());
        proxyResponse.put("response", response);

        connection.disconnect();

        return proxyResponse;
    }
}
