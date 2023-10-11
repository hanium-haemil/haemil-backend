package com.haemil.backend.prepare.service;

import com.haemil.backend.global.config.BaseException;
import com.haemil.backend.global.config.ResponseStatus;
import com.haemil.backend.prepare.dto.PrePareInfoDto;
import com.haemil.backend.prepare.dto.PrepareDto;
import com.haemil.backend.prepare.dto.PrepareNeedInfoDto;
import com.haemil.backend.prepare.dto.PrepareWeatherDto;
import com.haemil.backend.weather.dto.AirInfoDto;
import com.haemil.backend.weather.dto.WeatherInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrepareService {

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
      } else if (category.equals("WSD")) {
        prepareDto.setWsd(weatherInfoDto.getFcstValue());
      }
    }
  }

  public void filterAirData(List<AirInfoDto> airInfoDto, PrepareDto prepareDto) {
    AirInfoDto resultDto = airInfoDto.get(0);
    prepareDto.setPm10grade(resultDto.getPm10Grade());
    prepareDto.setPm25grade(resultDto.getPm25Grade());
    prepareDto.setPm10value(resultDto.getPm10Value());
    prepareDto.setPm25value(resultDto.getPm25Value());
  }

  public List<PrePareInfoDto> ParsingJson(List<PrepareDto> prepareDtoList) throws BaseException {
    List<PrePareInfoDto> prePareInfoDtoList = new ArrayList<>();

    for (PrepareDto prepareDto : prepareDtoList) {
      PrePareInfoDto prePareInfoDto = new PrePareInfoDto();

      prePareInfoDto.setPm10value(prepareDto.getPm10value());

      prePareInfoDto.setClothes(getClothesValue(prepareDto.getMaxTemp(), prepareDto.getMinTemp()));

      prePareInfoDto.setFeel_like(prepareDto.getFeelLike());
      prePareInfoDto.setUv(getUvStatus(prepareDto));

      int percent = getPercentValue(prepareDto);
      prePareInfoDto.setPercent(percent);

      String result = getResultValue(percent);
      prePareInfoDto.setResult(result);

      prePareInfoDtoList.add(prePareInfoDto);
    }
    return prePareInfoDtoList;
  }

  public List<PrepareNeedInfoDto> ParsingNeed(List<PrepareDto> prepareDtoList)
      throws BaseException {
    List<PrepareNeedInfoDto> prePareInfoDtoList = new ArrayList<>();

    for (PrepareDto prepareDto : prepareDtoList) {
      PrepareNeedInfoDto prePareInfoDto = new PrepareNeedInfoDto();

      prePareInfoDto.setMask(getMaskValue(prepareDto.getPm10grade(), prepareDto.getPm25grade()));
      prePareInfoDto.setUmbrella(needUmbrella(prepareDto.getPop(), prepareDto.getPty()));
      prePareInfoDto.setClothes(getClothesValue(prepareDto.getMaxTemp(), prepareDto.getMinTemp()));

      prePareInfoDtoList.add(prePareInfoDto);
    }

    return prePareInfoDtoList;
  }

  public List<PrepareWeatherDto> ParsingWeather(List<PrepareDto> prepareDtoList)
      throws BaseException {
    List<PrepareWeatherDto> prePareInfoDtoList = new ArrayList<>();

    for (PrepareDto prepareDto : prepareDtoList) {
      PrepareWeatherDto prePareInfoDto = new PrepareWeatherDto();
      prePareInfoDto.setPm10value(prepareDto.getPm10value());
      prePareInfoDto.setPm25value(prepareDto.getPm25value());
      prePareInfoDto.setPop(prepareDto.getPop());
      prePareInfoDto.setWsd(prepareDto.getWsd());
      prePareInfoDto.setPcp(prepareDto.getPcp());
      prePareInfoDto.setUv(prepareDto.getUv());

      prePareInfoDtoList.add(prePareInfoDto);
    }
    return prePareInfoDtoList;
  }

  // 마스크 착용 여부를 판단하는 메서드
  private String getMaskValue(String pm10grade, String pm25grade) {
    double result = 0.0;

    if (pm10grade != "null" && pm25grade != "null") {
      // 미세먼지 + 초미세먼지 의 평균으로 비교
      int pm10gradeInt = Integer.parseInt(pm10grade);
      int pm25gradeInt = Integer.parseInt(pm25grade);
      result = (pm10gradeInt + pm25gradeInt) / 2;
    } else if (pm10grade == "null" && pm25grade != "null") {
      int pm25gradeInt = Integer.parseInt(pm25grade);
      result = pm25gradeInt;
    } else if (pm25grade == "null" && pm10grade != "null") {
      int pm10gradeInt = Integer.parseInt(pm10grade);
      result = pm10gradeInt;
    }

    if (result < 2) return "자율";
    else if (result < 3) return "권고";
    else return "필수";
  }

  private String getUvStatus(PrepareDto prepareDto) {
    int uvIndex = Integer.parseInt(prepareDto.getUv()); // 자외선 지수

    if (uvIndex <= 2) {
      return "아주 좋음";
    } else if (uvIndex >= 3 && uvIndex <= 5) {
      return "좋음";
    } else if (uvIndex >= 6 && uvIndex <= 7) {
      return "보통";
    } else if (uvIndex >= 8 && uvIndex <= 10) {
      return "나쁨";
    } else {
      return "아주 나쁨";
    }
  }

  // 기온에 따른 옷차림을 판단하는 메서드
  private String getClothesValue(String maxTemp, String minTemp) {
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
  private String needUmbrella(String pop, String pty) {
    if (pop.equals("0") && pty.equals("0")) {
      return "우산 필요 없음"; // 눈이나 비가 안옴
    } else {
      return "우산 필요"; // 눈이나 비가 옴
    }
  }

  public int getPercentValue(PrepareDto prepareDto) throws BaseException {
    // 구현 방법에 따라 외출 적합도를 판단하여 해당 퍼센트 값을 반환
    int percentValue = 50; // 디폴트 값 0

    try {
      // 현재 실시간 weather 정보
      int temperature = Integer.parseInt(prepareDto.getTmp()); // 기온
      int precipitationProbability = Integer.parseInt(prepareDto.getPop()); // 강수확률
      int humidity = Integer.parseInt(prepareDto.getReh()); // 습도
      String airQuality =
          airQualityScore(prepareDto.getPm10grade(), prepareDto.getPm25grade()); // 대기질 등급 계산

      // Temperature calculation
      if (temperature >= 20 && temperature <= 25) {
        percentValue += 10;
      } else if ((temperature >= 15 && temperature < 20)
          || (temperature > 25 && temperature <= 30)) {
        percentValue += 5;
      } else if ((temperature >= 10 && temperature < 15)
          || (temperature > 30 && temperature <= 35)) {
        percentValue -= 5;
      } else if ((temperature >= 0 && temperature < 10)
          || (temperature > 35 && temperature <= 40)) {
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

      int uvIndex = Integer.parseInt(prepareDto.getUv()); // 자외선 지수
      if (uvIndex >= 0 && uvIndex <= 2) {
        percentValue += 10;
      } else if (uvIndex >= 3 && uvIndex <= 5) {
        percentValue += 5;
      } else if (uvIndex >= 6 && uvIndex <= 7) {
        percentValue += 0;
      } else if (uvIndex >= 8 && uvIndex <= 10) {
        percentValue -= 5;
      } else if (uvIndex >= 11) {
        percentValue -= 10;
      }

      percentValue = Math.max(0, Math.min(100, percentValue));
    } catch (NumberFormatException e) {
      log.error("NumberFormatException occurred while parsing data: {}", e.getMessage());
      throw new BaseException(ResponseStatus.INVALID_DATA_FORMAT);
    }

    return percentValue;
  }

  private String airQualityScore(String pm10S, String pm25S) {
    int pmScore = 0;

    if (pm10S != "null") {
      int pm10 = Integer.parseInt(pm10S);
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
    }

    if (pm25S != "null") {
      int pm25 = Integer.parseInt(pm25S);
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
    }

    // 대기질 점수 계산
    String airQualityScore = "";

    if (pmScore >= 10) {
      airQualityScore = "좋음"; // 좋음
    } else if (pmScore >= 0) {
      airQualityScore = "보통"; // 보통
    } else if (pmScore >= -5) {
      airQualityScore = "나쁨"; // 나쁨
    } else if (pmScore >= -10) {
      airQualityScore = "매우 나쁨"; // 매우 나쁨
    } else {
      airQualityScore = "위험"; // 위험
    }

    return airQualityScore;
  }

  public String getResultValue(int percent) {
    if (percent >= 0 && percent <= 20) {
      return "외출하기 매우 나쁜 날이에요!";
    } else if (percent <= 40) {
      return "외출하기에는 조금 어려운 날씨에요.";
    } else if (percent <= 60) {
      return "외출하기 보통인 날씨에요.";
    } else if (percent <= 80) {
      return "외출하기 좋은 날씨에요!";
    } else {
      return "외출하기 아주 좋은 날씨에요!";
    }
  }
}
