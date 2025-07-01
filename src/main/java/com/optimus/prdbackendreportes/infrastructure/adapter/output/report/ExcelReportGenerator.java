package com.optimus.prdbackendreportes.infrastructure.adapter.output.report;

import com.optimus.prdbackendreportes.domain.exception.ReportGenerationException;
import com.optimus.prdbackendreportes.domain.model.entity.DetailInfo;
import com.optimus.prdbackendreportes.domain.model.entity.HeaderInfo;
import com.optimus.prdbackendreportes.infrastructure.adapter.input.rest.dto.request.InfoRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Log4j2
public class ExcelReportGenerator {

    private static final String[] CABECERA_HEADERS = {
            "Código de cuenta", "Fecha de Proceso", "Lote de Proceso", "Zona de Consultora",
            "Fecha de Emisión", "Número de Boleta", "Secuencia de Boleta", "Código de Consultora",
            "Nombre de Consultora", "Total de Unidades", "Número de Pedido Relacionado",
            "Secuencia de Pedido Relacionado"
    };

    private static final String[] DETALLE_HEADERS = {
            "Código de cuenta", "Fecha de Proceso", "Lote de Proceso", "Zona de Consultora",
            "Fecha de Emisión", "Número de Boleta", "Secuencia de Boleta", "Código de Consultora",
            "Nombre de Consultora", "Total de Unidades", "Código de Transacción Pedido Relacionado",
            "Número de Pedido Relacionado", "Secuencia de Pedido Relacionado", "Código de Producto",
            "Descripción de Producto", "Unidades a Recoger", "Campaña de Atención", "Tipo de Atención"
    };

