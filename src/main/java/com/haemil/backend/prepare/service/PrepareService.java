package com.haemil.backend.prepare.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.ResponseStatus;
import com.haemil.backend.prepare.dto.PrePareInfoDto;
import com.haemil.backend.prepare.dto.PrepareDto;
import com.haemil.backend.weather.dto.AirInfoDto;
import com.haemil.backend.weather.dto.WeatherInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrepareService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public void filterWeatherData(List<WeatherInfoDto> weatherInfoDtoList, PrepareDto prepareDto) {
        for (WeatherInfoDto weatherInfoDto : weatherInfoDtoList) {
            String category = weatherInfoDto.getCategory();

            if (category.equals("TMP")) {
                prepareDto.setTmp(weatherInfoDto.getFcstValue());
            } else if (category.equals("POP")) {
                prepareDto.setPop(weatherInfoDto.getFcstValue());
            } else if (category.equals("PTY")) {
                prepareDto.setPty(weatherInfoDto.getFcstValue());
            } else if (category.equals("PCP")) {
                prepareDto.setPcp(weatherInfoDto.getFcstValue());
            } else if (category.equals("REH")) {
                prepareDto.setReh(weatherInfoDto.getFcstValue());
            } else if (category.equals("SKY")) {
                prepareDto.setSky(weatherInfoDto.getFcstValue());
            }
        }
    }

    public void filterAirData(List<AirInfoDto> airInfoDto, PrepareDto prepareDto) {
        AirInfoDto resultDto = airInfoDto.get(0);
        prepareDto.setPm10grade(resultDto.getPm10Grade());
        prepareDto.setPm25grade(resultDto.getPm25Grade());
    }

    public String ParsingJson(List<PrepareDto> prepareDtoList) throws BaseException {
        List<PrePareInfoDto> prePareInfoDtoList = new ArrayList<>();

        try {
            for (PrepareDto prepareDto : prepareDtoList) {
                PrePareInfoDto prePareInfoDto = new PrePareInfoDto();
                // PrepareDto의 필드 값을 PrePareInfoDto에 설정
                prePareInfoDto.setMask(getMaskValue(prepareDto.getPm10grade(), prepareDto.getPm25grade()));
                prePareInfoDto.setClothes(getClothesValue(prepareDto.getMaxTemp(), prepareDto.getMinTemp(), prepareDto.getTmp()));
                prePareInfoDto.setUmbrella(needUmbrella(prepareDto.getPop(), prepareDto.getPty()));
                int percent = getPercentValue(prepareDto);
                prePareInfoDto.setPercent(percent);

                String result = getResultValue(percent);
                prePareInfoDto.setResult(result);

                prePareInfoDtoList.add(prePareInfoDto);
            }
            return convertObjectToJson(prePareInfoDtoList);
        } catch (JsonProcessingException e) {
            throw new BaseException(ResponseStatus.CANNOT_CONVERT_JSON);
        }
    }

    private String convertObjectToJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }


    // 마스크 착용 여부를 판단하는 메서드
    private String getMaskValue(String pm10grade, String pm25grade) {
        // 미세먼지 + 초미세먼지 의 평균으로 비교
        int pm10gradeInt = Integer.parseInt(pm10grade);
        int pm25gradeInt = Integer.parseInt(pm25grade);
        double result = (pm10gradeInt + pm25gradeInt) / 2;

        if (result < 2)
            return "자율";
        else if (result < 3)
            return "권고";
        else
            return "필수";
    }

    // 기온에 따른 옷차림을 판단하는 메서드
    //평균온도로
    private String getClothesValue(String maxTemp, String minTemp, String temp) {
        // 실시간 온도로 가져옴
//        double todayTemp = Double.parseDouble(temp);
        // 또는 평균 온도로도 가능하게
        double todayTemp = (Double.parseDouble(maxTemp) + Double.parseDouble(minTemp)) / 2;

        if (todayTemp >= 28.0) {
            return "민소매, 반팔, 반바지, 원피스";
        } else if (todayTemp >= 23.0) {
            return "반팔, 얇은 셔츠, 반바지, 면바지";
        } else if (todayTemp >= 20.0) {
            return "얇은 가디건, 긴팔, 면바지, 청바지";
        } else if (todayTemp >= 17.0) {
            return "얇은 니트, 맨투맨, 가디건, 청바지";
        } else if (todayTemp >= 12.0) {
            return "자켓, 가디건, 야상, 스타킹, 청바지, 면바지";
        } else if (todayTemp >= 9.0) {
            return "자켓, 트렌치코트, 야상, 니트, 청바지, 스타킹";
        } else if (todayTemp >= 5.0) {
            return "코트, 가죽자켓, 히트텍, 니트, 레깅스";
        } else {
            return "패딩, 두꺼운코트, 목도리, 기모제품";
        }
    }

    // 강수 확률과 형태에 따른 우산 우선 여부를 판단하는 메서드
    private boolean needUmbrella(String pop, String pty) {
        if (pop == "0" && pty == "0") {
            return false; // 눈이나 비가 안옴
        }
        else {
            return true; // 눈이나 비가 옴
        }
    }

    public int getPercentValue(PrepareDto prepareDto) throws BaseException {
        // 구현 방법에 따라 외출 적합도를 판단하여 해당 퍼센트 값을 반환
        int percentValue = 0; // 디폴트 값 0

        try {
            // 현재 실시간 weather 정보
            int temperature = Integer.parseInt(prepareDto.getTmp()); // 기온
            int precipitationProbability = Integer.parseInt(prepareDto.getPop()); // 강수확률
            int humidity = Integer.parseInt(prepareDto.getReh()); // 습도
            int pm10 = Integer.parseInt(prepareDto.getPm10grade()); // 미세먼지 등급
            int pm25 = Integer.parseInt(prepareDto.getPm25grade()); // 초미세먼지 등급
            String airQuality = airQualityScore(pm10, pm25); // 대기질 등급 계산

            // Temperature calculation
            if (temperature >= 20 && temperature <= 25) {
                percentValue += 10;
            } else if ((temperature >= 15 && temperature < 20) || (temperature > 25 && temperature <= 30)) {
                percentValue += 5;
            } else if ((temperature >= 10 && temperature < 15) || (temperature > 30 && temperature <= 35)) {
                percentValue -= 5;
            } else if ((temperature >= 0 && temperature < 10) || (temperature > 35 && temperature <= 40)) {
                percentValue -= 10;
            } else if (temperature < 0 || temperature >= 40) {
                percentValue -= 20;
            }

            // Precipitation probability calculation
            if (precipitationProbability <= 30) {
                percentValue += 10;
            } else if (precipitationProbability <= 50) {
                percentValue += 5;
            } else if (precipitationProbability <= 70) {
                percentValue += 0;
            } else if (precipitationProbability <= 80) {
                percentValue -= 5;
            } else if (precipitationProbability > 80) {
                percentValue -= 10;
            }

            // Humidity calculation
            if (humidity >= 30 && humidity <= 50) {
                percentValue += 10;
            } else if ((humidity >= 20 && humidity < 30) || (humidity > 50 && humidity <= 60)) {
                percentValue += 5;
            } else if ((humidity >= 10 && humidity < 20) || (humidity > 60 && humidity <= 70)) {
                percentValue -= 5;
            } else if ((humidity >= 0 && humidity < 10) || (humidity > 70 && humidity <= 80)) {
                percentValue -= 10;
            } else if (humidity > 80) {
                percentValue -= 20;
            }

            // Air quality calculation
            switch (airQuality) {
                case "좋음":
                    percentValue += 10;
                    break;
                case "보통":
                    percentValue += 5;
                    break;
                case "나쁨":
                    percentValue -= 5;
                    break;
                case "매우 나쁨":
                    percentValue -= 10;
                    break;
                case "위험":
                    percentValue -= 20;
                    break;
                default:
                    break;
            }

            // Make sure the percent value stays within 0-100 range
            percentValue = Math.max(0, Math.min(100, percentValue));
        } catch (NumberFormatException e) {
            // Handle parsing errors if needed
            log.error("NumberFormatException occurred while parsing data: {}", e.getMessage());
            throw new BaseException(ResponseStatus.INVALID_DATA_FORMAT);
        }

        return percentValue;
    }

    private String airQualityScore(int pm10, int pm25) {
        int pmScore = 0;

        // PM10 점수 계산
        if (pm10 >= 0 && pm10 <= 30) {
            pmScore += 10;
        } else if (pm10 >= 31 && pm10 <= 80) {
            pmScore += 0;
        } else if (pm10 >= 81 && pm10 <= 150) {
            pmScore -= 5;
        } else if (pm10 >= 151) {
            pmScore -= 10;
        }

        // PM2.5 점수 계산
        if (pm25 >= 0 && pm25 <= 15) {
            pmScore += 10;
        } else if (pm25 >= 16 && pm25 <= 35) {
            pmScore += 0;
        } else if (pm25 >= 36 && pm25 <= 75) {
            pmScore -= 5;
        } else if (pm25 >= 76) {
            pmScore -= 10;
        }

        // 대기질 점수 계산
        String airQualityScore = "";

        if (pmScore >= 10) {
            airQualityScore = "좋음";  // 좋음
        } else if (pmScore >= 0) {
            airQualityScore = "보통";   // 보통
        } else if (pmScore >= -5) {
            airQualityScore = "나쁨";   // 나쁨
        } else if (pmScore >= -10) {
            airQualityScore = "매우 나쁨";  // 매우 나쁨
        } else {
            airQualityScore = "위험";  // 위험
        }

        return airQualityScore;
    }

    public String getResultValue(int percent) {
        if (percent >= 0 && percent <= 20) {
            return "외출하기 매우 나쁜 날이에요! 가능하면 실내에서 활동하세요.";
        } else if (percent <= 40) {
            return "외출하기에는 조금 어려운 날씨에요. 외출 시에는 주의하세요.";
        } else if (percent <= 60) {
            return "외출하기 보통인 날씨에요. 잊은 물건은 없는지 확인하세요.";
        } else if (percent <= 80) {
            return "외출하기 좋은 날씨에요! 쾌적한 날씨를 즐기며 외출하세요.";
        } else {
            return "외출하기 아주 좋은 날씨에요! 좋은 날씨를 만끽해보세요.";
        }
    }
}