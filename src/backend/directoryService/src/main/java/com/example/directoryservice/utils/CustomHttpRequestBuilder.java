package com.example.directoryservice.utils;

import com.example.directoryservice.model.Path;
import com.example.directoryservice.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class CustomHttpRequestBuilder {

    @Value("${security.service.sharedKey}")
    private String sharedKey;
    @Value("${BACKEND_GATEWAY_SERVICE_HOSTNAME}")
    private String gatewayHostname;
    @Value("${BACKEND_GATEWAY_SERVICE_PORT_INT}")
    private String gatewayPort;
    private final Environment env;
    @Value("${security.service.name}")
    private String thisServiceGatewayPath;

    private final JwtService jwtService;

    public String buildPath(String version, String resource) {
        return String.format("%s%s%s%s", Path.API, thisServiceGatewayPath, version, resource);
    }

    public String buildUrl(String path) {
        String protocol = isHttpsEnabled() ? "https" : "http";
        return String.format("%s://%s:%s%s", protocol, gatewayHostname, gatewayPort, path);
    }

    public String addQueryParamToUrl(String url, String param, String value) {
       return UriComponentsBuilder.fromHttpUrl(url).queryParam(param, value).toUriString();
    }

    public HttpEntity<String> buildHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        String jwtToken = jwtService.generateJwtToken(sharedKey);
        headers.set("Authorization", "Bearer " + jwtToken);
        return new HttpEntity<>(headers);
    }

    public HttpEntity<Object> buildHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        String jwtToken = jwtService.generateJwtToken(sharedKey);
        headers.set("Authorization", "Bearer " + jwtToken);
        return new HttpEntity<>(body, headers);
    }

    private boolean isHttpsEnabled() {
        String serverSslKeyStore = env.getProperty("server.ssl.key-store");
        return serverSslKeyStore != null && !serverSslKeyStore.isEmpty();
    }

}
