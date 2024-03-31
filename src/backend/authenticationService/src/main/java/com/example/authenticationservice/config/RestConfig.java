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

/**
 * Configuration class for setting up {@link RestTemplate} with SSL support in the application.
 * This class configures a {@link RestTemplate} to use a custom {@link SSLContext} that relies on a trust store specified in the application properties.
 * The SSL context and connection management settings ensure secure HTTP connections compliant with TLS standards.
 *
 * @since 0.1.0 (2024-01-15)
 * @author BOPS (from 2023-11-02 to 2024-03-31)
 * @version 0.1.0 (2024-01-15)
 */
@Configuration
@Component("restConfig")
public class RestConfig {

    /**
     * Path to the trust store file, loaded from application properties. The trust store contains certificates that
     * the application trusts.
     *
     * @since 0.1.0
     */
    @Value("${server.ssl.trust-store}")
    private String trustStorePath;

    /**
     * Password for the trust store file, loaded from application properties. Used to access the trust store contents.
     *
     * @since 0.1.0
     */
    @Value("${server.ssl.trust-store-password}")
    private String trustStorePassword;

    /**
     * Configures and provides a {@link RestTemplate} bean that is set up to use HTTPS with a custom SSL context.
     * The SSL context is configured with a trust store that contains trusted certificates. This setup is necessary for making
     * secure HTTP requests to endpoints that require SSL.
     *
     * @return A {@link RestTemplate} instance configured for HTTPS communication.
     * @throws Exception if there's an error loading the trust store or setting up the SSL context.
     * @since 0.1.0
     */
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
