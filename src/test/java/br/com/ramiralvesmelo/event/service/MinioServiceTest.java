package br.com.ramiralvesmelo.event.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.time.Duration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.ramiralvesmelo.event.service.MinioService;
import br.com.ramiralvesmelo.util.number.OrderNumberUtil;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class MinioServiceTest {

    @Autowired
    private MinioService minioService;

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.bucket}")
    private String bucket;

    @BeforeAll
    void beforeAll() throws Exception {
        boolean ready = waitForMinioReady(minioUrl, Duration.ofSeconds(30));
        assumeTrue(ready, () -> "MinIO não está pronto em " + minioUrl + " — docker compose up -d minio");

        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    @Test
    void upload() throws Exception {
        String name = OrderNumberUtil.generate();
        byte[] v1 = "%PDF-1.4\n%v1\n%%EOF".getBytes();

        try (InputStream in = new ByteArrayInputStream(v1)) {
            minioService.uploadPdf(name, in, v1.length);
        }
        assertTrue(minioService.exists(name));

        // limpeza
        minioService.deletePdf(name);
    }

    @Test
    void download() throws Exception {
        String name = OrderNumberUtil.generate();
        byte[] v1 = "%PDF-1.4\n%v1\n%%EOF".getBytes();

        try (InputStream in = new ByteArrayInputStream(v1)) {
            minioService.uploadPdf(name, in, v1.length);
        }

        try (InputStream got = minioService.downloadPdf(name)) {
            assertArrayEquals(v1, got.readAllBytes());
        }

        // limpeza
        minioService.deletePdf(name);
    }

    @Test
    void replace() throws Exception {
        String name = OrderNumberUtil.generate();
        byte[] v1 = "%PDF-1.4\n%v1\n%%EOF".getBytes();
        byte[] v2 = "%PDF-1.4\n%v2\n%%EOF".getBytes();

        // upload inicial
        try (InputStream in = new ByteArrayInputStream(v1)) {
            minioService.uploadPdf(name, in, v1.length);
        }
        // update/overwrite
        try (InputStream in = new ByteArrayInputStream(v2)) {
            minioService.updatePdf(name, in, v2.length);
        }
        // valida
        try (InputStream got = minioService.downloadPdf(name)) {
            assertArrayEquals(v2, got.readAllBytes());
        }

        // limpeza
        minioService.deletePdf(name);
    }

    @Test
    void delete() throws Exception {
        String name = OrderNumberUtil.generate();
        byte[] v1 = "%PDF-1.4\n%v1\n%%EOF".getBytes();

        try (InputStream in = new ByteArrayInputStream(v1)) {
            minioService.uploadPdf(name, in, v1.length);
        }
        assertTrue(minioService.exists(name));

        minioService.deletePdf(name);
        assertFalse(minioService.exists(name));
    }

    // ===== helpers =====
    private static boolean waitForMinioReady(String baseUrl, Duration timeout) {
        long deadline = System.nanoTime() + timeout.toNanos();
        String health = baseUrl.replaceAll("/+$", "") + "/minio/health/ready";
        while (System.nanoTime() < deadline) {
            if (checkHealth(health)) return true;
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }
        return false;
    }

    private static boolean checkHealth(String urlStr) {
        try {
            HttpURLConnection c = (HttpURLConnection) URI.create(urlStr).toURL().openConnection();
            c.setConnectTimeout(2000);
            c.setReadTimeout(2000);
            c.setRequestMethod("GET");
            int code = c.getResponseCode();
            c.disconnect();
            return code >= 200 && code < 300;
        } catch (Exception e) {
            return false;
        }
    }


}
