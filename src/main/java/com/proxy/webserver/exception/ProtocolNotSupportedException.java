package com.proxy.webserver.exception;

public class ProtocolNotSupportedException extends Exception {
    public ProtocolNotSupportedException(String message){
        super(message);
    }
}
