package com.fullship.hBAF.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageUtil {

    @Value("${cloud.aws.bucket}")
    private String bucket;
    private final AmazonS3Client amazonS3Client;

    /**
     * 이미지 url S3 저장 메서드
     * @param src  이미지 소스 url
     * @param folder  이미지 저장 폴더 이름
     * @param name 이미지 이름 (alt)
     * @return S3 url
     */
    public URL uploadImageToS3(String src, String folder , String name){
        //이미지 저장
        HttpURLConnection conn = null;
        try {
            URL imgUrl = new URL(src);
            conn = (HttpURLConnection) imgUrl.openConnection();
            BufferedImage bufferedImage = ImageIO.read(conn.getInputStream());
            ByteArrayOutputStream imageOutPut = new ByteArrayOutputStream();

            ImageIO.write(bufferedImage, "png", imageOutPut);
            byte[] bytes = imageOutPut.toByteArray();

            // set metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/png");
            metadata.setContentLength(bytes.length);
            InputStream imageInputStream = new ByteArrayInputStream(bytes);
            amazonS3Client.putObject(
                    bucket,
                    folder+"/"+name,
                    imageInputStream,
                    metadata
            );
            return amazonS3Client.getUrl(bucket, folder+"/"+name);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FAIL_TO_UPLOAD_S3);
        } finally {
            conn.disconnect();
        }
    }

    /**
     * 이미지 파일 S3 저장 메서드
     * @param file  이미지 소스 파일
     * @param folder  이미지 저장 폴더 이름
     * @param name 이미지 이름 (alt)
     * @return S3 url
     */
    public URL uploadImageToS3(MultipartFile file, String folder , String name){
        // set metadata
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            InputStream imageInputStream = file.getInputStream();
            amazonS3Client.putObject(
                    bucket,
                    folder+"/"+name,
                    imageInputStream,
                    metadata
            );
            return amazonS3Client.getUrl(bucket, folder+"/"+name);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FAIL_TO_UPLOAD_S3);
        }
    }

}
