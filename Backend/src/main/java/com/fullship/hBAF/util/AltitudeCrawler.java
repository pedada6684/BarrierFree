package com.fullship.hBAF.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AltitudeCrawler {

    public void crawling() {
//        String projectPath = Paths.get(System.getProperty("user.dir")).toString();
        String projectPath = "C:\\Users\\SSAFY\\Downloads\\chromedriver-win64\\chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", projectPath);
        System.out.println("경로: " + projectPath);
        WebDriver webDriver = new ChromeDriver();
        //접속
        try {
            // 위쪽 : https://earth.google.com/web/@36.43247601,127.37553582,120.41121948a,38379.39887954d,35y,360h,0t,0r/data=OgMKATA
            // 아래쪽 : https://earth.google.com/web/@36.27708787,127.37048103,170.3296623a,38329.48360156d,35y,-0h,0t,0r/data=OgMKATA
            String searchUrl = "https://earth.google.com/web/@36.43247601,127.37553582,120.41121948a,38379.39887954d,35y,360h,0t,0r/data=OgMKATA";
            webDriver.get(searchUrl);
            Thread.sleep(10000);
//            webDriver.switchTo().frame("searchIframe");

//            try {
//                // XPath로 요소 찾기
//                WebElement element = webDriver.findElement(By.xpath("//*[@id=\"flt-pv-4\"]/div"));
//
//                // 찾은 요소의 텍스트 가져오기
//                String value = element.getText();
//                System.out.println("찾은 요소의 값: " + value);
//
//            } catch (org.openqa.selenium.NoSuchElementException e) {
//                System.out.println("요소를 찾을 수 없습니다.");
//            } finally {
//                // 브라우저 종료
//                webDriver.quit();
//            }

            // <slot> 요소 선택
//            WebElement slotElement = webDriver.findElement(By.cssSelector("slot[name='flt-pv-slot-3']"));
//            List<WebElement> elements = webDriver.findElements(By.xpath("//*[@id=\"flt-pv-4\"]/div"));
            try {
//            WebElement element = webDriver.findElement(By.xpath("//*[@id=\"flt-pv-4\"]/div"));
//            List<WebElement> elements = webDriver.findElements(By.xpath("slot[name=\"flt-pv-slot-3\"]"));
//            List<WebElement> elements = webDriver.findElements(By.xpath("//*[@id=\"flt-pv-1\"]"));
//            WebElement e = webDriver.findElement(By.cssSelector("slot[name=\"flt-pv-slot-3\"]"));
//            System.out.println(elements.getText());
//            System.out.println("elements: " + elements);

//            WebElement element = webDriver.findElement(By.cssSelector(".web-electable-region-context-menu"));
//
//            System.out.println("값: " + element.getText());

//                WebElement element = webDriver.findElement(By.cssSelector("[slot='flt-pv-slot-3']"));
//
//                // 요소의 텍스트 값 가져오기
//                String value = element.getText();

// WebDriverWait 객체 생성 (Duration 사용)
//                WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(60)); // 30초 대기

                // 페이지가 완전히 로드될 때까지 대기
//                wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete';"));

                // 요소가 나타날 때까지 대기
//                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//flt-scene-host/flt-clip/flt-platform-view-slot/slot")));
//                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("flt-scene-host > flt-clip > flt-platform-view-slot > slot")));
//                WebElement element2 = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[slot='flt-pv-slot-2']")));
//                WebElement element3 = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[slot='flt-pv-slot-3']")));
//                WebElement element4 = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[slot='flt-pv-slot-4']")));

//                WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(60));
//                WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[slot='flt-pv-slot-3']")));

                WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(220));
//                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[slot='flt-pv-slot-3']")));
//                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("flt-scene-host > flt-clip > flt-platform-view-slot > slot")));
//                WebElement element = webDriver.findElement(By.cssSelector("flt-scene-host > flt-clip > flt-platform-view-slot > slot"));
                WebElement element = webDriver.findElement(By.cssSelector("[slot='flt-pv-slot-3']"));
//                WebElement element = webDriver.findElement(By.xpath("/html/body/flutter-view/flt-glass-pane//flt-scene-host/flt-clip/flt-platform-view-slot/slot"));

//                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//flt-scene-host/flt-clip/flt-platform-view-slot/slot")));
//                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/flutter-view/flt-glass-pane//flt-scene-host/flt-clip/flt-platform-view-slot/slot")));
                String text = element.getText();

                for (int i = 0; i < 10; i++) {
                    String tt = element.getText();
                    System.out.println("Attempt " + (i + 1) + ": Element tt: " + tt);
                    Thread.sleep(1000); // 1초 대기
                }
                System.out.println("text: " + text);

                // 요소의 텍스트 출력
//                System.out.println(element2.getText());
                System.out.println("값: " + element.getText());
//                System.out.println(element4.getText());

//                System.out.println("slot='flt-pv-slot-2'의 값: " + value);

            } catch (org.openqa.selenium.NoSuchElementException e) {
                System.out.println("요소를 찾을 수 없습니다.");
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                // 브라우저 종료
                webDriver.quit();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
