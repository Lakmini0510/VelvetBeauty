package com.salon.VelvetBeauty.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Thread-safe file persistence manager for reading and writing text-based database records.
 * Stores files in the configured salon data directory (e.g. ./data/).
 */
@Component
public class FileStorageManager {

    private final String dataDirectoryPath;

    public FileStorageManager(@Value("${salon.data.dir:data}") String dataDir) {
        this.dataDirectoryPath = dataDir;
        ensureDirectoryExists();
    }

    private void ensureDirectoryExists() {
        try {
            Path path = Paths.get(dataDirectoryPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            System.err.println("Failed to create data directory: " + e.getMessage());
        }
    }

    public synchronized Path getFilePath(String fileName) {
        ensureDirectoryExists();
        Path file = Paths.get(dataDirectoryPath, fileName);
        if (!Files.exists(file)) {
            try {
                Files.createFile(file);
            } catch (IOException e) {
                System.err.println("Failed to create file: " + fileName + " - " + e.getMessage());
            }
        }
        return file;
    }

    public synchronized List<String> readAllLines(String fileName) {
        List<String> lines = new ArrayList<>();
        Path filePath = getFilePath(fileName);
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file " + fileName + ": " + e.getMessage());
        }
        return lines;
    }

    public synchronized void writeAllLines(String fileName, List<String> lines) {
        Path filePath = getFilePath(fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to file " + fileName + ": " + e.getMessage());
        }
    }

    public synchronized void appendLine(String fileName, String line) {
        Path filePath = getFilePath(fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8, 
                java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND)) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error appending to file " + fileName + ": " + e.getMessage());
        }
    }
}
