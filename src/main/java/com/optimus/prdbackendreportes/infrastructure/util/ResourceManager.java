package com.optimus.prdbackendreportes.infrastructure.util;

import com.optimus.prdbackendreportes.domain.exception.ReportGenerationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Log4j2
public class ResourceManager {

    private final Map<String, byte[]> resourceCache = new ConcurrentHashMap<>();

    public InputStream getResourceAsStream(String resourcePath) throws ReportGenerationException {
        byte[] resourceBytes = resourceCache.computeIfAbsent(resourcePath, this::loadResourceBytes);
        return new ByteArrayInputStream(resourceBytes);
    }

    public InputStream getResourceAsStreamNoCache(String resourcePath) throws ReportGenerationException {
        InputStream stream = getClass().getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new ReportGenerationException("Resource not found: " + resourcePath);
        }
        return stream;
    }

    private byte[] loadResourceBytes(String resourcePath) {
        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new ReportGenerationException("Resource not found: " + resourcePath);
            }
            return inputStream.readAllBytes();
        } catch (IOException e) {
            log.error("Error loading the resource: {}", resourcePath, e);
            throw new ReportGenerationException("Error loading the resource: " + resourcePath, e);
        }
    }

    public void clearCache() {
        resourceCache.clear();
        log.info("Resource cache cleared, {} items removed", resourceCache.size());
    }

    public int getCacheSize() {
        return resourceCache.size();
    }
}
