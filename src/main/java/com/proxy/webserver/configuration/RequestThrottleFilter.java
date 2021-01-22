package com.proxy.webserver.configuration;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class RequestThrottleFilter implements Filter {

    private int MAX_REQUESTS_PER_MINUTE = 50;

    private LoadingCache<String, Integer> requestCountsPerClientID;

    public RequestThrottleFilter(){
        super();
        requestCountsPerClientID = CacheBuilder.newBuilder().
                expireAfterWrite(1, TimeUnit.MINUTES).build(new CacheLoader<String, Integer>() {
            public Integer load(String key) {
                return 0;
            }
        });
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        String clientID;
        if(httpServletRequest.getRequestURL().toString().contains("/proxyReplay")){
            clientID = httpServletRequest.getParameter("ClientID");
        }else{
            clientID = "System";
        }

        if(clientID == null){
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            httpServletResponse.getWriter().write("ClientID is required");
            return;
        }
        if(isMaximumRequestsPerSecondExceeded(clientID)){
            httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpServletResponse.getWriter().write("Too many requests");
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean isMaximumRequestsPerSecondExceeded(String clientID){
        int requests = 0;
        try {
            requests = requestCountsPerClientID.get(clientID);
            if(requests > MAX_REQUESTS_PER_MINUTE){
                requestCountsPerClientID.put(clientID, requests);
                return true;
            }
        } catch (ExecutionException e) {
            requests = 0;
        }
        requests++;
        requestCountsPerClientID.put(clientID, requests);
        return false;
    }

    @Override
    public void destroy() {

    }
}