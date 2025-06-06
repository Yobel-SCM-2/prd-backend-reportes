package com.optimus.prdbackendreportes.infrastructure.controllers;

import com.optimus.prdbackendreportes.application.services.interfaces.IRecojosProcesadosService;
import com.optimus.prdbackendreportes.domain.models.dto.request.InfoCabeceraRequest;
import com.optimus.prdbackendreportes.domain.models.dto.response.InfoCabecera;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jdk.jfr.ContentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/recojos-procesados")
@RequiredArgsConstructor
@Log4j2
public class RecojosProcesadosController {

    private final IRecojosProcesadosService recojosProcesadosService;

    @GetMapping("/export-info-cabecera")
    public void exportInfoCabecera(
            HttpServletResponse response,
            @RequestBody @Valid InfoCabeceraRequest request
    ) throws Exception {
        response.setContentType("application/octet-stream");

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=InformeRecojos_" + request.account() + "_" + request.processDate() + request.processBatch() + ".xlsx";
        response.setHeader(headerKey, headerValue);

        recojosProcesadosService.generateInfoCabeceraReport(response, request);
    }

}
