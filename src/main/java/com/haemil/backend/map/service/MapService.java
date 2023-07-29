package com.haemil.backend.map.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class MapService {

    @Value("${api.naver-client-id}")
    String clientId;

    @Value("${api.naver-client-secret}")
    String clientSecret;

    public String getMapUrl(String reqLocation) throws BaseException {

        String resultUrl = null;
        try {
            String address = reqLocation;
            String addr = URLEncoder.encode(address, "UTF-8");

            log.debug("address = {}", address);

            // Geocoding 개요에 나와있는 API URL 입력.
            String apiURL = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + addr;

            URL url = new URL(apiURL);
            log.debug("url = {}", url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            // Geocoding 개요에 나와있는 요청 헤더 입력.
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);

            // 요청 결과 확인. 정상 호출인 경우 200
            int responseCode = con.getResponseCode();

            BufferedReader br;

            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }

            br.close();

            ObjectMapper objectMapper = new ObjectMapper();
            log.debug("response = {} response.string = {}", response, response.toString());

            JsonNode jsonNode = objectMapper.readTree(response.toString());
            log.debug("jsonNode = {}", jsonNode);
            JsonNode addressesNode = jsonNode.get("addresses");
            log.debug("addressesNode = {}", addressesNode);

            if (addressesNode != null && addressesNode.isArray()) {
                for (JsonNode addressNode : addressesNode) {
                    String roadAddress = getParsedUrl(addressNode.get("roadAddress").asText());

                    String jibunAddress = addressNode.get("jibunAddress").asText();
                    String latitude = addressNode.get("y").asText();
                    String longitude = addressNode.get("x").asText();

                    log.debug("latitude = {} and longitude = {}", latitude, longitude);
                    resultUrl = "nmap://route/public?dlat=" + latitude + "&dlng=" + longitude + "&dname=" + roadAddress;
                }
            }
        } catch (UnsupportedEncodingException e) { // 에러가 발생했을 때 예외 status 명시
            log.debug("UnKnownAddrException 발생 ");
            throw new BaseException(ResponseStatus.UNKNOWN_ADDR);
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultUrl;
    }

    public String getParsedUrl(String roadAddress) throws UnsupportedEncodingException {
        String[] words = roadAddress.split(" ");
        String last_addr = words[words.length - 1];
        return URLEncoder.encode(last_addr, "UTF-8");
    }

}
