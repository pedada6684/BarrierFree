package com.fullship.hBAF.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Util {

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

    public List<Double[]> readExcelFromS3(String fileName) {
        List<Double[]> excelData = new ArrayList<>();
        try (
                // S3에서 파일 가져오기
                S3Object s3Object = amazonS3Client.getObject(bucket, fileName);
                S3ObjectInputStream inputStream = s3Object.getObjectContent();
                // 엑셀 파일 읽기
                Workbook workbook = new XSSFWorkbook(inputStream)
        ) {
            Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트만 읽기
            Iterator<Row> rowIterator = sheet.iterator();

            // 각 행을 읽어서 리스트에 추가
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                List<Double> rowData = new ArrayList<>();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch (cell.getCellTypeEnum()) {
                        case NUMERIC:
                            rowData.add(cell.getNumericCellValue());
                            break;
                        default:
                            // 기타 형식의 셀은 무시
                    }
                }
                // 리스트를 Double[] 배열로 변환하여 리스트에 추가
                excelData.add(rowData.toArray(new Double[0]));
            }
        } catch (IOException e) {
            // 예외 처리
            e.printStackTrace();
            // 빈 리스트를 반환하거나 다른 예외 처리 로직을 추가할 수 있습니다.
        }
        return excelData;
    }
}
