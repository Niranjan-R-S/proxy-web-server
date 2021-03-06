package com.proxy.webserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proxy.webserver.exception.ProtocolNotSupportedException;
import com.proxy.webserver.exception.RequestMalformedException;
import com.proxy.webserver.model.RequestParams;
import com.proxy.webserver.service.ProxyReplayService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProxyController.class)
class ProxyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProxyReplayService proxyReplayService;

    @Test
    public void shouldReplayRequestAndFetchData() throws Exception {
        HashMap<?,?> headers = new HashMap<>();
        RequestParams requestParams = new RequestParams("https://google.com", headers,
                "", "POST");

        HashMap<String, Object> proxyResponse = new HashMap<>();
        HashMap<Object, Object> serverResponse = new HashMap<>();
        serverResponse.put("id", 1);
        serverResponse.put("userId", 1);
        proxyResponse.put("response", serverResponse);
        proxyResponse.put("statusCode", 200);

        when(proxyReplayService.replayRequest(any(RequestParams.class), any())).thenReturn(proxyResponse);

        ObjectMapper requestMapper = new ObjectMapper();

        mockMvc.perform(post("/proxyReplay?ClientID=MyName")
                .content(requestMapper.writeValueAsString(requestParams))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value("1"))
                .andExpect(jsonPath("userId").value("1"));

    }

    @Test
    public void shouldReturnInternalServerErrorIfServiceFailsToReplayRequest() throws Exception {
        HashMap<?,?> headers = new HashMap<>();
        RequestParams requestParams = new RequestParams("https://somewebsite.com", headers,
                "", "POST");

        when(proxyReplayService.replayRequest(any(RequestParams.class), any())).thenThrow();

        ObjectMapper requestMapper = new ObjectMapper();

        mockMvc.perform(post("/proxyReplay?ClientID=MyName")
                .content(requestMapper.writeValueAsString(requestParams))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is5xxServerError());

    }

    @Test
    public void shouldReturnBadRequestIfRequestParamsAreInvalid() throws Exception {
        HashMap<?,?> headers = new HashMap<>();
        RequestParams requestParams = new RequestParams("https://somewebsite.com", headers,
                "", "Patch");

        when(proxyReplayService.replayRequest(any(RequestParams.class), any())).thenThrow(new RequestMalformedException("Bad request"));

        ObjectMapper requestMapper = new ObjectMapper();

        mockMvc.perform(post("/proxyReplay?ClientID=MyName")
                .content(requestMapper.writeValueAsString(requestParams))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void shouldReturnBadRequestIfClientIDIsNotProvidedInTheRequest() throws Exception {
        HashMap<?,?> headers = new HashMap<>();
        RequestParams requestParams = new RequestParams("https://google.com", headers,
                "", "POST");

        HashMap<String, Object> proxyResponse = new HashMap<>();
        HashMap<Object, Object> serverResponse = new HashMap<>();
        serverResponse.put("id", 1);
        serverResponse.put("userId", 1);
        proxyResponse.put("response", serverResponse);
        proxyResponse.put("statusCode", 200);

        when(proxyReplayService.replayRequest(any(RequestParams.class), any())).thenReturn(proxyResponse);

        ObjectMapper requestMapper = new ObjectMapper();

        mockMvc.perform(post("/proxyReplay")
                .content(requestMapper.writeValueAsString(requestParams))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void shouldReturnProtocolNotSupportedIfProtocolOfURLIsHTTP() throws Exception {
        HashMap<?,?> headers = new HashMap<>();
        RequestParams requestParams = new RequestParams("http://somewebsite.com", headers,
                "", "POST");

        when(proxyReplayService.replayRequest(any(RequestParams.class), any())).thenThrow(new ProtocolNotSupportedException("Unsupported protocol"));

        ObjectMapper requestMapper = new ObjectMapper();

        mockMvc.perform(post("/proxyReplay?ClientID=MyName")
                .content(requestMapper.writeValueAsString(requestParams))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void shouldReturnTimeoutIfTimeTakenToProcessIsMoreThan5Seconds() throws Exception {
        HashMap<?,?> headers = new HashMap<>();
        RequestParams requestParams = new RequestParams("https://somewebsite.com", headers,
                "", "POST");

        when(proxyReplayService.replayRequest(any(RequestParams.class), any())).thenThrow(new TimeoutException("Request took more than 5 seconds"));

        ObjectMapper requestMapper = new ObjectMapper();

        mockMvc.perform(post("/proxyReplay?ClientID=MyName")
                .content(requestMapper.writeValueAsString(requestParams))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isGatewayTimeout());

    }
}