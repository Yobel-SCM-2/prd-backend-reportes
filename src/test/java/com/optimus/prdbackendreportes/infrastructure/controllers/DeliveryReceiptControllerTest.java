package com.optimus.prdbackendreportes.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimus.prdbackendreportes.application.services.interfaces.IDeliveryReceiptService;
import com.optimus.prdbackendreportes.domain.models.dto.request.DeliveryReceiptRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(DeliveryReceiptController.class)
class DeliveryReceiptControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private IDeliveryReceiptService service;

    @Nested
    @DisplayName("Pruebas de casos exitosos para generar reporte de constancia de entrega")
    public class SuccessfullyDeliveryReceiptReport {

        @Test
        @DisplayName("Prueba validar que haya una carga para la cuenta, fecha de proceso, lote de proceso, número de orden y schema ingresado")
        public void testValidateContentByCargoNumber() throws Exception {
            DeliveryReceiptRequest request = new DeliveryReceiptRequest("ELX", LocalDate.parse("2025-03-26"), 4, "", "DYT_DEV");

            mockMvc.perform(post("/constancia-entrega/validar").contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Validación realizada correctamente"));

            verify(service).validateReportData(request);
        }

        @Test
        @DisplayName("Prueba generar reporte PDF de constancia de entrega")
        public void testGenerateReport() throws Exception {
            DeliveryReceiptRequest request = new DeliveryReceiptRequest("ELX", LocalDate.parse("2025-03-26"), 4, "", "DYT_DEV");

            mockMvc.perform(post("/constancia-entrega").contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_PDF));

            verify(service).generateDeliveryReceiptReport(request);
        }
    }
}