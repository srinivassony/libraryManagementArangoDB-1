package com.libraryManagementArangoDB.controller;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import com.libraryManagementArangoDB.exceptions.NoSuchApiException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class MissingApiHandler {

    private final RequestMappingHandlerMapping handlerMapping;

    private final Logger logger = LoggerFactory.getLogger(MissingApiHandler.class);

    private final Set<String> registeredEndpoints = new HashSet<>();

    public MissingApiHandler(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    private void loadRegisteredEndpoints() {
        registeredEndpoints.clear();
        registeredEndpoints.addAll(handlerMapping.getHandlerMethods().entrySet().stream()
                .flatMap(entry -> {
                    Set<String> paths = entry.getKey().getPathPatternsCondition() != null
                            ? entry.getKey().getPathPatternsCondition().getPatterns().stream().map(Object::toString)
                                    .collect(Collectors.toSet())
                            : Collections.emptySet();
                    Set<RequestMethod> methods = entry.getKey().getMethodsCondition().getMethods();

                    return paths.stream()
                            .flatMap(path -> methods.stream().map(method -> method.name() + " " + path));
                })
                .collect(Collectors.toSet()));
    }

    @RequestMapping("/**")
    public void handleUnknownApi(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();
        String requestKey = requestMethod + " " + requestUri;

        if (!registeredEndpoints.contains(requestKey)) {
            throw new NoSuchApiException(
                    "ERROR: API '" + requestUri + "' with method '" + requestMethod + "' does not exist!");
        }
    }

    @PostConstruct
    public void logRegisteredEndpoints() {
        loadRegisteredEndpoints();
        logger.info("Registered Endpoints: {}", registeredEndpoints);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        loadRegisteredEndpoints();
        logger.info("API Endpoints Loaded: " + registeredEndpoints);
    }
}