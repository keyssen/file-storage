package com.RViP.file_storage.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.RViP.file_storage.util.Constant.RESOURCES_DIR;
import static com.RViP.file_storage.util.MessageUtil.fileNmae;

@Slf4j
@Service
@RequiredArgsConstructor
public class Consumer {

    @KafkaListener(topics = "test_topic", containerFactory = "kafkaListenerContainerFactoryString")
    public void listenGroupTopic2(byte[] message) {
        final ObjectMapper objectMapper = new ObjectMapper();

        try {
            final KafkaFile eventSource = objectMapper.readValue(message, KafkaFile.class);

            log.info("getAllReaders {}", eventSource.id);

            try {
                // Генерируем путь для сохранения файла в папку resources
                String filename = StringUtils.cleanPath(String.format(fileNmae, eventSource.id));
                Path filePath = Paths.get(RESOURCES_DIR + filename);
                log.info("getAllReaders {}", filePath);
                Files.createDirectories(filePath.getParent());
                // Сохраняем файл
                Files.write(filePath, eventSource.data);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            log.error("Couldn't parse message: {}; exception: ", message, e);
        }
    }

}
