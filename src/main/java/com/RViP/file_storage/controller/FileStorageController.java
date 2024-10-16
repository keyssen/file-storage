package com.RViP.file_storage.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileStorageController {

    // Путь к папке resources
    private static final String RESOURCES_DIR = "src/main/resources/file/";

    @PostMapping("/save")
    public ResponseEntity<String> uploadCsvFile(@RequestParam("file") MultipartFile file, @RequestParam UUID requestId) {
        log.info("getAllReaders {}", requestId);
        System.out.println(file);
        if (file.isEmpty() || !isCsvFile(file)) {
            return ResponseEntity.badRequest().body("Invalid file. Please upload a CSV file.");
        }

        try {
            // Генерируем путь для сохранения файла в папку resources
            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            Path filePath = Paths.get(RESOURCES_DIR + filename);
            log.info("getAllReaders {}", filePath);
            Files.createDirectories(filePath.getParent());
            // Сохраняем файл
            Files.write(filePath, file.getBytes());

            return ResponseEntity.ok("File uploaded successfully: " + filename);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload file: " + e.getMessage());
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<byte[]> getReportById(@PathVariable String id) {
        log.info("/find/{id} {}",id);
        try {
            // Определяем путь к файлу в папке resources
            String filePath = "readers-" + id + ".csv";  // Например, файл с расширением .pdf



            // Получаем исходное имя файла
            Path path = Paths.get(RESOURCES_DIR + filePath);
            log.info("getAllReaders {}", path);
            String fileName = path.getFileName().toString();

            // Читаем содержимое файла
            byte[] fileContent = Files.readAllBytes(path);

            // Устанавливаем заголовки ответа для отправки файла
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, Files.probeContentType(path));

            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);

        } catch (IOException e) {
            // В случае ошибки вернем HTTP 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    // Проверяем, что файл имеет расширение .csv
    private boolean isCsvFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        return filename != null && filename.endsWith(".csv");
    }
}