package com.optimus.prdbackendreportes.infrastructure.controllers;

import com.optimus.prdbackendreportes.application.services.interfaces.IDeliveryReceiptService;
import com.optimus.prdbackendreportes.domain.models.dto.request.DeliveryReceiptRequest;
import com.optimus.prdbackendreportes.domain.models.dto.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para los reportes de constancia de entrega
 */
@RestController
@RequestMapping("/constancia-entrega")
@RequiredArgsConstructor
public class DeliveryReceiptController {

    private final IDeliveryReceiptService service;

    /**
     * Endpoint para validar que existen datos para generar el reporte
     *
     * @param request Datos de solicitud
     * @return Respuesta de validación
     */
    @PostMapping("/validar")
    public ResponseEntity<Response<Void>> validateReportData(@RequestBody DeliveryReceiptRequest request) {
        service.validateReportData(request);

        Response<Void> response = new Response<>(
                true,
                "Validación realizada correctamente",
                null
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para generar el reporte PDF de constancia de entrega
     *
     * @param request Datos de solicitud
     * @return PDF del reporte
     */
    @PostMapping
    public ResponseEntity<byte[]> generateReport(@RequestBody DeliveryReceiptRequest request) {
        byte[] pdfBytes = service.generateDeliveryReceiptReport(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "constancia-entrega.pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}