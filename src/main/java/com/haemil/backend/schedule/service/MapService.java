package com.haemil.backend.schedule.service;

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

    @Value("${api.kakao-rest-api-key}")
    String restApiKey;

    public String getMapUrl(String reqLocation) throws BaseException {

        String resultUrl = null;
        try {
            String address = reqLocation;
            String addr = URLEncoder.encode(address, "UTF-8");

            // Geocoding 개요에 나와있는 API URL 입력.
            String apiURL = "https://dapi.kakao.com/v2/local/search/keyword.json?page=1&size=1&sort=accuracy&query="+addr;

            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            // 헤더
            con.setRequestProperty("Authorization", "KakaoAK " + restApiKey);

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
            JsonNode jsonNode = objectMapper.readTree(response.toString());
            JsonNode itemsNode = jsonNode.get("documents");

            if (itemsNode != null && itemsNode.isArray() && itemsNode.size() > 0) {
                JsonNode firstItem = itemsNode.get(0);
                String mapx = firstItem.get("y").asText();
                String mapy = firstItem.get("x").asText();
                resultUrl = "nmap://route/public?dlat=" + mapx + "&dlng=" + mapy + "&dname=" + addr;
            }
        } catch (UnsupportedEncodingException e) {
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
