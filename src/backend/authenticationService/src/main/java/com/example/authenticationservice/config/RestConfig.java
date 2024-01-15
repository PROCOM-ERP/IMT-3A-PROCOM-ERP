package com.example.authenticationservice.config;

import java.io.FileInputStream;
import java.security.*;
import javax.net.ssl.SSLContext;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Configuration
@Component("restConfig")
public class RestConfig {

  @Value("${server.ssl.trust-store}")
  private String trustStorePath;

  @Value("${server.ssl.trust-store-password}")
  private String trustStorePassword;

  @Bean
  public RestTemplate restTemplate() throws Exception {
    KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
    trustStore.load(new FileInputStream(trustStorePath),
                    trustStorePassword.toCharArray());

    SSLContext sslContext =
        new SSLContextBuilder().loadTrustMaterial(trustStore, null).build();

    PoolingHttpClientConnectionManager connectionManager =
        PoolingHttpClientConnectionManagerBuilder.create()
            .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                                     .setSslContext(sslContext)
                                     .setTlsVersions(TLS.V_1_3)
                                     .build())
            .setDefaultSocketConfig(SocketConfig.custom()
                                        .setSoTimeout(Timeout.ofMinutes(1))
                                        .build())
            .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.LAX)
            .setConnPoolPolicy(PoolReusePolicy.LIFO)
            .setDefaultConnectionConfig(
                ConnectionConfig.custom()
                    .setSocketTimeout(Timeout.ofMinutes(1))
                    .setConnectTimeout(Timeout.ofMinutes(1))
                    .setTimeToLive(TimeValue.ofMinutes(10))
                    .build())
            .build();
    HttpClient httpClient = HttpClientBuilder.create()
                                .setConnectionManager(connectionManager)
                                .build();

    ClientHttpRequestFactory requestFactory =
        new HttpComponentsClientHttpRequestFactory(httpClient);
    return new RestTemplate(requestFactory);
  }
}
