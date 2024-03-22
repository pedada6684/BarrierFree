package com.fullship.hBAF.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fullship.hBAF.domain.place.service.PlaceService;
import com.fullship.hBAF.domain.place.service.command.UpdatePlaceImageCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
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
     * @param placeName: 장소명
     * @param placeId: 장소 id
     */
    public void updatelBFImage(String placeName, Long placeId) {
        URL s3Url = crawlThumbnailImage(placeName);
        UpdatePlaceImageCommand updatePlaceImageCommand = UpdatePlaceImageCommand.builder()
                .placeId(placeId)
                .imageUrl(s3Url.toString())
                .build();
        placeService.updatePlaceImageUrl(updatePlaceImageCommand);
    }

    public URL crawlThumbnailImage(String searchKey) {
        //초기설정
        //TODO: driver 분화 필요
        //TODO: 경로 yml파일로 관리 필요 더 찾아볼 것
        String projectPath = Paths.get(System.getProperty("user.dir")).toString();
        String path = projectPath + "\\asset\\img\\thumbnail\\";
        System.setProperty("webdriver.chrome.driver", projectPath +"\\chromedriver-win64\\chromedriver.exe");
        WebDriver webDriver = new ChromeDriver();
        //접속
        try {
            String searchUrl = "https://map.naver.com/p/search/"+ URLEncoder.encode(searchKey, "utf-8");
            webDriver.get(searchUrl);
            Thread.sleep(100);
            webDriver.switchTo().frame("searchIframe");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);//TODO: 커스텀 Exception으로 수정필요
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
}