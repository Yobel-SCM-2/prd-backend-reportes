package com.optimus.prdbackendreportes.infrastructure.adapter.output.report;

import com.optimus.prdbackendreportes.domain.model.enums.ReportFormat;
import com.optimus.prdbackendreportes.domain.port.output.ReportGenerator;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This Factory class is responsible for managing different report generators
 */
@Component
@Log4j2
public class ReportGeneratorFactory {

    private final Map<ReportFormat, ReportGenerator> generators;

    public ReportGeneratorFactory(List<ReportGenerator> reportGenerators) {
        this.generators = reportGenerators.stream()
                .collect(Collectors.toMap(
                        ReportGenerator::getSupportedFormat,
                        Function.identity()
                ));

        log.info("Initialized ReportGeneratorFactory with {} generators: {}",
                generators.size(), generators.keySet());
    }

    public ReportGenerator getGenerator(ReportFormat format) {
        ReportGenerator generator = generators.get(format);

        if (generator == null) {
            throw new UnsupportedOperationException(
                    "No generator available for format: " + format +
                            ". Available formats: " + generators.keySet()
            );
        }

        log.debug("Retrieved generator for format: {}", format);
        return generator;
    }

    /**
     * Check if a format is supported
     */
    public boolean isFormatSupported(ReportFormat format) {
        return generators.containsKey(format);
    }

    /**
     * Get all supported formats
     */
    public List<ReportFormat> getSupportedFormats() {
        return List.copyOf(generators.keySet());
    }
}
