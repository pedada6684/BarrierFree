package com.fullship.hBAF.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fullship.hBAF.domain.place.service.PlaceService;
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
@Slf4j
public class ImageCrawler {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.bucket}")
    private String bucket;

    @Transactional
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
            System.out.println(element.getAttribute("src"));
            String src = element.getAttribute("src");
            String alt = element.getAttribute("alt");
            if (alt.equals(searchKey)){
                //이미지 S3 저장
                return uploadImageToS3(src, alt);
            }else { // 검색결과가 매칭되지 않으면 저장하지 않음
                System.out.println("NOT MATCH");
                System.out.println("alt: " + alt);
                System.out.println("search: " + searchKey);
            }
        }
        return null;
    }

    /**
     * 이미지 로컬 저장 함수
     *
     * @param src  이미지 소스 url
     * @param name 이미지 이름 (alt)
     */
    private URL uploadImageToS3(String src, String name){
        //이미지 저장
        HttpURLConnection conn = null;
        try {
            URL imgUrl = new URL(src);
            conn = (HttpURLConnection) imgUrl.openConnection();
            BufferedImage bufferedImage = ImageIO.read(conn.getInputStream());
            ByteArrayOutputStream imageOutPut = new ByteArrayOutputStream();

            ImageIO.write(bufferedImage, "png", imageOutPut);
            byte[] bytes = imageOutPut.toByteArray();

            // 같은 이름일 경우 덮어쓰기 위하여 (썸네일은 하나만 존재해야함)
            // String imageName = UUID.randomUUID().toString().replace("-", "") + StringUtils.cleanPath(name);
            String imageName = name;
            // set metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/png");
            metadata.setContentLength(bytes.length);
            InputStream imaInputStream = new ByteArrayInputStream(bytes);
            amazonS3Client.putObject(
                    bucket,
                    "ThumbNail/"+imageName,
                    imaInputStream,
                    metadata
            );
            return amazonS3Client.getUrl(bucket, "ThumbNail/"+imageName);
        } catch (IOException e) {

            throw new RuntimeException(e);
        } finally {
            conn.disconnect();
        }
    }
}