    public byte[] generateHeaderInfoReport(List<HeaderInfo> data, InfoRequest request) {
        log.info("Generating Excel cabecera report for {} items", data.size());

        validateInputData(data, request, "cabecera");

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            XSSFSheet sheet = createCabeceraSheet(workbook, request);
            writeHeaderData(workbook, sheet, data);
            autoSizeColumns(sheet, CABECERA_HEADERS.length);

            workbook.write(outputStream);
            byte[] bytes = outputStream.toByteArray();

            log.info("Excel cabecera report generated successfully, size: {} bytes", bytes.length);
            return bytes;

        } catch (IOException e) {
            log.error("Error generating Excel cabecera report", e);
            throw new ReportGenerationException("Error al crear archivo Excel de cabecera", e);
        }
    }

    public byte[] generateDetailInfoReport(List<DetailInfo> data, InfoRequest request) {
        log.info("Generating Excel detalle report for {} items", data.size());

        validateInputData(data, request, "detalle");

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            XSSFSheet sheet = createDetailSheet(workbook, request);
            writeDetailData(workbook, sheet, data);
            autoSizeColumns(sheet, DETALLE_HEADERS.length);

            workbook.write(outputStream);
            byte[] bytes = outputStream.toByteArray();

            log.info("Excel detalle report generated successfully, size: {} bytes", bytes.length);
            return bytes;

        } catch (IOException e) {
            log.error("Error generating Excel detalle report", e);
            throw new ReportGenerationException("Error al crear archivo Excel de detalle", e);
        }
    }

    private void validateInputData(List<?> data, InfoRequest request, String reportType) {
        if (data == null || data.isEmpty()) {
            throw new ReportGenerationException("No hay datos para generar el reporte de " + reportType);
        }
        if (request == null) {
            throw new ReportGenerationException("Request de reporte no puede ser nulo");
        }
    }

    private XSSFSheet createCabeceraSheet(XSSFWorkbook workbook, InfoRequest request) {
        String sheetName = buildSheetName("InformeRecojos", request);
        XSSFSheet sheet = workbook.createSheet(sheetName);

        createHeaderRow(sheet, workbook, CABECERA_HEADERS);
        return sheet;
    }

    private XSSFSheet createDetailSheet(XSSFWorkbook workbook, InfoRequest request) {
        String sheetName = buildSheetName("InformeRecojosDetalle", request);
        XSSFSheet sheet = workbook.createSheet(sheetName);

        createHeaderRow(sheet, workbook, DETALLE_HEADERS);
        return sheet;
    }

    private String buildSheetName(String prefix, InfoRequest request) {
        return String.format("%s_%s_%s%d", prefix, request.account(), request.processDate(), request.processBatch());
    }

    private void createHeaderRow(XSSFSheet sheet, XSSFWorkbook workbook, String[] headers) {
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = createHeaderStyle(workbook);

        for (int i = 0; i < headers.length; i++) {
            createCell(headerRow, i, headers[i], headerStyle);
        }
    }

    private void writeHeaderData(XSSFWorkbook workbook, XSSFSheet sheet, List<HeaderInfo> data) {
        int rowCount = 1;
        CellStyle dataStyle = createDataStyle(workbook);

        for (HeaderInfo cabecera : data) {
            Row row = sheet.createRow(rowCount++);
            writeHeaderRow(row, cabecera, dataStyle);
        }

        log.debug("Written {} cabecera data rows", data.size());
    }

    private void writeHeaderRow(Row row, HeaderInfo header, CellStyle dataStyle) {
        int columnCount = 0;
        createCell(row, columnCount++, header.getAccount(), dataStyle);
        createCell(row, columnCount++, header.getProcessDate(), dataStyle);
        createCell(row, columnCount++, header.getProcessBatch(), dataStyle);
        createCell(row, columnCount++, header.getConsultantArea(), dataStyle);
        createCell(row, columnCount++, header.getIssueDate(), dataStyle);
        createCell(row, columnCount++, header.getTicketNumber(), dataStyle);
        createCell(row, columnCount++, header.getTicketSequence(), dataStyle);
        createCell(row, columnCount++, header.getConsultantCode(), dataStyle);
        createCell(row, columnCount++, header.getConsultantName(), dataStyle);
        createCell(row, columnCount++, header.getTotalUnits(), dataStyle);
        createCell(row, columnCount++, header.getRelatedOrderNumber(), dataStyle);
        createCell(row, columnCount++, header.getRelatedOrderSequence(), dataStyle);
    }

    private void writeDetailData(XSSFWorkbook workbook, XSSFSheet sheet, List<DetailInfo> data) {
        int rowCount = 1;
        CellStyle dataStyle = createDataStyle(workbook);

        for (DetailInfo detalle : data) {
            Row row = sheet.createRow(rowCount++);
            writeDetailRow(row, detalle, dataStyle);
        }

        log.debug("Written {} detalle data rows", data.size());
    }

    private void writeDetailRow(Row row, DetailInfo detail, CellStyle dataStyle) {
        int columnCount = 0;
        createCell(row, columnCount++, detail.getAccount(), dataStyle);
        createCell(row, columnCount++, detail.getProcessDate(), dataStyle);
        createCell(row, columnCount++, detail.getProcessBatch(), dataStyle);
        createCell(row, columnCount++, detail.getConsultantArea(), dataStyle);
        createCell(row, columnCount++, detail.getIssueDate(), dataStyle);
        createCell(row, columnCount++, detail.getTicketNumber(), dataStyle);
        createCell(row, columnCount++, detail.getTicketSequence(), dataStyle);
        createCell(row, columnCount++, detail.getConsultantCode(), dataStyle);
        createCell(row, columnCount++, detail.getConsultantName(), dataStyle);
        createCell(row, columnCount++, detail.getTotalUnits(), dataStyle);
        createCell(row, columnCount++, detail.getRelatedOrderTransactionCode(), dataStyle);
        createCell(row, columnCount++, detail.getRelatedOrderNumber(), dataStyle);
        createCell(row, columnCount++, detail.getRelatedOrderSequence(), dataStyle);
        createCell(row, columnCount++, detail.getProductCode(), dataStyle);
        createCell(row, columnCount++, detail.getProductDescription(), dataStyle);
        createCell(row, columnCount++, detail.getUnitsToBePicked(), dataStyle);
        createCell(row, columnCount++, detail.getAttentionCampaign(), dataStyle);
        createCell(row, columnCount++, detail.getAttentionType(), dataStyle);
    }

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

    private void createCell(Row row, int columnIndex, Object value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellStyle(style);

        if (value == null) {
            cell.setCellValue("");
            return;
        }

        switch (value) {
            case Integer i -> cell.setCellValue(i);
            case String s -> cell.setCellValue(s);
            case Double d -> cell.setCellValue(d);
            case Boolean b -> cell.setCellValue(b);
            case LocalDate localDate -> cell.setCellValue(localDate.toString());
            case LocalDateTime localDateTime -> cell.setCellValue(localDateTime.toString());
            case Date sqlDate -> cell.setCellValue(sqlDate.toString());
            case Timestamp timestamp -> cell.setCellValue(timestamp.toString());
            default -> cell.setCellValue(value.toString());
        }
    }

    private void autoSizeColumns(XSSFSheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
        log.debug("Auto-sized {} columns", columnCount);
    }
}
