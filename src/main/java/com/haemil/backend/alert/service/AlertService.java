package com.haemil.backend.alert.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haemil.backend.alert.dto.ApiInfoDto;
import com.haemil.backend.alert.dto.GetApiDto;
import com.haemil.backend.alert.entity.AlertApi;
import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownContentTypeException;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {
    private final RestTemplate restTemplate;

    @Value("${api.secret-key}")
    private String serviceKey;
    private String responseBody;

    public String getAlertInfo(GetApiDto reqGetApiDto) throws BaseException {

        try {
            String apiUrl = reqGetApiDto.getApiUrl();
            String type = reqGetApiDto.getType();
            String pageNo = reqGetApiDto.getPageNo();
            String numOfRows = reqGetApiDto.getNumOfRows();

            StringBuilder urlBuilder = new StringBuilder(apiUrl);
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + serviceKey);
            urlBuilder.append("&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(pageNo, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode(numOfRows, "UTF-8"));

//            log.debug("urlBuilder: {}", urlBuilder);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "*/*;q=0.9"); // HTTP_ERROR 방지
            HttpEntity<String> httpRequest = new HttpEntity<>(null, headers);
//            log.debug("httpRequest = {}", httpRequest);

            RestTemplate restTemplate = new RestTemplate();
//            log.debug("restTemplate = {}", restTemplate);

            HttpStatus httpStatus = null;
            ResponseEntity<String> httpResponse = null;

            URI uri = new URI(urlBuilder.toString()); // service key is not registered 오류 방지
//            log.debug("uri = {}", uri);
            httpResponse = restTemplate.exchange(uri, HttpMethod.GET, httpRequest, new ParameterizedTypeReference<String>(){});
//            log.debug("httpResponse = {}", httpResponse);

            if (httpResponse != null && httpResponse.getBody() != null) {
                responseBody = httpResponse.getBody();
            }
//            log.debug("responseBody = {}",responseBody);

        } catch (UnsupportedEncodingException e) { // 에러가 발생했을 때 예외 status 명시
            log.debug("UnsupportedEncodingException 발생 ");
            throw new BaseException(ResponseStatus.UNSUPPORTED_ENCODING);
        } catch (URISyntaxException e) {
            log.debug("URISyntaxException 발생 ");
            throw new BaseException(ResponseStatus.UNSUPPORTED_ENCODING);
        }

        return responseBody;
    }

    public ApiInfoDto ParsingJson(String responseBody) throws BaseException {
        ApiInfoDto apiInfoDto;

        try {
            List<AlertApi> alertApiList = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode jsonNode = objectMapper.readTree(responseBody);
            JsonNode firstElement = jsonNode.get("DisasterMsg").get(1);
            JsonNode rowNode = firstElement.get("row");
            JsonNode nextNode = rowNode.get(0);

            String msg = nextNode.get("msg").asText();
            String location = nextNode.get("location_name").asText(); // 예시로 location_name을 사용하여 location 값을 가져옴

//            log.debug("msg: " + msg);
//            log.debug("location: " + location);

            AlertApi alertApi = AlertApi.builder()
                    .msg(msg)
                    .location(location)
                    .build();

            alertApiList.add(alertApi);

            // 변환된 데이터를 ApiInfoDto 형태로 리스트로 반환
            apiInfoDto = new ApiInfoDto();

            for (AlertApi a : alertApiList) {
                apiInfoDto.setMsg(a.getMsg());
                apiInfoDto.setLocation(a.getLocation());
            }
//            log.debug("apiInfoDto: " + apiInfoDto);

        } catch (JsonProcessingException e) { // 에러가 발생했을 때 예외 status 명시
            throw new BaseException(ResponseStatus.CANNOT_CONVERT_JSON);
        }
        return apiInfoDto;
    }

    public boolean isJson(String xmlString) throws BaseException {
        // JSON 형식인지 확인
        boolean isJson = xmlString.startsWith("{") && xmlString.endsWith("}");

        if (!isJson) { // JSON 형식이 아닌 경우
            throw new BaseException(ResponseStatus.INVALID_XML_FORMAT);
        } else {
            return true;
        }
    }
}
