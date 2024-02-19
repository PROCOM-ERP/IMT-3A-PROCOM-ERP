package com.example.directoryservice.service;

import com.example.directoryservice.dto.LoginProfileActivationResponseDto;
import com.example.directoryservice.model.LoginProfile;
import com.example.directoryservice.repository.LoginProfileRepository;
import com.example.directoryservice.utils.CustomHttpRequestBuilder;
import com.example.directoryservice.utils.PerformanceTracker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class LoginProfileService {

    private final LoginProfileRepository loginProfileRepository;
    private final RestTemplate restTemplate;
    private final CustomHttpRequestBuilder customHttpRequestBuilder;

    private final PerformanceTracker performanceTracker;
    private final Logger logger = LoggerFactory.getLogger(LoginProfileService.class);

    /* Public Methods */

    @Transactional
    public void createLoginProfile(String idLoginProfile)
    {
        logger.info("Start login profile creation...");
        long startTimeNano = performanceTracker.getCurrentTime();

        // build and save LoginProfile entity if it doesn't already exist
        if (! loginProfileRepository.existsById(idLoginProfile))
            loginProfileRepository.save(LoginProfile.builder()
                    .id(idLoginProfile)
                    .build());

        // log elapsed time for method execution
        long elapsedTimeMillis = performanceTracker.getElapsedTimeMillis(startTimeNano);
        logger.info("Elapsed time to create new login profile : " + elapsedTimeMillis + " ms");
    }

    @Transactional
    public void updateLoginProfileActivationById(String getLoginProfileActivationById)
        throws RestClientException
    {
        logger.info("Start login profile activation update...");
        long startTimeNano = performanceTracker.getCurrentTime();

        // retrieve external LoginProfile entity
        LoginProfileActivationResponseDto loginProfileDto = getLoginProfileActivationById(getLoginProfileActivationById);

        // retrieve LoginProfile entity or create new one
        LoginProfile loginProfile = loginProfileRepository.findById(loginProfileDto.getId())
                .orElse(loginProfileRepository.save(LoginProfile.builder()
                        .id(loginProfileDto.getId())
                        .build()));

        // set LoginProfile entity activation status
        loginProfile.setIsEnable(loginProfileDto.getIsEnable());

        // save changes
        loginProfileRepository.save(loginProfile);

        // log elapsed time for method execution
        long elapsedTimeMillis = performanceTracker.getElapsedTimeMillis(startTimeNano);
        logger.info("Elapsed time to update login profile activation : " + elapsedTimeMillis + " ms");
    }

    @Transactional
    public void updateLoginProfileJwtGenMinAtById(String idLoginProfile)
    {
        logger.info("Start login profile jwt min generation instant update...");
        long startTimeNano = performanceTracker.getCurrentTime();

        // reset LoginProfile entity jwt min generation instant
        loginProfileRepository.updateJwtGenMinAtById(idLoginProfile);

        // log elapsed time for method execution
        long elapsedTimeMillis = performanceTracker.getElapsedTimeMillis(startTimeNano);
        logger.info("Elapsed time to update login profile jwt min generation instant : " + elapsedTimeMillis + " ms");
    }

    @Transactional
    public void updateAllLoginProfileJwtGenMin()
    {
        logger.info("Start all login profiles jwt min generation instant update...");
        long startTimeNano = performanceTracker.getCurrentTime();

        // reset all LoginProfile entities jwt min generation instant
        loginProfileRepository.updateAllJwtGenMinAt();

        // log elapsed time for method execution
        long elapsedTimeMillis = performanceTracker.getElapsedTimeMillis(startTimeNano);
        logger.info("Elapsed time to update all login profiles jwt min generation instant : " + elapsedTimeMillis + " ms");
    }

    /* Private Methods */
    private LoginProfileActivationResponseDto getLoginProfileActivationById(String getLoginProfileActivationById)
        throws RestClientException
    {
        // build request
        String url = customHttpRequestBuilder.buildUrl(getLoginProfileActivationById);
        HttpEntity<String> entity = customHttpRequestBuilder.buildHttpEntity();

        // send request
        ResponseEntity<LoginProfileActivationResponseDto> response = restTemplate.exchange(url, HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}); // response with custom type

        // check if body is existing and consistent
        if (! (response.getStatusCode().is2xxSuccessful() && response.hasBody() && response.getBody() != null))
            throw new RestClientException("");

        // return expected LoginProfile activation
        return response.getBody();
    }
}
