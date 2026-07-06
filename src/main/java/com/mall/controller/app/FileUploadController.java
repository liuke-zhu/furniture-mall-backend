package com.mall.controller.app;

import com.mall.common.api.Result;
import com.mall.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");
    private static final Map<String, byte[]> MAGIC_BYTES = Map.of(
            "image/jpeg", new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF},
            "image/png", new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47},
            "image/gif", "GIF8".getBytes(),
            "image/webp", "RIFF".getBytes()
    );
    private static final long MAX_SIZE = 5 * 1024 * 1024;

    @Value("${mall.upload.path}")
    private String uploadPath;

    @Value("${mall.upload.url-prefix}")
    private String urlPrefix;

    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException("文件大小不能超过 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BusinessException("仅支持 jpg/png/gif/webp 格式");
        }
        String ext = resolveSafeExtension(file.getOriginalFilename(), contentType);
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new BusinessException("不支持的文件扩展名");
        }
        try {
            validateMagicBytes(file, contentType);
            Path dir = Paths.get(uploadPath).toAbsolutePath();
            Files.createDirectories(dir);
            String filename = UUID.randomUUID().toString().replace("-", "") + ext;
            Path target = dir.resolve(filename);
            file.transferTo(target.toFile());
            return Result.success("上传成功", urlPrefix + "/" + filename);
        } catch (IOException e) {
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }
    }

    private String resolveSafeExtension(String originalFilename, String contentType) {
        String ext = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> "";
        };
        if (originalFilename != null && originalFilename.contains(".")) {
            String originalExt = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase(Locale.ROOT);
            if (ALLOWED_EXTENSIONS.contains(originalExt)) {
                ext = originalExt;
            }
        }
        return ext;
    }

    private void validateMagicBytes(MultipartFile file, String contentType) throws IOException {
        byte[] expected = MAGIC_BYTES.get(contentType);
        if (expected == null) {
            throw new BusinessException("不支持的文件类型");
        }
        byte[] header = new byte[expected.length];
        try (InputStream inputStream = file.getInputStream()) {
            int read = inputStream.read(header);
            if (read < expected.length) {
                throw new BusinessException("文件内容无效");
            }
        }
        for (int i = 0; i < expected.length; i++) {
            if (header[i] != expected[i]) {
                throw new BusinessException("文件内容与类型不匹配");
            }
        }
    }
}
