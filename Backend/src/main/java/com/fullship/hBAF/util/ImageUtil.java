package com.fullship.hBAF.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageUtil {

    @Value("${cloud.aws.bucket}")
    private String bucket;
    private final AmazonS3Client amazonS3Client;

    /**
     * 이미지 S3 저장 메서드
     *
     * @param src  이미지 소스 url
     * @param folder  이미지 저장 폴더 이름
     * @param name 이미지 이름 (alt)
     * @return S3 url
     */
    public URL uploadImageToS3(String src, String folder ,String name){
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
            String profileName = UUID.randomUUID().toString().replace("-", "") + name;

            String imageName = name;
            // set metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/png");
            metadata.setContentLength(bytes.length);
            InputStream imaInputStream = new ByteArrayInputStream(bytes);
            amazonS3Client.putObject(
                    bucket,
                    folder+"/"+imageName,
                    imaInputStream,
                    metadata
            );
            return amazonS3Client.getUrl(bucket, folder+"/"+imageName);
        } catch (IOException e) {

            throw new RuntimeException(e);
        } finally {
            conn.disconnect();
        }
    }
}
