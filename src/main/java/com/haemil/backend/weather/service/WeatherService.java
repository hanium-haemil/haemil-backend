package com.haemil.backend.weather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.ResponseStatus;
import com.haemil.backend.weather.dto.WeatherDto;
import com.haemil.backend.weather.dto.WeatherInfoDto;
import com.haemil.backend.weather.entity.WeatherApi;
import lombok.RequiredArgsConstructor;
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
public class WeatherService {
    private final RestTemplate restTemplate;

    public String getWeatherInfo(WeatherDto weatherDto) throws BaseException {
        String responseBody;
        try {
            String apiUrl = weatherDto.getApiUrl();
            String serviceKey = weatherDto.getServiceKey();
            String type = weatherDto.getDataType();
            String numOfRows = weatherDto.getNumOfRows();
            String pageNo = weatherDto.getPageNo();
            String base_date = weatherDto.getBase_date();
            String base_time = weatherDto.getBase_time();
            String nx = weatherDto.getNx();
            String ny = weatherDto.getNy();

            log.debug("serviceKey: " + serviceKey);

            StringBuilder urlBuilder = new StringBuilder(apiUrl);
            urlBuilder.append("?"+ URLEncoder.encode("serviceKey", "UTF-8")+"="+serviceKey);
            urlBuilder.append("&"+ URLEncoder.encode("dataType", "UTF-8")+"="+URLEncoder.encode(type, "UTF-8"));
            urlBuilder.append("&"+ URLEncoder.encode("numOfRows", "UTF-8")+"="+URLEncoder.encode(numOfRows, "UTF-8"));
            urlBuilder.append("&"+ URLEncoder.encode("pageNo", "UTF-8")+"="+URLEncoder.encode(pageNo, "UTF-8"));
            urlBuilder.append("&"+ URLEncoder.encode("base_date", "UTF-8")+"="+URLEncoder.encode(base_date, "UTF-8"));
            urlBuilder.append("&"+ URLEncoder.encode("base_time", "UTF-8")+"="+URLEncoder.encode(base_time, "UTF-8"));
            urlBuilder.append("&"+ URLEncoder.encode("nx", "UTF-8")+"="+URLEncoder.encode(nx, "UTF-8"));
            urlBuilder.append("&"+ URLEncoder.encode("ny", "UTF-8")+"="+URLEncoder.encode(ny, "UTF-8"));

            ResponseEntity<String> response = restTemplate.getForEntity(urlBuilder.toString(), String.class);

            responseBody = response.getBody();

            log.debug("urlBuilder: " + urlBuilder);
            log.debug("responseBody: " + responseBody);
        } catch (UnsupportedEncodingException e) {
            log.debug("UnsupportedEncodingException 발생 ");
            throw new BaseException(ResponseStatus.UNSUPPORTED_ENCODING);
        }
        return responseBody;
    }

    public List<WeatherInfoDto> ParsingJson(String responseBody) throws BaseException {
        List<WeatherInfoDto> weatherInfoDtoList;
        try {
            List<WeatherApi> weatherApiList = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode jsonNode = objectMapper.readTree(responseBody);
            JsonNode itemsNode = jsonNode.get("response").get("body").get("items");
            JsonNode itemNode = itemsNode.get("item");

            if (itemNode.isArray()) {
                for (JsonNode node : itemNode) {
                    String fcstDate = node.get("fcstDate").asText();
                    String fcstTime = node.get("fcstTime").asText();
                    String category = node.get("category").asText();
                    String fcstValue = node.get("fcstValue").asText();

                    WeatherApi weatherApi = WeatherApi.builder()
                            .fcstDate(fcstDate)
                            .fcstTime(fcstTime)
                            .category(category)
                            .fcstValue(fcstValue)
                            .build();

                    weatherApiList.add(weatherApi);
                }
            }

            weatherInfoDtoList = new ArrayList<>();
            for (WeatherApi a : weatherApiList) {
                WeatherInfoDto weatherInfoDto = new WeatherInfoDto();
                weatherInfoDto.setFcstDate(a.getFcstDate());
                weatherInfoDto.setFcstTime(a.getFcstTime());
                weatherInfoDto.setCategory(a.getCategory());
                weatherInfoDto.setFcstValue(a.getFcstValue());
                weatherInfoDtoList.add(weatherInfoDto);
            }
            log.debug("weatherInfoDtoList:" + weatherInfoDtoList);

        } catch (JsonProcessingException e) {
            throw new BaseException(ResponseStatus.CANNOT_CONVERT_JSON);
        }
        return weatherInfoDtoList;
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
