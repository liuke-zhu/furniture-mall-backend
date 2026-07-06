package com.mall.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(0)
public class DemoImageInitializer implements CommandLineRunner {

    private static final String[] IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};

    @Value("${mall.upload.path}")
    private String uploadPath;

    @Override
    public void run(String... args) throws IOException {
        Path demoDir = Paths.get("demo-images").toAbsolutePath().normalize();
        Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        if (!Files.isDirectory(demoDir)) {
            return;
        }
        if (Files.isDirectory(uploadDir) && hasImageFiles(uploadDir)) {
            return;
        }
        Files.createDirectories(uploadDir);
        int copied = 0;
        try (Stream<Path> files = Files.list(demoDir)) {
            for (Path source : (Iterable<Path>) files.filter(this::isImageFile)::iterator) {
                Path target = uploadDir.resolve(source.getFileName());
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                copied++;
            }
        }
        if (copied > 0) {
            log.info("DemoImageInitializer: copied {} demo image(s) to {}", copied, uploadDir);
        }
    }

    private boolean hasImageFiles(Path dir) throws IOException {
        try (Stream<Path> files = Files.list(dir)) {
            return files.anyMatch(this::isImageFile);
        }
    }

    private boolean isImageFile(Path path) {
        if (!Files.isRegularFile(path)) {
            return false;
        }
        String name = path.getFileName().toString().toLowerCase();
        for (String ext : IMAGE_EXTENSIONS) {
            if (name.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
}
