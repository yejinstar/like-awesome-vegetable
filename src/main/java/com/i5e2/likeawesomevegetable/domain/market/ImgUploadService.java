package com.i5e2.likeawesomevegetable.domain.market;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.i5e2.likeawesomevegetable.repository.CompanyBuyingImageRepository;
import com.i5e2.likeawesomevegetable.repository.FarmAuctionImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ImgUploadService {

    private final AmazonS3Client amazonS3Client;
    private final FarmAuctionImageRepository farmAuctionImageRepository;
    private final CompanyBuyingImageRepository companyBuyingImageRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public FarmAuctionImageResponse farmUploadImg(MultipartFile multipartFile,FarmAuction farmAuction) throws IOException {

        log.info("서비스에서 파일 확인 : " + multipartFile.getOriginalFilename());

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        //파일크기 설정
        objectMetadata.setContentLength(multipartFile.getSize());

        //이름 설정
        String originalFilename = multipartFile.getOriginalFilename();
        int index = originalFilename.lastIndexOf(".");
        String ext = originalFilename.substring(index + 1);

        //저장된 파일 이름
        String upLoadFileName = UUID.randomUUID() + "." + ext;
        //저장할 디렉토리 경로+파일
        String key = "farm/img/" + upLoadFileName;

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, key, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new FileUploadException();
        }

        String storeFileUrl = amazonS3Client.getUrl(bucket, key).toString();
        FarmAuctionImage farmAuctionImage = new FarmAuctionImage(storeFileUrl, originalFilename,farmAuction);
        farmAuctionImageRepository.save(farmAuctionImage);


        log.info("서비스 끝 & 저장된 파일 이름  : " + upLoadFileName);
        log.info("저장 경로  : " + key);

        return FarmAuctionImageResponse.of(originalFilename, "파일 등록 성공");
    }



//    public CompanyBuyingResponse companyUploadImg(MultipartFile multipartFile) throws IOException {
//
//        log.info("서비스에서 파일 확인 : " + multipartFile.getOriginalFilename());
//
//        ObjectMetadata objectMetadata = new ObjectMetadata();
//        objectMetadata.setContentType(multipartFile.getContentType());
//        //파일크기 설정
//        objectMetadata.setContentLength(multipartFile.getSize());
//
//        //이름 설정
//        String originalFilename = multipartFile.getOriginalFilename();
//        int index = originalFilename.lastIndexOf(".");
//        String ext = originalFilename.substring(index + 1);
//
//        //저장된 파일 이름
//        String upLoadFileName = UUID.randomUUID() + "." + ext;
//        //저장할 디렉토리 경로+파일
//        String key = "company/img/" + upLoadFileName;
//
//        try (InputStream inputStream = multipartFile.getInputStream()) {
//            amazonS3Client.putObject(new PutObjectRequest(bucket, key, inputStream, objectMetadata)
//                    .withCannedAcl(CannedAccessControlList.PublicRead));
//        } catch (IOException e) {
//            throw new FileUploadException();
//        }
//
//        String storeFileUrl = amazonS3Client.getUrl(bucket, key).toString();
//        CompanyBuyingImage companyBuyingImage = new CompanyBuyingImage(storeFileUrl,originalFilename);
//        companyBuyingImageRepository.save(companyBuyingImage);
//
//
//        log.info("서비스 끝 & 저장된 파일 이름  : " + upLoadFileName);
//        log.info("저장 경로  : " + key);
//
//        return CompanyBuyingResponse.of(originalFilename, "파일 등록 성공");
//    }
}
