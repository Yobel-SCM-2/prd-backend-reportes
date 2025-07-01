package com.optimus.prdbackendreportes.infrastructure.adapter.input.rest.controller;

import com.optimus.prdbackendreportes.application.port.input.ProcessedPickupsUseCase;
import com.optimus.prdbackendreportes.domain.model.entity.DetailInfo;
import com.optimus.prdbackendreportes.domain.model.entity.HeaderInfo;
import com.optimus.prdbackendreportes.domain.model.enums.ReportFormat;
import com.optimus.prdbackendreportes.domain.model.valueobject.Account;
import com.optimus.prdbackendreportes.domain.model.valueobject.ProcessBatch;
import com.optimus.prdbackendreportes.domain.model.valueobject.ProcessDate;
import com.optimus.prdbackendreportes.domain.port.output.ReportGenerator;
import com.optimus.prdbackendreportes.infrastructure.adapter.input.rest.dto.request.InfoRequest;
import com.optimus.prdbackendreportes.infrastructure.adapter.input.rest.dto.response.FileInfoResponse;
import com.optimus.prdbackendreportes.infrastructure.adapter.output.report.ReportGeneratorFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recojos-procesados")
@Log4j2
@RequiredArgsConstructor
@CrossOrigin
public class ProcessedPickupsController {

    private final ProcessedPickupsUseCase processedPickupsUseCase;
    private final ReportGeneratorFactory reportGeneratorFactory;

    @GetMapping("/export-info-cabecera")
    public ResponseEntity<byte[]> exportInfoCabecera(
            @Valid @ModelAttribute InfoRequest request) {

        log.info("Generating cabecera report for account: {}, processDate: {}, processBatch: {}",
                request.account(), request.processDate(), request.processBatch());

        List<HeaderInfo> data = processedPickupsUseCase.generateHeaderInfoData(
                Account.of(request.account()),
                ProcessDate.of(request.processDate()),
                ProcessBatch.of(request.processBatch())
        );

        ReportGenerator generator = reportGeneratorFactory.getGenerator(ReportFormat.EXCEL);

        byte[] reportData = generator.generateHeaderInfoReport(data, request, ReportFormat.EXCEL);

        FileInfoResponse fileInfo = createHeaderFileInfo(request);
        HttpHeaders headers = createExcelHeaders(fileInfo);

        log.info("Header report generated successfully. File: {}, Size: {} bytes",
                fileInfo.fileName(), reportData.length);

        return ResponseEntity.ok().headers(headers).body(reportData);
    }

    @GetMapping("/export-info-detalle")
    public ResponseEntity<byte[]> exportInfoDetalle(
            @Valid @ModelAttribute InfoRequest request) {

        log.info("Generating detalle report for account: {}, processDate: {}, processBatch: {}",
                request.account(), request.processDate(), request.processBatch());

        List<DetailInfo> data = processedPickupsUseCase.generateDetailInfoData(
                Account.of(request.account()),
                ProcessDate.of(request.processDate()),
                ProcessBatch.of(request.processBatch())
        );

        ReportGenerator generator = reportGeneratorFactory.getGenerator(ReportFormat.EXCEL);

        byte[] reportData = generator.generateDetailInfoReport(data, request, ReportFormat.EXCEL);

        FileInfoResponse fileInfo = createDetailFileInfo(request);
        HttpHeaders headers = createExcelHeaders(fileInfo);

        log.info("Detail report generated successfully. File: {}, Size: {} bytes",
                fileInfo.fileName(), reportData.length);

        return ResponseEntity.ok().headers(headers).body(reportData);
    }

    private FileInfoResponse createHeaderFileInfo(InfoRequest request) {
        String fileName = String.format("InformeRecojos_%s_%s%d%s",
                request.account(), request.processDate(), request.processBatch(), ReportFormat.EXCEL.getFileExtension());
        return new FileInfoResponse(fileName, ReportFormat.EXCEL.getMimeType(), 0L);
    }

    private FileInfoResponse createDetailFileInfo(InfoRequest request) {
        String fileName = String.format("InformeRecojosDetalle_%s_%s%d%s",
                request.account(), request.processDate(), request.processBatch(), ReportFormat.EXCEL.getFileExtension());
        return new FileInfoResponse(fileName, ReportFormat.EXCEL.getMimeType(), 0L);
    }

    private HttpHeaders createExcelHeaders(FileInfoResponse fileInfo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(fileInfo.contentType()));
        headers.setContentDispositionFormData("attachment", fileInfo.fileName());
        return headers;
    }
}
