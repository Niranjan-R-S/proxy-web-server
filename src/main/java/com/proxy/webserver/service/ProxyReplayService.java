package com.proxy.webserver.service;

import com.proxy.webserver.model.RequestParams;
import com.proxy.webserver.utils.RequestClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;

@Service
public class ProxyReplayService {

    public HashMap<String, Object> replayRequest(RequestParams requestParams) throws IOException {
        return RequestClient.makeRequest(requestParams);
    }

}
