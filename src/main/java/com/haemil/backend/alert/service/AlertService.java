package com.haemil.backend.alert.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haemil.backend.alert.dto.AlertDto;
import com.haemil.backend.alert.dto.GetApiDto;
import com.haemil.backend.alert.dto.ReqCoordDto;
import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.ResponseStatus;
import com.haemil.backend.weather.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
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

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "*/*;q=0.9"); // HTTP_ERROR 방지
            HttpEntity<String> httpRequest = new HttpEntity<>(null, headers);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> httpResponse = null;

            URI uri = new URI(urlBuilder.toString()); // service key is not registered 오류 방지
            httpResponse = restTemplate.exchange(uri, HttpMethod.GET, httpRequest, new ParameterizedTypeReference<String>(){});

            if (httpResponse != null && httpResponse.getBody() != null) {
                responseBody = httpResponse.getBody();
            }
        } catch (UnsupportedEncodingException e) { // 에러가 발생했을 때 예외 status 명시
            log.debug("UnsupportedEncodingException 발생 ");
            throw new BaseException(ResponseStatus.UNSUPPORTED_ENCODING);
        } catch (URISyntaxException e) {
            log.debug("URISyntaxException 발생 ");
            throw new BaseException(ResponseStatus.UNSUPPORTED_ENCODING);
        }

        return responseBody;
    }

    // 임시
    private final LocationService locationService;

    public List<AlertDto> ParsingJson(String responseBody, HttpServletRequest request) throws BaseException {
        try {
            String fullLocationJsonString = locationService.getLocationInfo(request);
            String userLocation = ParsingLocation(fullLocationJsonString);

            List<AlertDto> alertApiList = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode jsonNode = objectMapper.readTree(responseBody);
            JsonNode firstElement = jsonNode.get("DisasterMsg").get(1);
            JsonNode rowNode = firstElement.get("row");

            for (JsonNode nextNode : rowNode) {
                String msg = nextNode.get("msg").asText();
                String location = nextNode.get("location_name").asText();
                if (location.contains(userLocation)) {
                    AlertDto alertDto = new AlertDto(msg, location);
                    alertApiList.add(alertDto);
                }
            }

            return alertApiList;
        } catch (JsonProcessingException e) { // 에러가 발생했을 때 예외 status 명시
            throw new BaseException(ResponseStatus.CANNOT_CONVERT_JSON);
        }
    }

    // -- 메소드 --
    public String ParsingLocation(String fullLocationJsonString) throws BaseException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(fullLocationJsonString);

            JsonNode documentsNode = jsonNode.get("documents");
            if (documentsNode != null && documentsNode.isArray() && documentsNode.size() > 0) {
                JsonNode firstDocumentNode = documentsNode.get(0);
                JsonNode region1DepthNameNode = firstDocumentNode.get("region_1depth_name");
                if (region1DepthNameNode != null && region1DepthNameNode.isTextual()) {
                    return region1DepthNameNode.asText();
                }
            }
        } catch (JsonProcessingException e) {
            throw new BaseException(ResponseStatus.CANNOT_CONVERT_JSON);
        }
        throw new BaseException(ResponseStatus.UNKNOWN_USER_LOCATION);
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
