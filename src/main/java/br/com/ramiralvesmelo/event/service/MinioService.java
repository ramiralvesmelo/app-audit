package br.com.ramiralvesmelo.event.service;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;

@Service
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    // ============ HELPERS ============
    private void ensureBucket() throws Exception {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    public boolean exists(String fileName) throws Exception {
        try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileName)
                    .build()
            );
            return true;
        } catch (io.minio.errors.ErrorResponseException e) {
            if ("NoSuchKey".equalsIgnoreCase(e.errorResponse().code())) {
                return false;
            }
            throw e; // outros erros propagam
        }
    }

    // ============ CREATE (UPLOAD) ============
    public void uploadPdf(String fileName, InputStream inputStream, long size) throws Exception {
        ensureBucket();
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucket)
                .object(fileName)
                .stream(inputStream, size, -1)
                .contentType("application/pdf")
                .build()
        );
    }

    // ============ READ (DOWNLOAD) ============
    public InputStream downloadPdf(String fileName) throws Exception {
        return minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(bucket)
                .object(fileName)
                .build()
        );
    }

    // ============ UPDATE (REPLACE) ============
    /**
     * Atualiza/substitui o conteúdo do PDF. No MinIO (S3-like),
     * um novo putObject com o mesmo key faz "overwrite".
     */
    public void updatePdf(String fileName, InputStream inputStream, long size) throws Exception {
        ensureBucket();
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucket)
                .object(fileName)
                .stream(inputStream, size, -1)
                .contentType("application/pdf")
                .build()
        );
    }

    // ============ DELETE (REMOVE) ============
    public void deletePdf(String fileName) throws Exception {
        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucket)
                .object(fileName)
                .build()
        );
    }
}
