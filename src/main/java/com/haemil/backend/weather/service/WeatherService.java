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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    public Map<String, String> transformWeatherData(List<WeatherInfoDto> weatherInfoDtoList, List<WeatherInfoDto> todayData, WeatherDto weatherDto) {
        List<WeatherInfoDto> Result = new ArrayList<>();
        Map<String, String> resultData = new HashMap<>();

        for (WeatherInfoDto today : todayData) {
            String fcstValue = today.getFcstValue();
            String category = today.getCategory();

            if (category.equals("TMP")) {
                resultData.put("current", fcstValue);
                break;
            }
        }

        for (WeatherInfoDto weatherInfoDto : weatherInfoDtoList) {
            String fcstDate = weatherInfoDto.getFcstDate();
            String fcstValue = weatherInfoDto.getFcstValue();

            if (fcstDate.equals(weatherDto.getBase_date())) {
                String category = weatherInfoDto.getCategory();

                if (category.equals("TMN")) {
                    resultData.put("min", fcstValue);
                } else if (category.equals("TMX")) {
                    resultData.put("max", fcstValue);
                }
            }
        }

        return resultData;
    }

    public List<Map<String, String>> filterCurrentTimeAndSpecifiedDateData(List<WeatherInfoDto> weatherInfoDtoList, String specifiedTime) {
        Map<String, Map<String, String>> groupedData = new HashMap<>();

        for (WeatherInfoDto weatherInfoDto : weatherInfoDtoList) {
            String fcstDate = weatherInfoDto.getFcstDate();
            String fcstTime = weatherInfoDto.getFcstTime();
            String fcstValue = weatherInfoDto.getFcstValue();
            String category = weatherInfoDto.getCategory();

            // "fcstTime"이 "1500"이고 오늘, 내일 또는 모레인 데이터만 필터링
            if (fcstTime.equals(specifiedTime) && isSpecifiedDate(fcstDate) && (category.equals("TMP") || category.equals("SKY"))) {
                // 날짜별로 데이터 그룹화
                String key = fcstDate + fcstTime;
                groupedData.putIfAbsent(key, new HashMap<>());
                Map<String, String> dataPoint = groupedData.get(key);

                dataPoint.put("fcstDate", fcstDate);
                dataPoint.put("fcstTime", fcstTime);

                if (category.equals("TMP")) {
                    dataPoint.put("TMP", fcstValue);
                }

                if (category.equals("SKY")) {
                    dataPoint.put("SKY", fcstValue);
                }
            }
        }

        List<Map<String, String>> resultList = new ArrayList<>(groupedData.values());
        resultList.sort(Comparator.comparing(dataPoint -> dataPoint.get("fcstDate")));

        return resultList;
    }

    private boolean isSpecifiedDate(String fcstDate) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate dayAfterTomorrow = today.plusDays(2);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate parsedFcstDate = LocalDate.parse(fcstDate, formatter);

        return parsedFcstDate.equals(today) || parsedFcstDate.equals(tomorrow) || parsedFcstDate.equals(dayAfterTomorrow);
    }


    public List<Map<String, String>> filterNextData(List<WeatherInfoDto> weatherInfoDtoList, LocalTime startTime, int numDataPoints) {
        List<Map<String, String>> filteredList = new ArrayList<>();

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH00");

        for (int i = 0; i < numDataPoints; i++) {
            LocalTime nextTime = startTime.plusHours(i);
            String formattedNextTime = nextTime.format(timeFormatter);

            Map<String, String> dataPoint = new HashMap<>();
            boolean hasTmp = false;
            boolean hasSky = false;

            String fcstDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")); // 현재 날짜를 사용
            String fcstTime = formattedNextTime;

            for (WeatherInfoDto weatherInfoDto : weatherInfoDtoList) {
                String category = weatherInfoDto.getCategory();
                String fcstValue = weatherInfoDto.getFcstValue();

                if (weatherInfoDto.getFcstDate().equals(fcstDate) && weatherInfoDto.getFcstTime().equals(fcstTime) && (category.equals("TMP") || category.equals("SKY"))) {
                    if (category.equals("TMP")) {
                        dataPoint.put("TMP", fcstValue);
                        hasTmp = true;
                    } else if (category.equals("SKY")) {
                        dataPoint.put("SKY", fcstValue);
                        hasSky = true;
                    }

                    if (hasTmp && hasSky) {
                        dataPoint.put("FCST_DATE", fcstDate);
                        dataPoint.put("FCST_TIME", fcstTime);
                        filteredList.add(dataPoint);
                        break;
                    }
                }
            }
        }

        return filteredList;
    }

}
