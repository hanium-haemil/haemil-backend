package com.haemil.backend.weather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.ResponseStatus;
import com.haemil.backend.weather.dto.WeatherDto;
import com.haemil.backend.weather.dto.WeatherInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {
    private final RestTemplate restTemplate;

    @Value("${api.hj-secret-key}")
    private String serviceKey;

    public String getWeatherInfo(WeatherDto weatherDto) throws BaseException {
        String responseBody;
        try {
            String apiUrl = weatherDto.getApiUrl();
            String type = weatherDto.getDataType();
            String numOfRows = weatherDto.getNumOfRows();
            String pageNo = weatherDto.getPageNo();
            String base_date = weatherDto.getBase_date();
            String base_time = weatherDto.getBase_time();
            String nx = weatherDto.getNx();
            String ny = weatherDto.getNy();

            StringBuilder urlBuilder = new StringBuilder(apiUrl);
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey);
            urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode(numOfRows, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(pageNo, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(base_date, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(base_time, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8"));

            ResponseEntity<String> response = restTemplate.getForEntity(urlBuilder.toString(), String.class);

            responseBody = response.getBody();
        } catch (UnsupportedEncodingException e) {
            throw new BaseException(ResponseStatus.UNSUPPORTED_ENCODING);
        }
        return responseBody;
    }

    public List<WeatherInfoDto> ParsingJson(String responseBody) throws BaseException {
        List<WeatherInfoDto> weatherInfoDtoList;
        try {
            List<WeatherInfoDto> weatherApiList = new ArrayList<>();
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

                    WeatherInfoDto weatherInfoDto = new WeatherInfoDto();
                    weatherInfoDto.setFcstDate(fcstDate);
                    weatherInfoDto.setFcstTime(fcstTime);
                    weatherInfoDto.setCategory(category);
                    weatherInfoDto.setFcstValue(fcstValue);
                    weatherApiList.add(weatherInfoDto);
                }
            }

            weatherInfoDtoList = new ArrayList<>(weatherApiList);
        } catch (JsonProcessingException e) {
            throw new BaseException(ResponseStatus.CANNOT_CONVERT_JSON);
        }
        return weatherInfoDtoList;
    }

    public boolean isJson(String xmlString) throws BaseException {
        boolean isJson = xmlString.startsWith("{") && xmlString.endsWith("}");

        if (!isJson) {
            throw new BaseException(ResponseStatus.INVALID_XML_FORMAT);
        } else {
            return true;
        }
    }

    public List<WeatherInfoDto> filterCurrentTimeData(List<WeatherInfoDto> weatherInfoDtoList, WeatherDto weatherDto) {
        List<WeatherInfoDto> filteredList = new ArrayList<>();

        for (WeatherInfoDto weatherInfoDto : weatherInfoDtoList) {
            String fcstDate = weatherInfoDto.getFcstDate();
            String fcstTime = weatherInfoDto.getFcstTime();

            if (fcstDate.equals(weatherDto.getBase_date()) && fcstTime.equals(weatherDto.getCurrent_time())) {
                filteredList.add(weatherInfoDto);
            }
        }

        return filteredList;
    }

    public List<String> filterTMNandTMXData(List<WeatherInfoDto> weatherInfoDtoList, WeatherDto weatherDto) {
        List<WeatherInfoDto> TMNList = new ArrayList<>();
        List<WeatherInfoDto> TMXList = new ArrayList<>();
        List<String> TMNXList = new ArrayList<>();

        for (WeatherInfoDto weatherInfoDto : weatherInfoDtoList) {
            String fcstDate = weatherInfoDto.getFcstDate();

            if (fcstDate.equals(weatherDto.getBase_date())) {
                String category = weatherInfoDto.getCategory();

                if (category.equals("TMN")) {
                    TMNList.add(weatherInfoDto);
                } else if (category.equals("TMX")) {
                    TMXList.add(weatherInfoDto);
                }
            }
        }

        for (WeatherInfoDto weatherInfoDto : TMNList) {
            String fcstValue = weatherInfoDto.getFcstValue();
            TMNXList.add(fcstValue);
        }
        for (WeatherInfoDto weatherInfoDto : TMXList) {
            String fcstValue = weatherInfoDto.getFcstValue();
            TMNXList.add(fcstValue);
        }

        return TMNXList;
    }

    public List<WeatherInfoDto> filterCurrentTimeAndSpecifiedDateData(List<WeatherInfoDto> weatherInfoDtoList, String specifiedTime) {
        List<WeatherInfoDto> filteredList = new ArrayList<>();

        for (WeatherInfoDto weatherInfoDto : weatherInfoDtoList) {
            String fcstTime = weatherInfoDto.getFcstTime();
            String fcstDate = weatherInfoDto.getFcstDate();

            // 지정한 시간과 지정한 날짜에 해당하는 데이터만 필터링 (오늘, 내일, 모레)
            if (fcstTime.equals(specifiedTime) && isSpecifiedDate(fcstDate)) {
                if (weatherInfoDto.getCategory().equals("TMP")) {
                    filteredList.add(weatherInfoDto);
                }
            }
        }
        return filteredList;
    }

    private boolean isSpecifiedDate(String fcstDate) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate dayAfterTomorrow = today.plusDays(2);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate parsedFcstDate = LocalDate.parse(fcstDate, formatter);

        return parsedFcstDate.equals(today) ||
                parsedFcstDate.equals(tomorrow) ||
                parsedFcstDate.equals(dayAfterTomorrow);
    }
}
