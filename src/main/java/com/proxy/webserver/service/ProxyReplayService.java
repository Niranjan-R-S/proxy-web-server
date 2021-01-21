package com.proxy.webserver.service;

import com.proxy.webserver.exception.RequestMalformedException;
import com.proxy.webserver.model.RequestParams;
import com.proxy.webserver.utils.RequestClient;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class ProxyReplayService {

    public HashMap<String, Object> replayRequest(RequestParams requestParams) throws IOException, RequestMalformedException {
        this.validateParameters(requestParams);
        return RequestClient.makeRequest(requestParams);
    }

    private void validateParameters(RequestParams requestParams) throws RequestMalformedException {
        ArrayList<String> allowedRequestTypes = new ArrayList<>();
        allowedRequestTypes.add("GET");
        allowedRequestTypes.add("POST");
        allowedRequestTypes.add("PUT");
        allowedRequestTypes.add("DELETE");

        if(Strings.isBlank(requestParams.getClientID())){
            throw new RequestMalformedException("ClientID cannot be empty");
        } else if (Strings.isBlank(requestParams.getURL())){
            throw new RequestMalformedException("URL cannot be empty");
        } else if (!allowedRequestTypes.contains(requestParams.getHttpRequestType())){
            throw new RequestMalformedException("Allowed values for request types are GET, PUT, POST and Delete");
        }

    }

}
