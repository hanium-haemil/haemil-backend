package com.haemil.backend.weather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.ResponseStatus;
import com.haemil.backend.weather.dto.AirDto;
import com.haemil.backend.weather.dto.AirInfoDto;
import com.haemil.backend.weather.dto.TransferDto;
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
  private final StationService stationService;

  @Value("${api.hj-secret-key}")
  String serviceKey;

  public String getAirInfo(AirDto airDto, TransferDto transferDto) throws BaseException {
    String responseBody;
    try {
      String apiUrl = airDto.getApiUrl();
      String returnType = airDto.getReturnType();
      String numOfRows = airDto.getNumOfRows();
      String pageNo = airDto.getPageNo();
      String stationName =
          stationService.getStationName(stationService.getStationInfo(transferDto));
      String dataTerm = airDto.getDataTerm();
      String ver = airDto.getVer();

      StringBuilder urlBuilder = new StringBuilder(apiUrl);
      urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey);
      urlBuilder.append(
          "&"
              + URLEncoder.encode("returnType", "UTF-8")
              + "="
              + URLEncoder.encode(returnType, "UTF-8"));
      urlBuilder.append(
          "&"
              + URLEncoder.encode("numOfRows", "UTF-8")
              + "="
              + URLEncoder.encode(numOfRows, "UTF-8"));
      urlBuilder.append(
          "&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(pageNo, "UTF-8"));
      urlBuilder.append("&" + URLEncoder.encode("stationName", "UTF-8") + "=" + stationName);
      urlBuilder.append(
          "&"
              + URLEncoder.encode("dataTerm", "UTF-8")
              + "="
              + URLEncoder.encode(dataTerm, "UTF-8"));
      urlBuilder.append(
          "&" + URLEncoder.encode("ver", "UTF-8") + "=" + URLEncoder.encode(ver, "UTF-8"));

      ResponseEntity<String> response =
          restTemplate.getForEntity(urlBuilder.toString(), String.class);

      responseBody = response.getBody();

    } catch (UnsupportedEncodingException e) {
      log.debug("UnsupportedEncodingException 발생 ");
      throw new BaseException(ResponseStatus.UNSUPPORTED_ENCODING);
    }
    return responseBody;
  }

  public List<AirInfoDto> ParsingJson(String responseBody) throws BaseException {
    List<AirInfoDto> airInfoDtoList;
    try {
      ObjectMapper objectMapper = new ObjectMapper();

      JsonNode jsonNode = objectMapper.readTree(responseBody);
      JsonNode itemsNode = jsonNode.get("response").get("body").get("items");

      airInfoDtoList = new ArrayList<>();
      if (itemsNode.isArray()) {
        for (JsonNode node : itemsNode) {
          String dataTime = node.get("dataTime").asText();
          String pm10Value = node.get("pm10Value").asText();
          String pm25Value = node.get("pm25Value").asText();
          String pm10Grade = node.get("pm10Grade").asText();
          String pm25Grade = node.get("pm25Grade").asText();

          AirInfoDto airInfoDto = new AirInfoDto();
          airInfoDto.setDataTime(dataTime);
          airInfoDto.setPm10Value(pm10Value);
          airInfoDto.setPm10Grade(pm10Grade);
          airInfoDto.setPm25Grade(pm25Grade);
          airInfoDto.setPm25Value(pm25Value);
          airInfoDtoList.add(airInfoDto);
        }
      }

      return airInfoDtoList;
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
