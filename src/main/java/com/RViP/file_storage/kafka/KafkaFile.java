package com.RViP.file_storage.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class KafkaFile {
    UUID id;
    byte[] data;
}
