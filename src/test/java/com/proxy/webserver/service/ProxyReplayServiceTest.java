package com.proxy.webserver.service;

import com.proxy.webserver.exception.RequestMalformedException;
import com.proxy.webserver.model.RequestParams;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@ActiveProfiles("test")
class ProxyReplayServiceTest {

    @Autowired
    private ProxyReplayService proxyReplayService;

    @Test
    public void shouldReplayGetRequest() throws Exception {
        HashMap<?,?> headers = new HashMap<>();
        RequestParams requestParams = new RequestParams("MyName", "https://jsonplaceholder.typicode.com/todos/1", headers,
                "", "GET");

        HashMap<String, Object> response = proxyReplayService.replayRequest(requestParams);

        assertEquals(200, response.get("statusCode"));
    }

    @Test
    public void shouldReplayPostRequest() throws Exception {
        HashMap<?,?> headers = new HashMap<>();
        RequestParams requestParams = new RequestParams("MyName", "https://jsonplaceholder.typicode.com/posts", headers,
                "{'title':'foo','body':'bar','userId':1}", "Post");

        HashMap<String, Object> response = proxyReplayService.replayRequest(requestParams);

        assertEquals(201, response.get("statusCode"));
    }

    @Test
    public void shouldReplayPutRequest() throws Exception {
        HashMap<String,String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        headers.put("charset", "UTF-8");
        RequestParams requestParams = new RequestParams("MyName", "https://jsonplaceholder.typicode.com/posts/1", headers,
                "{'id':1,'title':'foo','body':'bar','userId':1}", "Put");

        HashMap<String, Object> response = proxyReplayService.replayRequest(requestParams);

        assertEquals(200, response.get("statusCode"));
    }

    @Test
    public void shouldReplayDeleteRequest() throws Exception {
        HashMap<String,String> headers = new HashMap<>();
        RequestParams requestParams = new RequestParams("MyName", "https://jsonplaceholder.typicode.com/posts/1", headers,
                "", "Delete");

        HashMap<String, Object> response = proxyReplayService.replayRequest(requestParams);

        assertEquals(200, response.get("statusCode"));
    }

    @Test
    public void shouldThrowMalformedRequestIfClientIDIsEmpty() throws Exception {
        HashMap<?,?> headers = new HashMap<>();
        RequestParams requestParams = new RequestParams("", "https://jsonplaceholder.typicode.com/todos/1", headers,
                "", "GET");

        assertThrows(RequestMalformedException.class, ()->proxyReplayService.replayRequest(requestParams));
    }

    @Test
    public void shouldThrowMalformedRequestIfRequestTypeIsNotValid() throws Exception {
        HashMap<?,?> headers = new HashMap<>();
        RequestParams requestParams = new RequestParams("MyName", "https://jsonplaceholder.typicode.com/todos/1", headers,
                "", "Patch");

        assertThrows(RequestMalformedException.class, ()->proxyReplayService.replayRequest(requestParams));
    }

    @Test
    public void shouldThrowMalformedRequestIfRequestURLIsEmpty() throws Exception {
        HashMap<?,?> headers = new HashMap<>();
        RequestParams requestParams = new RequestParams("MyName", "", headers,
                "", "GET");

        assertThrows(RequestMalformedException.class, ()->proxyReplayService.replayRequest(requestParams));
    }
}