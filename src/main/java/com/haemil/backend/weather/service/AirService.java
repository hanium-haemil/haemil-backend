package com.haemil.backend.weather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.ResponseStatus;
import com.haemil.backend.weather.dto.AirDto;
import com.haemil.backend.weather.dto.AirInfoDto;
import com.haemil.backend.weather.entity.AirApi;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AirService {
    private final RestTemplate restTemplate;
    @Value("${api.hj-secret-key}")
    String serviceKey;

    public String getAirInfo(AirDto airdto) throws BaseException {
        String responseBody;
        try {
            String apiUrl = airdto.getApiUrl();
            String returnType = airdto.getReturnType();
            String numOfRows = airdto.getNumOfRows();
            String pageNo = airdto.getPageNo();
            String stationName = airdto.getStationName();
            String dataTerm = airdto.getDataTerm();
            String ver = airdto.getVer();

            log.debug("serviceKey: " + serviceKey);

            StringBuilder urlBuilder = new StringBuilder(apiUrl);
            urlBuilder.append("?"+ URLEncoder.encode("serviceKey", "UTF-8")+"="+serviceKey);
            urlBuilder.append("&"+ URLEncoder.encode("returnType", "UTF-8")+"="+URLEncoder.encode(returnType, "UTF-8"));
            urlBuilder.append("&"+ URLEncoder.encode("numOfRows", "UTF-8")+"="+URLEncoder.encode(numOfRows, "UTF-8"));
            urlBuilder.append("&"+ URLEncoder.encode("pageNo", "UTF-8")+"="+URLEncoder.encode(pageNo, "UTF-8"));
            urlBuilder.append("&"+ URLEncoder.encode("stationName", "UTF-8")+"="+stationName);
            urlBuilder.append("&"+ URLEncoder.encode("dataTerm", "UTF-8")+"="+URLEncoder.encode(dataTerm, "UTF-8"));
            urlBuilder.append("&"+ URLEncoder.encode("ver", "UTF-8")+"="+URLEncoder.encode(ver, "UTF-8"));

            ResponseEntity<String> response = restTemplate.getForEntity(urlBuilder.toString(), String.class);

            responseBody = response.getBody();

            log.info("urlBuilder: " + urlBuilder);
            log.info("responseBody: " + responseBody);
        } catch (UnsupportedEncodingException e) {
            log.debug("UnsupportedEncodingException 발생 ");
            throw new BaseException(ResponseStatus.UNSUPPORTED_ENCODING);
        }
        return responseBody;
    }

    public List<AirInfoDto> ParsingJson(String responseBody) throws BaseException {
        List<AirInfoDto> airInfoDtoList;
        try {
            List<AirApi> airApiList = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode jsonNode = objectMapper.readTree(responseBody);
            log.info("jsonNode = {}", jsonNode);
            JsonNode itemsNode = jsonNode.get("response").get("body").get("items");
            log.info("itemsNode = {}", itemsNode);
            JsonNode itemNode = itemsNode.get("item");

            if (itemsNode.isArray()) {
                for (JsonNode node : itemsNode) {
                    String dataTime = node.get("dataTime").asText();
                    String pm10Value = node.get("pm10Value").asText();
                    String pm25Value = node.get("pm25Value").asText();
                    String pm10Grade = node.get("pm10Grade").asText();
                    String pm25Grade = node.get("pm25Grade").asText();

                    AirApi airApi = AirApi.builder()
                            .dataTime(dataTime)
                            .pm10Value(pm10Value)
                            .pm25Value(pm25Value)
                            .pm10Grade(pm10Grade)
                            .pm25Grade(pm25Grade)
                            .build();

                    airApiList.add(airApi);
                }
            }

            airInfoDtoList = new ArrayList<>();
            for (AirApi a : airApiList) {
                AirInfoDto airInfoDto = new AirInfoDto();

                airInfoDto.setDataTime(a.getDataTime());
                airInfoDto.setPm10Value(a.getPm10Value());
                airInfoDto.setPm10Grade(a.getPm10Grade());
                airInfoDto.setPm25Grade(a.getPm25Grade());
                airInfoDto.setPm25Value(a.getPm25Value());
                airInfoDtoList.add(airInfoDto);
            }
            log.debug("airInfoDtoList:" + airInfoDtoList);

        } catch (JsonProcessingException e) {
            throw new BaseException(ResponseStatus.CANNOT_CONVERT_JSON);
        }
        return airInfoDtoList;
    }

    public boolean isJson(String xmlString) throws BaseException {
        boolean isJson = xmlString.startsWith("{") && xmlString.endsWith("}");

        if (!isJson) {
            throw new BaseException(ResponseStatus.INVALID_XML_FORMAT);
        }else {
            return true;
        }
    }
}
