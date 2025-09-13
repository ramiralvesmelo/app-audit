package br.com.ramiralvesmelo.audit.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.minio.MinioClient;

@Testcontainers
@SpringBootTest
class MinioServiceTest {

    // Sobe um MinIO real em Docker
    // Use uma tag recente do MinIO
    static final GenericContainer<?> MINIO =
        new GenericContainer<>("minio/minio:RELEASE.2025-01-20T14-21-49Z") // ajuste se necessário
            .withEnv("MINIO_ROOT_USER", "minioadmin")
            .withEnv("MINIO_ROOT_PASSWORD", "minioadmin")
            .withCommand("server", "/data") // modo single
            .withExposedPorts(9000)
            .waitingFor(Wait.forHttp("/minio/health/ready").forPort(9000));

    @BeforeAll
    static void beforeAll() {
        MINIO.start();
    }

    @AfterAll
    static void afterAll() {
        MINIO.stop();
    }

    // Injeta dinamicamente as properties que sua MinioService usa
    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        String endpoint = "http://" + MINIO.getHost() + ":" + MINIO.getMappedPort(9000);
        registry.add("minio.url", () -> endpoint);
        registry.add("minio.access-key", () -> "minioadmin");
        registry.add("minio.secret-key", () -> "minioadmin");
        registry.add("minio.bucket", () -> "test-bucket");
    }

    // Define um MinioClient específico para o teste (usando as props acima)
    @TestConfiguration
    static class MinioTestConfig {
        @Bean
        MinioClient minioClient(
            @Value("${minio.url}") String url,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey
        ) {
            return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
        }
    }

    @Autowired
    private MinioService minioService;

    @Test
    void shouldUploadAndDownloadPdf() throws Exception {
        // PDF mínimo (Qualquer byte array serve; MinIO não valida conteúdo)
        byte[] pdfBytes = "%PDF-1.4\n1 0 obj\n<<>>\nendobj\ntrailer\n<<>>\n%%EOF"
                .getBytes(StandardCharsets.UTF_8);
        String fileName = "teste.pdf";

        try (ByteArrayInputStream in = new ByteArrayInputStream(pdfBytes)) {
            minioService.uploadPdf(fileName, in, pdfBytes.length);
        }

        try (InputStream downloaded = minioService.downloadPdf(fileName)) {
            assertNotNull(downloaded, "InputStream do download não pode ser nulo");
            byte[] got = downloaded.readAllBytes();
            assertArrayEquals(pdfBytes, got, "O conteúdo baixado deve ser igual ao enviado");
        }
    }
}
