package com.optimus.prdbackendreportes.application.services.impl;

import com.optimus.prdbackendreportes.application.services.interfaces.IRecojosProcesadosService;
import com.optimus.prdbackendreportes.domain.models.dto.request.InfoRequest;
import com.optimus.prdbackendreportes.domain.models.dto.response.FileInfoResponse;
import com.optimus.prdbackendreportes.domain.models.dto.response.InfoCabecera;
import com.optimus.prdbackendreportes.domain.models.dto.response.InfoDetalle;
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
    public FileInfoResponse getFileInfoCabecera(InfoRequest request) {
        String fileName = String.format("InformeRecojos_%s_%s%d.xlsx",
                request.account(), request.processDate(), request.processBatch());

        return new FileInfoResponse(fileName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", 0L);
    }

    @Override
    public FileInfoResponse getFileInfoDetalle(InfoRequest request) {
        String fileName = String.format("InformeRecojosDetalle_%s_%s%d.xlsx",
                request.account(), request.processDate(), request.processBatch());

        return new FileInfoResponse(fileName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", 0L);
    }

    @Override
    public byte[] generateInfoDetalleReport(InfoRequest request) {
        validateRequest(request);

        try {
            List<InfoDetalle> data = repository.getInfoDetalle(
                    request.account(), request.processDate(), request.processBatch());

            if (data.isEmpty()) {
                throw new NoDataFoundException("No se encontraron datos para los parámetros especificados");
            }

            return createExcelReportDetalle(data, request);

        } catch (NoDataFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error generating detalle report for request: {}", request, e);
            throw new ReportGenerationException("Error interno al generar el reporte de detalle", e);
        }
    }

    @Override
    public byte[] generateInfoCabeceraReport(InfoRequest request) {
        validateRequest(request);

        try {
            List<InfoCabecera> data = repository.getInfoCabecera(
                    request.account(), request.processDate(), request.processBatch());

            if (data.isEmpty()) {
                throw new NoDataFoundException("No se encontraron datos para los parámetros especificados");
            }

            return createExcelReportCabecera(data, request);

        } catch (NoDataFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error generating cabecera report for request: {}", request, e);
            throw new ReportGenerationException("Error interno al generar el reporte de cabecera", e);
        }
    }

    private void validateRequest(InfoRequest request) {
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

    // =============== MÉTODOS PARA INFORME DE CABECERA ===============

    private byte[] createExcelReportCabecera(List<InfoCabecera> data, InfoRequest request) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            XSSFSheet sheet = writeHeaderLineCabecera(workbook, request);
            writeDataLinesCabecera(workbook, sheet, data);

            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new ReportGenerationException("Error al crear el archivo Excel de cabecera", e);
        }
    }

    private XSSFSheet writeHeaderLineCabecera(XSSFWorkbook workbook, InfoRequest request) {
        String sheetName = String.format("InformeRecojos_%s_%s%d",
                request.account(), request.processDate(), request.processBatch());
        XSSFSheet sheet = workbook.createSheet(sheetName);
        Row row = sheet.createRow(0);

        CellStyle style = createHeaderStyle(workbook);

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

    private void writeDataLinesCabecera(XSSFWorkbook workbook, XSSFSheet sheet, List<InfoCabecera> data) {
        int rowCount = 1;
        CellStyle style = createDataStyle(workbook);

        for (InfoCabecera cabecera : data) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, cabecera.getAccount(), style);
            createCell(row, columnCount++, cabecera.getProcessDate(), style);
            createCell(row, columnCount++, cabecera.getProcessBatch(), style);
            createCell(row, columnCount++, cabecera.getConsultantArea(), style);
            createCell(row, columnCount++, cabecera.getIssueDate(), style);
            createCell(row, columnCount++, cabecera.getTicketNumber(), style);
            createCell(row, columnCount++, cabecera.getTicketSequence(), style);
            createCell(row, columnCount++, cabecera.getConsultantCode(), style);
            createCell(row, columnCount++, cabecera.getConsultantName(), style);
            createCell(row, columnCount++, cabecera.getTotalUnits(), style);
            createCell(row, columnCount++, cabecera.getRelatedOrderNumber(), style);
            createCell(row, columnCount++, cabecera.getRelatedOrderSequence(), style);
        }

        // Auto-ajustar columnas
        for (int i = 0; i < 12; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // =============== MÉTODOS PARA INFORME DE DETALLE ===============

    private byte[] createExcelReportDetalle(List<InfoDetalle> data, InfoRequest request) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            XSSFSheet sheet = writeHeaderLineDetalle(workbook, request);
            writeDataLinesDetalle(workbook, sheet, data);

            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new ReportGenerationException("Error al crear el archivo Excel de detalle", e);
        }
    }

    private XSSFSheet writeHeaderLineDetalle(XSSFWorkbook workbook, InfoRequest request) {
        String sheetName = String.format("InformeRecojosDetalle_%s_%s%d",
                request.account(), request.processDate(), request.processBatch());
        XSSFSheet sheet = workbook.createSheet(sheetName);
        Row row = sheet.createRow(0);

        CellStyle style = createHeaderStyle(workbook);

        String[] headers = {
                "Código de cuenta", "Fecha de Proceso", "Lote de Proceso", "Zona de Consultora",
                "Fecha de Emisión", "Número de Boleta", "Secuencia de Boleta", "Código de Consultora",
                "Nombre de Consultora", "Total de Unidades", "Código de Transacción Pedido Relacionado",
                "Número de Pedido Relacionado", "Secuencia de Pedido Relacionado", "Código de Producto",
                "Descripción de Producto", "Unidades a Recoger", "Campaña de Atención", "Tipo de Atención"
        };

        for (int i = 0; i < headers.length; i++) {
            createCell(row, i, headers[i], style);
        }

        return sheet;
    }

    private void writeDataLinesDetalle(XSSFWorkbook workbook, XSSFSheet sheet, List<InfoDetalle> data) {
        int rowCount = 1;
        CellStyle style = createDataStyle(workbook);

        for (InfoDetalle detalle : data) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

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
            createCell(row, columnCount++, detalle.getRelatedOrderTransactionCode(), style);
            createCell(row, columnCount++, detalle.getRelatedOrderNumber(), style);
            createCell(row, columnCount++, detalle.getRelatedOrderSequence(), style);
            createCell(row, columnCount++, detalle.getProductCode(), style);
            createCell(row, columnCount++, detalle.getProductDescription(), style);
            createCell(row, columnCount++, detalle.getUnitsToBePicked(), style);
            createCell(row, columnCount++, detalle.getAttentionCampaign(), style);
            createCell(row, columnCount++, detalle.getAttentionType(), style);
        }

        // Auto-ajustar columnas
        for (int i = 0; i < 18; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // =============== MÉTODOS AUXILIARES ===============

    private CellStyle createHeaderStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        return style;
    }

    private CellStyle createDataStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        return style;
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
            case java.time.LocalDate localDate -> cell.setCellValue(localDate.toString());
            case java.time.LocalDateTime localDateTime -> cell.setCellValue(localDateTime.toString());
            case java.sql.Date sqlDate -> cell.setCellValue(sqlDate.toString());
            case java.sql.Timestamp timestamp -> cell.setCellValue(timestamp.toString());
            default -> cell.setCellValue(value.toString());
        }
    }
}

