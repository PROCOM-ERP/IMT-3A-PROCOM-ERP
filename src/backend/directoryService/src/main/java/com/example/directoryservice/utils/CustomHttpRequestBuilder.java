package com.example.directoryservice.utils;

import com.example.directoryservice.model.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomHttpRequestBuilder {

    @Value("${security.services.sharedkey}")
    private String sharedKey;

    @Value("${BACKEND_GATEWAY_SERVICE_HOSTNAME}")
    private String gatewayHostname;

    @Value("${BACKEND_GATEWAY_SERVICE_PORT_INT}")
    private String gatewayPort;

    private final Environment env;

    private final String thisServiceGatewayPath = "/dir";

    public String buildPath(String version, String resource) {
        return String.format("%s%s%s%s", Path.API, thisServiceGatewayPath, version, resource);
    }

    public String buildUrl(String path) {
        String protocol = isHttpsEnabled() ? "https" : "http";
        return String.format("%s://%s:%s%s", protocol, gatewayHostname, gatewayPort, path);
    }

    public HttpEntity<String> buildHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", sharedKey);
        return new HttpEntity<>(headers);
    }

    public HttpEntity<Object> buildHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", sharedKey);
        return new HttpEntity<>(body, headers);
    }

    private boolean isHttpsEnabled() {
        String serverSslKeyStore = env.getProperty("server.ssl.key-store");
        return serverSslKeyStore != null && !serverSslKeyStore.isEmpty();
    }

}
