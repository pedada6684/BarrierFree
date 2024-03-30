package com.fullship.hBAF.util;

import com.fullship.hBAF.domain.place.entity.Place;
import com.fullship.hBAF.domain.place.service.PlaceService;
import com.fullship.hBAF.domain.place.service.command.UpdatePlaceImageCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ImageCrawler {
    private final PlaceService placeService;
    private final ImageUtil imageUtil;

    /**
     * 베리어프리 장소 썸네일 이미지 업데이트 메서드
     * @param placeList: 장소 리스트
     */
    public void updatelBFImage(List<Place> placeList) {
        //초기설정
        //TODO: driver 분화 필요
        //TODO: 경로 yml파일로 관리 필요 더 찾아볼 것
        String projectPath = Paths.get(System.getProperty("user.dir")).toString();
        String path = projectPath + "\\asset\\img\\thumbnail\\";
        System.setProperty("webdriver.chrome.driver", projectPath +"\\chromedriver-win64\\chromedriver.exe");
        WebDriver webDriver = new ChromeDriver();

        for (Place place : placeList) {
            URL s3Url = crawlThumbnailImageNaver(place.getPlaceName(), webDriver);
            if (s3Url == null) { // 네이버에 없을 시 카카오에서 재검색
               s3Url = crawlThumbnailImageKakao(place.getPlaceName(), webDriver);
               if (s3Url == null) continue;
            }

            UpdatePlaceImageCommand updatePlaceImageCommand = UpdatePlaceImageCommand.builder()
                    .placeId(place.getId())
                    .imageUrl(s3Url.toString())
                    .build();
            placeService.updatePlaceImageUrl(updatePlaceImageCommand);
        }
    }

    /**
     * 네이버 썸네일 크롤링
     * @param searchKey
     * @param webDriver
     * @return
     */
    private URL crawlThumbnailImageNaver(String searchKey, WebDriver webDriver) {
        //접속
        try {
            String searchUrl = "https://map.naver.com/p/search/"+ URLEncoder.encode(searchKey, "utf-8");
            webDriver.get(searchUrl);
            Thread.sleep(1000);
            webDriver.switchTo().frame("searchIframe");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);//TODO: 커스텀 Exception으로 수정필요
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFrameException e) {
            log.warn("NoSuchFrameException");
            return null;
        }
        //크롤링
        List<WebElement> elements = webDriver.findElements(By.cssSelector(".lazyload-wrapper img"));
        for (WebElement element : elements) {
            String src = element.getAttribute("src");
            String alt = element.getAttribute("alt");
            if (alt.equals(searchKey)){ //이미지 S3 저장
                return imageUtil.uploadImageToS3(src, "ThumbNail",alt);
            }else { // 검색결과가 매칭되지 않으면 저장하지 않음
                log.info("NOT MATCH");
                log.info("alt: " + alt);
                log.info("search: " + searchKey);
            }
        }
        return null;
    }

    /**
     * 카카오 썸네일 크롤링
     * @param searchKey
     * @param webDriver
     * @return
     */
    private URL crawlThumbnailImageKakao(String searchKey, WebDriver webDriver) {
        //접속
        try {
            String searchUrl = "https://m.map.kakao.com/actions/searchView?q="+ URLEncoder.encode(searchKey, "utf-8");
            webDriver.get(searchUrl);
            Thread.sleep(1000);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // CSS 선택자를 사용하여 요소 찾기
        try {
            WebElement element = webDriver.findElement(By.cssSelector("li[data-title='"+searchKey +"'] .wrap_img img"));
            // src 속성 값 가져오기
            String src = element.getAttribute("src");
            return imageUtil.uploadImageToS3(src, "ThumbNail",searchKey);
        } catch (NoSuchElementException e) {
            return null;
        }
    }
}