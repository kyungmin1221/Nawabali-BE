package com.nawabali.nawabali.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.nawabali.nawabali.exception.CustomException;
import com.nawabali.nawabali.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AwsS3Service {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;


    public Map<String, Object> uploadFile(List<MultipartFile> multipartFiles, String dirName) throws IOException {

        if(multipartFiles.size() > 5) {
            throw new CustomException(ErrorCode.MAX_UPLOAD_PHOTO);
        }

        List<String> originalUrls = new ArrayList<>();
        String compressedUrl = null;
        boolean isFirst = true;

        for (MultipartFile file : multipartFiles) {
            String fileName = createFileName(file.getOriginalFilename());
            String filePath = dirName + "/" + fileName;
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            try (InputStream inputStream = file.getInputStream()) {
                byte[] bytes = inputStream.readAllBytes();  // InputStream을 byte 배열로 변환
                InputStream fileInputStream = new ByteArrayInputStream(bytes);  // 원본 파일 InputStream

                amazonS3.putObject(new PutObjectRequest(bucket, filePath, fileInputStream, metadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                String url = amazonS3.getUrl(bucket, filePath).toString();
                originalUrls.add(url);

                if (isFirst) {
                    InputStream compressedInputStream = new ByteArrayInputStream(bytes);  // 리사이즈를 위한 복사된 InputStream
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    Thumbnails.of(compressedInputStream).size(60, 60)
                            .outputQuality(1.0)
                            .toOutputStream(os);
                    byte[] compressedImage = os.toByteArray();
                    ByteArrayInputStream uploadInputStream = new ByteArrayInputStream(compressedImage);
                    ObjectMetadata compressedMetadata = new ObjectMetadata();
                    compressedMetadata.setContentLength(compressedImage.length);
                    compressedMetadata.setContentType(file.getContentType());

                    String compressedFilePath = "compressed_" + filePath;
                    amazonS3.putObject(new PutObjectRequest(bucket, compressedFilePath, uploadInputStream, compressedMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
                    compressedUrl = amazonS3.getUrl(bucket, compressedFilePath).toString();
                    isFirst = false;
                }
            }
        }
        Map<String, Object> response = new HashMap<>();
        response.put("resizedUrl", compressedUrl);
        response.put("originalUrls", originalUrls);
        return response;
    }

    private String createFileName(String originalFilename) {
        return UUID.randomUUID().toString() + getFileExtension(originalFilename);
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    public void deleteFile(String fileName){
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }


    public String uploadSingleFile(MultipartFile multipartFile, String dirName){
        String fileName = createFileName(multipartFile.getOriginalFilename());
        String filePath = dirName + "/" + fileName; // 폴더 경로 추가
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, filePath, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            // S3 URL 생성 및 리스트에 추가
            return amazonS3.getUrl(bucket, filePath).toString();
        } catch (IOException e) {
            throw new CustomException(ErrorCode.PHOTO_UPLOAD_ERROR);
        }
    }

    public S3Object getS3Object(String objectKey){
        return amazonS3.getObject(bucket, objectKey);
    }

    public String getContentType(String objectKey){
        S3Object s3Object = amazonS3.getObject(bucket, objectKey);
        return s3Object.getObjectMetadata().getContentType();
    }

    public String saveAndGetUrl(String resizedFilePath, ByteArrayInputStream inputStream, ObjectMetadata resizedMetadata){
        amazonS3.putObject(new PutObjectRequest(bucket, resizedFilePath, inputStream, resizedMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucket, resizedFilePath).toString();
    }
}
