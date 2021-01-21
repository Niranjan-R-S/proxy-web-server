package com.proxy.webserver.service;

import com.proxy.webserver.exception.ProtocolNotSupportedException;
import com.proxy.webserver.exception.RequestMalformedException;
import com.proxy.webserver.model.RequestParams;
import com.proxy.webserver.utils.RequestClient;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.*;

@Service
public class ProxyReplayService {

    public HashMap<String, Object> replayRequest(RequestParams requestParams, String ClientID) throws IOException, RequestMalformedException, ProtocolNotSupportedException, InterruptedException, ExecutionException, TimeoutException {
        this.validateParameters(requestParams);
        ExecutorService executor = Executors.newCachedThreadPool();
        Callable<HashMap<String, Object>> task = new Callable<HashMap<String, Object>>() {
            public HashMap<String, Object> call() throws IOException {
                return RequestClient.makeRequest(requestParams);
            }
        };
        Future<HashMap<String, Object>> future = executor.submit(task);
        HashMap<String, Object> result = future.get(5, TimeUnit.SECONDS);
        future.cancel(true);
        return result;
    }

    private void validateParameters(RequestParams requestParams) throws RequestMalformedException, MalformedURLException, ProtocolNotSupportedException {
        ArrayList<String> allowedRequestTypes = new ArrayList<>();
        allowedRequestTypes.add("GET");
        allowedRequestTypes.add("POST");
        allowedRequestTypes.add("PUT");
        allowedRequestTypes.add("DELETE");

        if (Strings.isBlank(requestParams.getURL())){
            throw new RequestMalformedException("URL cannot be empty");
        } else if (!allowedRequestTypes.contains(requestParams.getHttpRequestType())){
            throw new RequestMalformedException("Allowed values for request types are GET, PUT, POST and Delete");
        }


        URL url = new URL(requestParams.getURL());
        if(!url.getProtocol().equals("https")){
            throw new ProtocolNotSupportedException("Only http calls are supported");
        }
    }

}
