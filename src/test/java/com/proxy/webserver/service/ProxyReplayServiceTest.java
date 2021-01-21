package com.proxy.webserver.service;

import com.proxy.webserver.model.RequestParams;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;


@SpringBootTest
@ActiveProfiles("test")
class ProxyReplayServiceTest {

    @Autowired
    private ProxyReplayService proxyReplayService;

    @Test
    public void shouldReplayRequest() throws Exception {
        HashMap<?,?> headers = new HashMap<>();
        RequestParams requestParams = new RequestParams("MyName", "https://jsonplaceholder.typicode.com/todos/1", headers,
                "", "GET");

        HashMap<String, Object> response = proxyReplayService.replayRequest(requestParams);
    }
}