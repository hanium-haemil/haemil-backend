package com.haemil.backend.weather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.ResponseStatus;
import com.haemil.backend.weather.dto.StationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


@Slf4j
@Service
@RequiredArgsConstructor
public class StationService {
    private final RestTemplate restTemplate;
    private final TransferService transferService;
    @Value("${api.hj-secret-key}")
    String serviceKey;

    private final StationDto stationDto;

    public String getStationInfo() throws BaseException {
        String responseBody;
        try {
            String apiUrl = stationDto.getApiUrl();
            String returnType = stationDto.getReturnType();
            String tmX = transferService.getTmInfo(transferService.getTransferInfo()).getTmX(); // 받아오기 by TransferService
            String tmY = transferService.getTmInfo(transferService.getTransferInfo()).getTmY(); // 받아오기 by TransferService

            StringBuilder urlBuilder = new StringBuilder(apiUrl);
            urlBuilder.append("?"+ URLEncoder.encode("serviceKey", "UTF-8")+"="+serviceKey);
            urlBuilder.append("&"+ URLEncoder.encode("returnType", "UTF-8")+"="+URLEncoder.encode(returnType, "UTF-8"));
            urlBuilder.append("&"+ URLEncoder.encode("tmX", "UTF-8")+"="+tmX);
            urlBuilder.append("&"+ URLEncoder.encode("tmY", "UTF-8")+"="+tmY);

            ResponseEntity<String> response = restTemplate.getForEntity(urlBuilder.toString(), String.class);

            responseBody = response.getBody();

            log.info("station_urlBuilder: " + urlBuilder);
            log.info("station_responseBody: " + responseBody);
        } catch (UnsupportedEncodingException e) {
            log.debug("UnsupportedEncodingException 발생 ");
            throw new BaseException(ResponseStatus.UNSUPPORTED_ENCODING);
        }
        return responseBody;
    }

    public String getStationName(String responseBody) throws BaseException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode jsonNode = objectMapper.readTree(responseBody);
            JsonNode itemsNode = jsonNode.get("response").get("body").get("items");
            JsonNode itemNode = itemsNode.get(0);

            String stationName = itemNode.get("stationName").asText();

            log.info("stationName:" + stationName);
            return stationName;
        } catch (JsonProcessingException e) {
            throw new BaseException(ResponseStatus.CANNOT_CONVERT_JSON);
        }
    }

    public boolean isJson(String jsonString) throws BaseException {
        boolean isJson = jsonString.startsWith("{") && jsonString.endsWith("}");

        if (!isJson) {
            throw new BaseException(ResponseStatus.INVALID_XML_FORMAT);
        } else {
            return true;
        }
    }
}
