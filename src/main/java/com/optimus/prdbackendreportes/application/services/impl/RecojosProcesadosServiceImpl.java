package com.optimus.prdbackendreportes.application.services.impl;

import com.optimus.prdbackendreportes.application.services.interfaces.IRecojosProcesadosService;
import com.optimus.prdbackendreportes.domain.models.dto.request.InfoCabeceraRequest;
import com.optimus.prdbackendreportes.domain.models.dto.response.FileInfoResponse;
import com.optimus.prdbackendreportes.domain.models.dto.response.InfoCabecera;
import com.optimus.prdbackendreportes.domain.repositories.IRecojosProcesadosRepository;
import com.optimus.prdbackendreportes.utils.exceptions.NoDataFoundException;
import com.optimus.prdbackendreportes.utils.exceptions.ReportGenerationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class RecojosProcesadosServiceImpl implements IRecojosProcesadosService {

    private final IRecojosProcesadosRepository repository;

    @Override
    public FileInfoResponse getFileInfo(InfoCabeceraRequest request) {
        String fileName = String.format("InformeRecojos_%s_%s%d.xlsx",
                request.account(), request.processDate(), request.processBatch());

        return new FileInfoResponse(fileName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", 0L);
    }

    @Override
    public byte[] generateInfoCabeceraReport(InfoCabeceraRequest request) {
        validateRequest(request);

        try {
            List<InfoCabecera> data = repository.getInfoCabecera(
                    request.account(), request.processDate(), request.processBatch());

            if (data.isEmpty()) {
                throw new NoDataFoundException("No se encontraron datos para los parámetros especificados");
            }

            return createExcelReport(data, request);

        } catch (NoDataFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error generating report for request: {}", request, e);
            throw new ReportGenerationException("Error interno al generar el reporte", e);
        }
    }

    private void validateRequest(InfoCabeceraRequest request) {
        if (!StringUtils.hasText(request.account())) {
            throw new IllegalArgumentException("La cuenta es requerida");
        }
        if (!StringUtils.hasText(request.processDate())) {
            throw new IllegalArgumentException("La fecha de proceso es requerida");
        }
        if (request.processBatch() == null || request.processBatch() < 1) {
            throw new IllegalArgumentException("El lote de proceso debe ser mayor a 0");
        }
    }

    private byte[] createExcelReport(List<InfoCabecera> data, InfoCabeceraRequest request) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            XSSFSheet sheet = writeHeaderLine(workbook, request);
            writeDataLines(workbook, sheet, data);

            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new ReportGenerationException("Error al crear el archivo Excel", e);
        }
    }

    private XSSFSheet writeHeaderLine(XSSFWorkbook workbook, InfoCabeceraRequest request) {
        String sheetName = String.format("InformeRecojos_%s_%s%d",
                request.account(), request.processDate(), request.processBatch());
        XSSFSheet sheet = workbook.createSheet(sheetName);
        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        String[] headers = {
                "Código de cuenta", "Fecha de Proceso", "Lote de Proceso", "Zona de Consultora",
                "Fecha de Emisión", "Número de Boleta", "Secuencia de Boleta", "Código de Consultora",
                "Nombre de Consultora", "Total de Unidades", "Número de Pedido Relacionado",
                "Secuencia de Pedido Relacionado"
        };

        for (int i = 0; i < headers.length; i++) {
            createCell(row, i, headers[i], style);
        }

        return sheet;
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        Cell cell = row.createCell(columnCount);
        cell.setCellStyle(style);

        if (value == null) {
            cell.setCellValue("");
            return;
        }
        switch (value) {
            case Integer i -> cell.setCellValue(i);
            case String s -> cell.setCellValue(s);
            case Double v -> cell.setCellValue(v);
            case Boolean b -> cell.setCellValue(b);
            default -> cell.setCellValue(value.toString());
        }
    }

    private void writeDataLines(XSSFWorkbook workbook, XSSFSheet sheet, List<InfoCabecera> data) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (InfoCabecera detalle : data) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            System.out.println("Issue Date: " + detalle.getIssueDate());

            createCell(row, columnCount++, detalle.getAccount(), style);
            createCell(row, columnCount++, detalle.getProcessDate(), style);
            createCell(row, columnCount++, detalle.getProcessBatch(), style);
            createCell(row, columnCount++, detalle.getConsultantArea(), style);
            createCell(row, columnCount++, detalle.getIssueDate(), style);
            createCell(row, columnCount++, detalle.getTicketNumber(), style);
            createCell(row, columnCount++, detalle.getTicketSequence(), style);
            createCell(row, columnCount++, detalle.getConsultantCode(), style);
            createCell(row, columnCount++, detalle.getConsultantName(), style);
            createCell(row, columnCount++, detalle.getTotalUnits(), style);
            createCell(row, columnCount++, detalle.getRelatedOrderNumber(), style);
            createCell(row, columnCount++, detalle.getRelatedOrderSequence(), style);
        }

        // Auto-ajustar columnas
        for (int i = 0; i < 12; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}

