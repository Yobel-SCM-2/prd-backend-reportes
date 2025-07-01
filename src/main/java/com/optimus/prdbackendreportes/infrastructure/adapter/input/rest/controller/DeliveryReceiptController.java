package com.optimus.prdbackendreportes.infrastructure.adapter.input.rest.controller;

import com.optimus.prdbackendreportes.application.port.input.DeliveryReceiptUseCase;
import com.optimus.prdbackendreportes.domain.model.entity.DeliveryReceiptItem;
import com.optimus.prdbackendreportes.domain.model.valueobject.Account;
import com.optimus.prdbackendreportes.domain.model.valueobject.OrderNumber;
import com.optimus.prdbackendreportes.domain.model.valueobject.ProcessBatch;
import com.optimus.prdbackendreportes.domain.model.valueobject.ProcessDate;
import com.optimus.prdbackendreportes.infrastructure.adapter.input.rest.dto.request.DeliveryReceiptRequest;
import com.optimus.prdbackendreportes.infrastructure.adapter.input.rest.dto.response.ApiResponse;
import com.optimus.prdbackendreportes.infrastructure.adapter.output.report.PdfReportGenerator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/constancia-entrega")
@Log4j2
@RequiredArgsConstructor
@CrossOrigin
public class DeliveryReceiptController {

    private final DeliveryReceiptUseCase deliveryReceiptUseCase;
    private final PdfReportGenerator pdfGenerator;

    @PostMapping("/validar")
    public ResponseEntity<ApiResponse<Void>> validateReportData(@Valid @RequestBody DeliveryReceiptRequest request) {

        log.info("Validating delivery receipt data for account: {}", request.account());

        deliveryReceiptUseCase.validateReportData(
                Account.of(request.account()),
                ProcessDate.of(request.processDate()),
                ProcessBatch.of(request.processBatch()),
                OrderNumber.of(request.orderNumber()),
                request.schema()
        );

        return ResponseEntity.ok(ApiResponse.success(null, "Validation successful"));
    }

    @PostMapping
    public ResponseEntity<byte[]> generateReport(@Valid @RequestBody DeliveryReceiptRequest request) {

        log.info("Generating delivery receipt report for account: {}", request.account());

        List<DeliveryReceiptItem> data = deliveryReceiptUseCase.generateReportData(
                Account.of(request.account()),
                ProcessDate.of(request.processDate()),
                ProcessBatch.of(request.processBatch()),
                OrderNumber.of(request.orderNumber()),
                request.schema()
        );

        byte[] pdfBytes = pdfGenerator.generateDeliveryReceiptReport(data, request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "constancia-entrega.pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        log.info("Delivery receipt report generated successfully, size: {} bytes", pdfBytes.length);
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
