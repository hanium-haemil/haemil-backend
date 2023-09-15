package com.haemil.backend.weather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.ResponseStatus;
import com.haemil.backend.weather.controller.AirController;
import com.haemil.backend.weather.dto.StationDto;
import com.haemil.backend.weather.dto.TransferDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {
    private final RestTemplate restTemplate;
    @Value("${api.hj-kakao-key}")
    String serviceKey;
    AirController airController;

    public String getTransferInfo(TransferDto transferDto2) throws BaseException {
        String responseBody;

        TransferDto transferDto = transferDto2;

        try {
            String x = transferDto.getY();
            String y = transferDto.getX();
            String input_coord = "WGS84";
            String output_coord = "TM";

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "KakaoAK " + serviceKey);

//            log.debug("serviceKey: " + serviceKey);

            StringBuilder urlBuilder = new StringBuilder("https://dapi.kakao.com/v2/local/geo/transcoord.json");
            urlBuilder.append("?"+ URLEncoder.encode("x", "UTF-8")+"="+URLEncoder.encode(x, "UTF-8"));
            urlBuilder.append("&"+ URLEncoder.encode("y", "UTF-8")+"="+URLEncoder.encode(y, "UTF-8"));
            urlBuilder.append("&"+ URLEncoder.encode("input_coord", "UTF-8")+"="+URLEncoder.encode(input_coord, "UTF-8"));
            urlBuilder.append("&"+ URLEncoder.encode("output_coord", "UTF-8")+"="+URLEncoder.encode(output_coord, "UTF-8"));

            URI url = new URI(urlBuilder.toString());
            RequestEntity<?> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, url);

            ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
            responseBody = response.getBody();

//            log.info("urlBuilder: " + urlBuilder);
//            log.info("responseBody: " + responseBody);
        } catch (UnsupportedEncodingException e) {
            log.debug("UnsupportedEncodingException 발생 ");
            throw new BaseException(ResponseStatus.UNSUPPORTED_ENCODING);
        } catch (Exception e) {
            log.debug("INVALID_XML_FORMAT Exception 발생");
            throw new BaseException(ResponseStatus.INVALID_XML_FORMAT);
        }
        return responseBody;
    }

    // tmX , tmY -> Station 으로 넘기기
    public StationDto getTmInfo(String responseBody) throws BaseException {
        try {
//            log.debug("transfer - tminfo = {}", responseBody);
            StationDto stationDto = new StationDto();
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode jsonNode = objectMapper.readTree(responseBody);
            JsonNode documentsNode = jsonNode.get("documents");
            JsonNode itemNode = documentsNode.get(0);

            String x = itemNode.get("x").asText();
            String y = itemNode.get("y").asText();
            stationDto.setTmX(x);
            stationDto.setTmY(y);

            return stationDto;
        } catch (JsonProcessingException e) {
            throw new BaseException(ResponseStatus.CANNOT_CONVERT_JSON);
        }
    }
}
