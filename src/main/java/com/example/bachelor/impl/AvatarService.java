package com.example.bachelor.impl;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.log4j.Log4j2;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Log4j2
@Service
class AvatarService {
    private static final String DEFAULT_AVATAR = "images/default_avatar.png";
    private static final String FOLDER_DELIMITER = "/";
    private static final String FORMAT_DELIMITER = ".";
    private static final String DEFAULT_AVATAR_FORMAT = "png";
    private static final Integer AVATAR_SIZE = 100; // 100x100 image
    private static final String PROFILE_FOLDER = "profile";

    private final AmazonS3 s3client;
    private final String uploadBucket;

    public AvatarService(
            @Value("${aws.accessKey}") String awsAccessKey,
            @Value("${aws.secretKey}") String awsSecretKey,
            @Value("${aws.region}") String awsRegion,
            @Value("${aws.uploadsBucket}") String uploadBucket) {

        AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);

        this.s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(awsRegion)
                .build();

        this.uploadBucket = uploadBucket;
    }

    private byte[] downsizeAvatar(InputStream stream) {
        try {
            var imageBuff = ImageIO.read(stream);
            var resized = Scalr.resize(imageBuff, AVATAR_SIZE);

            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ImageIO.write(resized, DEFAULT_AVATAR_FORMAT, byteOut);
            return byteOut.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot process avatar" + e.getMessage());
        }
    }


    String doUpload(String username, InputStream content) {
        var avatar = downsizeAvatar(content);

        var key = PROFILE_FOLDER + FOLDER_DELIMITER + username + FORMAT_DELIMITER + DEFAULT_AVATAR_FORMAT;

        var metadata = new ObjectMetadata();
        metadata.setContentLength(avatar.length);

        var request = new PutObjectRequest(uploadBucket, key, new ByteArrayInputStream(avatar), metadata);

        request.withCannedAcl(CannedAccessControlList.PublicRead);

        s3client.putObject(request);

        return key;
    }

    void copyDefaultAvatar(String username) {
        var targetKey = PROFILE_FOLDER + FOLDER_DELIMITER + username + FORMAT_DELIMITER + DEFAULT_AVATAR_FORMAT;
        var copyObjRequest = new CopyObjectRequest(uploadBucket, DEFAULT_AVATAR, uploadBucket, targetKey);
        copyObjRequest.withCannedAccessControlList(CannedAccessControlList.PublicRead);
        s3client.copyObject(copyObjRequest);
    }

}
