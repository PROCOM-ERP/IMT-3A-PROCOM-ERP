package com.example.directoryservice.service;

import com.example.directoryservice.dto.LoginProfileActivationResponseDto;
import com.example.directoryservice.model.LoginProfile;
import com.example.directoryservice.repository.LoginProfileRepository;
import com.example.directoryservice.utils.CustomHttpRequestBuilder;
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

    /* Public Methods */

    @Transactional
    public void createLoginProfile(String idLoginProfile)
    {
        // build and save LoginProfile entity if it doesn't already exist
        if (! loginProfileRepository.existsById(idLoginProfile))
            loginProfileRepository.save(LoginProfile.builder()
                    .id(idLoginProfile)
                    .build());
    }

    @Transactional
    public void updateLoginProfileActivationById(String getLoginProfileActivationById)
        throws RestClientException
    {
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
    }

    @Transactional
    public void updateLoginProfileJwtGenMinAtById(String idLoginProfile)
    {
        // reset LoginProfile entity jwt min generation instant
        loginProfileRepository.updateJwtGenMinAtById(idLoginProfile);
    }

    @Transactional
    public void updateAllLoginProfileJwtGenMin()
    {
        // reset all LoginProfile entities jwt min generation instant
        loginProfileRepository.updateAllJwtGenMinAt();
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
