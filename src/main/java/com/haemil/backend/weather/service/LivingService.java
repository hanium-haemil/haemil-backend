package com.haemil.backend.weather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haemil.backend.alert.dto.ReqCoordDto;
import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.ResponseStatus;
import com.haemil.backend.weather.dto.AirDto;
import com.haemil.backend.weather.dto.AirInfoDto;
import com.haemil.backend.weather.dto.LivingDto;
import com.haemil.backend.weather.dto.LivingInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LivingService {
    private final RestTemplate restTemplate;
    @Value("${api.hj-openweather-key}")
    String serviceKey;

    @Value("${api.hj-secret-key}")
    String serviceKey2;

    // 체감 온도
    public String getLivingTempInfo(LivingDto livingDto) throws BaseException {
        String responseBody;
        try {
            String apiUrl = livingDto.getApiUrl();
            String lat = livingDto.getLat();
            String lon = livingDto.getLon();
            String lang = livingDto.getLang();
            String units = livingDto.getUnits();

            StringBuilder urlBuilder = new StringBuilder(apiUrl);
            urlBuilder.append("?"+ URLEncoder.encode("appid", "UTF-8")+"="+serviceKey);
            urlBuilder.append("&"+ URLEncoder.encode("lat", "UTF-8")+"="+URLEncoder.encode(lat, "UTF-8"));
            urlBuilder.append("&"+ URLEncoder.encode("lon", "UTF-8")+"="+URLEncoder.encode(lon, "UTF-8"));
            urlBuilder.append("&"+ URLEncoder.encode("lang", "UTF-8")+"="+URLEncoder.encode(lang, "UTF-8"));
            urlBuilder.append("&"+ URLEncoder.encode("units", "UTF-8")+"="+URLEncoder.encode(units, "UTF-8"));

            ResponseEntity<String> response = restTemplate.getForEntity(urlBuilder.toString(), String.class);

            responseBody = response.getBody();

        } catch (UnsupportedEncodingException e) {
            log.debug("UnsupportedEncodingException 발생 ");
            throw new BaseException(ResponseStatus.UNSUPPORTED_ENCODING);
        }
        return responseBody;
    }

    private final LocationService locationService;
    // 자외선
    public String getUVInfo(LivingDto livingDto, HttpServletRequest request) throws BaseException {
        String responseBody;
        try {
            String apiUrl = livingDto.getApiUrlUV();
            String dataType = livingDto.getDataType();
            String areaNo = locationService.getLocation(locationService.getLocationInfo(request)).getAreaNo();

            if (areaNo.length() >= 2) {
                String modifiedAreaNo = areaNo.substring(0, areaNo.length() - 5) + "00000";
//                locationService.getLocation(locationService.getLocationInfo(request)).setAreaNo(modifiedAreaNo);
                areaNo = modifiedAreaNo;
            }
            String numOfRows = livingDto.getNumOfRows();
            String pageNo = livingDto.getPageNo();
            String time = livingDto.getTime();

            StringBuilder urlBuilder = new StringBuilder(apiUrl);
            urlBuilder.append("?"+ URLEncoder.encode("serviceKey", "UTF-8")+"="+serviceKey2);
            urlBuilder.append("&"+ URLEncoder.encode("dataType", "UTF-8")+"="+URLEncoder.encode(dataType, "UTF-8"));
            urlBuilder.append("&"+ URLEncoder.encode("areaNo", "UTF-8")+"="+URLEncoder.encode(areaNo, "UTF-8"));
            urlBuilder.append("&"+ URLEncoder.encode("numOfRows", "UTF-8")+"="+URLEncoder.encode(numOfRows, "UTF-8"));
            urlBuilder.append("&"+ URLEncoder.encode("pageNo", "UTF-8")+"="+URLEncoder.encode(pageNo, "UTF-8"));
            urlBuilder.append("&"+ URLEncoder.encode("time", "UTF-8")+"="+URLEncoder.encode(time, "UTF-8"));

            ResponseEntity<String> response = restTemplate.getForEntity(urlBuilder.toString(), String.class);

            responseBody = response.getBody();
        } catch (UnsupportedEncodingException e) {
            log.debug("UnsupportedEncodingException 발생 ");
            throw new BaseException(ResponseStatus.UNSUPPORTED_ENCODING);
        }
        return responseBody;
    }

    public List<LivingInfoDto> ParsingJson(String responseBody1, String responseBody2) throws BaseException {
        List<LivingInfoDto> livingInfoDtoList;
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode jsonNode1 = objectMapper.readTree(responseBody1);
//            log.info("feelliketemp = {}", jsonNode1);

            JsonNode temp = jsonNode1.get("main");

            JsonNode jsonNode2 = objectMapper.readTree(responseBody2);
//            log.info("uv = {}", jsonNode2);

            JsonNode uv = jsonNode2.get("response").get("body").get("items").get("item");

            if (temp != null && uv != null) {
                String feelsLike = temp.get("feels_like").asText();

                JsonNode firstItem = uv.get(0);
                String uvi = firstItem.get("h0").asText();

                LivingInfoDto livingInfoDto = new LivingInfoDto();
                livingInfoDto.setFeels_like(feelsLike);
                livingInfoDto.setUvi(uvi);

                livingInfoDtoList = new ArrayList<>();
                livingInfoDtoList.add(livingInfoDto);
            } else {
                livingInfoDtoList = new ArrayList<>();
            }

//            log.debug("livingInfoDtoList:" + livingInfoDtoList);
            return livingInfoDtoList;
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
