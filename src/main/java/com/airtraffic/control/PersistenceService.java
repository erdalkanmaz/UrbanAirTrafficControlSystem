package com.airtraffic.control;

import com.airtraffic.model.SystemState;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Sistem durumunu kaydetme ve yükleme servisi
 * JSON formatında serialization/deserialization yapar
 */
public class PersistenceService {
    private final Gson gson;

    public PersistenceService() {
        // Custom adapter for LocalDateTime (Java 17 module system compatibility)
        JsonSerializer<LocalDateTime> localDateTimeSerializer = (src, typeOfSrc, context) -> {
            if (src == null) {
                return null;
            }
            return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        };
        
        JsonDeserializer<LocalDateTime> localDateTimeDeserializer = (json, typeOfT, context) -> {
            if (json == null || json.isJsonNull()) {
                return null;
            }
            return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        };
        
        // Gson builder with pretty printing and LocalDateTime adapter
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, localDateTimeSerializer)
                .registerTypeAdapter(LocalDateTime.class, localDateTimeDeserializer)
                .create();
    }

    /**
     * Sistem durumunu JSON dosyasına kaydeder
     * @param systemState Kaydedilecek sistem durumu
     * @param filePath Dosya yolu
     * @throws IOException Dosya yazma hatası
     * @throws IllegalArgumentException systemState veya filePath null/empty ise
     */
    public void saveState(SystemState systemState, String filePath) throws IOException {
        if (systemState == null) {
            throw new IllegalArgumentException("SystemState cannot be null");
        }
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        // Create parent directories if they don't exist
        java.io.File file = new java.io.File(filePath);
        java.io.File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(systemState, writer);
        }
    }

    /**
     * Sistem durumunu JSON dosyasından yükler
     * @param filePath Dosya yolu
     * @return Yüklenen sistem durumu
     * @throws IOException Dosya okuma hatası
     * @throws IllegalArgumentException filePath null/empty ise
     * @throws com.google.gson.JsonSyntaxException JSON format hatası
     */
    public SystemState loadState(String filePath) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        if (!Files.exists(Paths.get(filePath))) {
            throw new IOException("File not found: " + filePath);
        }

        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, SystemState.class);
        }
    }
}

