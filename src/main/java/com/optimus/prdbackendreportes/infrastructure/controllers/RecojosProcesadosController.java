package com.optimus.prdbackendreportes.infrastructure.controllers;

import com.optimus.prdbackendreportes.application.services.interfaces.IRecojosProcesadosService;
import com.optimus.prdbackendreportes.domain.models.dto.request.InfoCabeceraRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/recojos-procesados")
@RequiredArgsConstructor
@Log4j2
public class RecojosProcesadosController {

    private final IRecojosProcesadosService recojosProcesadosService;

    @GetMapping("/export-info-cabecera")
    public void exportInfoCabecera(
            HttpServletResponse response,
            @RequestParam String account,
            @RequestParam String processDate,
            @RequestParam Integer processBatch
    ) throws Exception {
        response.setContentType("application/octet-stream");

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=InformeRecojos_" + account + "_" + processDate + processBatch + ".xlsx";
        response.setHeader(headerKey, headerValue);

        InfoCabeceraRequest request = new InfoCabeceraRequest(account, processDate, processBatch);

        recojosProcesadosService.generateInfoCabeceraReport(response, request);
    }

}
