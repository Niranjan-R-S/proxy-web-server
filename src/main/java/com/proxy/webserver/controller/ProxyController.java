package com.proxy.webserver.controller;

import com.proxy.webserver.exception.ProtocolNotSupportedException;
import com.proxy.webserver.exception.RequestMalformedException;
import com.proxy.webserver.model.RequestParams;
import com.proxy.webserver.service.ProxyReplayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/proxyReplay")
public class ProxyController {

    @Autowired
    private ProxyReplayService proxyReplayService;

    @PostMapping
    public ResponseEntity<?> replayRequest(@RequestBody RequestParams requestParams,
                                            @RequestParam(value = "ClientID", required = true) String ClientID){
        try{
            HashMap<String, Object> response = proxyReplayService.replayRequest(requestParams, ClientID);
            return new ResponseEntity<>(response.get("response"), HttpStatus.valueOf((Integer) response.get("statusCode")));
        } catch (RequestMalformedException exception){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (ProtocolNotSupportedException exception){
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        } catch (TimeoutException exception){
            return new ResponseEntity<>(HttpStatus.GATEWAY_TIMEOUT);
        } catch (Exception exception){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}