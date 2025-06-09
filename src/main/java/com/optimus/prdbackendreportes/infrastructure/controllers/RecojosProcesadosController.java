package com.optimus.prdbackendreportes.infrastructure.controllers;

import com.optimus.prdbackendreportes.application.services.interfaces.IRecojosProcesadosService;
import com.optimus.prdbackendreportes.domain.models.dto.request.InfoRequest;
import com.optimus.prdbackendreportes.domain.models.dto.response.FileInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/recojos-procesados")
@RequiredArgsConstructor
@Log4j2
@CrossOrigin
public class RecojosProcesadosController {

    private final IRecojosProcesadosService recojosProcesadosService;

    @GetMapping("/export-info-cabecera")
    public ResponseEntity<byte[]> exportInfoCabecera(
            @RequestParam String account,
            @RequestParam String processDate,
            @RequestParam Integer processBatch
    ) {
        log.info("Generating report for account: {}, processDate: {}, processBatch: {}",
                account, processDate, processBatch);

        InfoRequest request = new InfoRequest(account, processDate, processBatch);

        byte[] reportData = recojosProcesadosService.generateInfoCabeceraReport(request);
        FileInfoResponse fileInfo = recojosProcesadosService.getFileInfoCabecera(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileInfo.fileName());
        headers.setContentLength(reportData.length);

        log.info("Report generated successfully. File: {}, Size: {} bytes",
                fileInfo.fileName(), reportData.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(reportData);
    }

    @GetMapping("/export-info-detalle")
    public ResponseEntity<byte[]> exportInfoDetalle(
            @RequestParam String account,
            @RequestParam String processDate,
            @RequestParam Integer processBatch
    ) {
        log.info("Generating detail report for account: {}, processDate: {}, processBatch: {}",
                account, processDate, processBatch);

        InfoRequest request = new InfoRequest(account, processDate, processBatch);

        byte[] reportData = recojosProcesadosService.generateInfoDetalleReport(request);
        FileInfoResponse fileInfo = recojosProcesadosService.getFileInfoDetalle(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileInfo.fileName());
        headers.setContentLength(reportData.length);

        log.info("Detail report generated successfully. File: {}, Size: {} bytes",
                fileInfo.fileName(), reportData.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(reportData);
    }
}
