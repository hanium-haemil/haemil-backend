package com.haemil.backend.weather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.ResponseStatus;
import com.haemil.backend.weather.dto.LivingDto;
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
public class LocationService {
  private final RestTemplate restTemplate;

  @Value("${api.hj-kakao-key}")
  String serviceKey;

  public String getLocationInfo(HttpServletRequest request) throws BaseException {
    String responseBody;
    try {
      String latitude = request.getParameter("latitude");
      String longitude = request.getParameter("longitude");

      HttpHeaders headers = new HttpHeaders();
      headers.add("Authorization", "KakaoAK " + serviceKey);

      StringBuilder urlBuilder =
          new StringBuilder("https://dapi.kakao.com/v2/local/geo/coord2regioncode.json");
      urlBuilder.append(
          "?" + URLEncoder.encode("x", "UTF-8") + "=" + URLEncoder.encode(longitude, "UTF-8"));
      urlBuilder.append(
          "&" + URLEncoder.encode("y", "UTF-8") + "=" + URLEncoder.encode(latitude, "UTF-8"));

      URI url = new URI(urlBuilder.toString());
      RequestEntity<?> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, url);

      ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
      responseBody = response.getBody();

    } catch (UnsupportedEncodingException e) {
      log.debug("UnsupportedEncodingException 발생 ");
      throw new BaseException(ResponseStatus.UNSUPPORTED_ENCODING);
    } catch (Exception e) {
      log.debug("INVALID_XML_FORMAT Exception 발생");
      throw new BaseException(ResponseStatus.INVALID_XML_FORMAT);
    }
    return responseBody;
  }

  public LivingDto getLocation(String responseBody) throws BaseException {
    try {
      LivingDto livingDto = new LivingDto();
      ObjectMapper objectMapper = new ObjectMapper();

      JsonNode jsonNode = objectMapper.readTree(responseBody);
      JsonNode documentsNode = jsonNode.get("documents");
      JsonNode itemNode = documentsNode.get(0);

      String code = itemNode.get("code").asText();
      livingDto.setAreaNo(code);

      return livingDto;
    } catch (JsonProcessingException e) {
      throw new BaseException(ResponseStatus.CANNOT_CONVERT_JSON);
    }
  }
}
