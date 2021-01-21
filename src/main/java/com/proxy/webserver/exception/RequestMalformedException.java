package com.proxy.webserver.exception;

public class RequestMalformedException extends Exception {
    public RequestMalformedException(String message){
        super(message);
    }
}
