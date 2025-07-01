package com.optimus.prdbackendreportes.infrastructure.adapter.input.rest.controller;

import com.optimus.prdbackendreportes.application.port.input.ProcessedPickupsUseCase;
import com.optimus.prdbackendreportes.domain.model.entity.DetailInfo;
import com.optimus.prdbackendreportes.domain.model.entity.HeaderInfo;
import com.optimus.prdbackendreportes.domain.model.valueobject.Account;
import com.optimus.prdbackendreportes.domain.model.valueobject.ProcessBatch;
import com.optimus.prdbackendreportes.domain.model.valueobject.ProcessDate;
import com.optimus.prdbackendreportes.infrastructure.adapter.input.rest.dto.request.InfoRequest;
import com.optimus.prdbackendreportes.infrastructure.adapter.input.rest.dto.response.FileInfoResponse;
import com.optimus.prdbackendreportes.infrastructure.adapter.output.report.ExcelReportGenerator;
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

    private final ProcessedPickupsUseCase recojosProcesadosUseCase;
    private final ExcelReportGenerator excelGenerator;

    @GetMapping("/export-info-cabecera")
    public ResponseEntity<byte[]> exportInfoCabecera(
            @Valid @ModelAttribute InfoRequest request) {

        log.info("Generating cabecera report for account: {}, processDate: {}, processBatch: {}",
                request.account(), request.processDate(), request.processBatch());

        List<HeaderInfo> data = recojosProcesadosUseCase.generateHeaderInfoData(
                Account.of(request.account()),
                ProcessDate.of(request.processDate()),
                ProcessBatch.of(request.processBatch())
        );

        byte[] reportData = excelGenerator.generateHeaderInfoReport(data, request);
        FileInfoResponse fileInfo = createHeaderFileInfo(request);

        HttpHeaders headers = createExcelHeaders(fileInfo);

        log.info("Cabecera report generated successfully. File: {}, Size: {} bytes",
                fileInfo.fileName(), reportData.length);

        return ResponseEntity.ok().headers(headers).body(reportData);
    }

    @GetMapping("/export-info-detalle")
    public ResponseEntity<byte[]> exportInfoDetalle(
            @Valid @ModelAttribute InfoRequest request) {

        log.info("Generating detalle report for account: {}, processDate: {}, processBatch: {}",
                request.account(), request.processDate(), request.processBatch());

        List<DetailInfo> data = recojosProcesadosUseCase.generateDetailInfoData(
                Account.of(request.account()),
                ProcessDate.of(request.processDate()),
                ProcessBatch.of(request.processBatch())
        );

        byte[] reportData = excelGenerator.generateDetailInfoReport(data, request);
        FileInfoResponse fileInfo = createDetailFileInfo(request);

        HttpHeaders headers = createExcelHeaders(fileInfo);

        log.info("Detalle report generated successfully. File: {}, Size: {} bytes",
                fileInfo.fileName(), reportData.length);

        return ResponseEntity.ok().headers(headers).body(reportData);
    }

    private FileInfoResponse createHeaderFileInfo(InfoRequest request) {
        String fileName = String.format("InformeRecojos_%s_%s%d.xlsx",
                request.account(), request.processDate(), request.processBatch());
        return new FileInfoResponse(fileName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", 0L);
    }

    private FileInfoResponse createDetailFileInfo(InfoRequest request) {
        String fileName = String.format("InformeRecojosDetalle_%s_%s%d.xlsx",
                request.account(), request.processDate(), request.processBatch());
        return new FileInfoResponse(fileName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", 0L);
    }

    private HttpHeaders createExcelHeaders(FileInfoResponse fileInfo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileInfo.fileName());
        return headers;
    }
}
