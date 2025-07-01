package com.optimus.prdbackendreportes.infrastructure.adapter.output.report.strategy;

import com.optimus.prdbackendreportes.domain.exception.ReportGenerationException;
import com.optimus.prdbackendreportes.domain.model.entity.DeliveryReceiptItem;
import com.optimus.prdbackendreportes.domain.model.entity.DetailInfo;
import com.optimus.prdbackendreportes.domain.model.entity.HeaderInfo;
import com.optimus.prdbackendreportes.domain.model.enums.ReportFormat;
import com.optimus.prdbackendreportes.domain.port.output.ReportGenerator;
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

import static com.optimus.prdbackendreportes.domain.model.constants.ReportConstants.DETAIL_HEADERS;
import static com.optimus.prdbackendreportes.domain.model.constants.ReportConstants.HEADER_HEADERS;

/**
 * Implementación Strategy para generar reportes en formato Excel
 */
@Component
@Log4j2
public class ExcelReportGeneratorImpl implements ReportGenerator {

    @Override
    public byte[] generateDeliveryReceiptReport(List<DeliveryReceiptItem> data, Object request, ReportFormat format) {
        throw new UnsupportedOperationException("Excel format not supported for DeliveryReceipt reports. Use PDF format instead.");
    }

    @Override
    public byte[] generateHeaderInfoReport(List<HeaderInfo> data, Object request, ReportFormat format) {
        validateFormat(format);
        InfoRequest infoRequest = castRequest(request, InfoRequest.class);

        log.info("Generating Excel header report for {} items", data.size());

        validateInputData(data, infoRequest);

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            XSSFSheet sheet = createHeaderSheet(workbook, infoRequest);
            writeHeaderData(workbook, sheet, data);
            autoSizeColumns(sheet, HEADER_HEADERS.length);

            workbook.write(outputStream);
            byte[] bytes = outputStream.toByteArray();

            log.info("Excel header report generated successfully, size: {} bytes", bytes.length);
            return bytes;

        } catch (IOException e) {
            log.error("Error generating Excel header report", e);
            throw new ReportGenerationException("Error al crear archivo Excel de cabecera", e);
        }
    }

    @Override
    public byte[] generateDetailInfoReport(List<DetailInfo> data, Object request, ReportFormat format) {
        validateFormat(format);
        InfoRequest infoRequest = castRequest(request, InfoRequest.class);

        log.info("Generating Excel detail report for {} items", data.size());

        validateInputData(data, infoRequest);

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            XSSFSheet sheet = createDetailSheet(workbook, infoRequest);
            writeDetailData(workbook, sheet, data);
            autoSizeColumns(sheet, DETAIL_HEADERS.length);

            workbook.write(outputStream);
            byte[] bytes = outputStream.toByteArray();

            log.info("Excel detail report generated successfully, size: {} bytes", bytes.length);
            return bytes;

        } catch (IOException e) {
            log.error("Error generating Excel detail report", e);
            throw new ReportGenerationException("Error al crear archivo Excel de detalle", e);
        }
    }

    @Override
    public boolean supports(ReportFormat format) {
        return ReportFormat.EXCEL.equals(format);
    }

    @Override
    public ReportFormat getSupportedFormat() {
        return ReportFormat.EXCEL;
    }

    // Métodos privados auxiliares
    private void validateFormat(ReportFormat format) {
        if (!supports(format)) {
            throw new ReportGenerationException("Formato no soportado: " + format + ". Este generador solo soporta EXCEL.");
        }
    }

    private <T> T castRequest(Object request, Class<T> expectedType) {
        if (!expectedType.isInstance(request)) {
            throw new ReportGenerationException(
                    "Tipo de request incorrecto. Se esperaba " + expectedType.getSimpleName() +
                            " pero se recibió " + request.getClass().getSimpleName()
            );
        }
        return expectedType.cast(request);
    }

    private void validateInputData(List<?> data, Object request) {
        if (data == null || data.isEmpty()) {
            throw new ReportGenerationException("No hay datos para generar el reporte Excel");
        }
        if (request == null) {
            throw new ReportGenerationException("Request de reporte no puede ser nulo");
        }
    }

    private XSSFSheet createHeaderSheet(XSSFWorkbook workbook, InfoRequest request) {
        String sheetName = buildSheetName("InformeRecojos", request);
        XSSFSheet sheet = workbook.createSheet(sheetName);
        createHeaderRow(sheet, workbook, HEADER_HEADERS);
        return sheet;
    }

    private XSSFSheet createDetailSheet(XSSFWorkbook workbook, InfoRequest request) {
        String sheetName = buildSheetName("InformeRecojosDetalle", request);
        XSSFSheet sheet = workbook.createSheet(sheetName);
        createHeaderRow(sheet, workbook, DETAIL_HEADERS);
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

        for (HeaderInfo header : data) {
            Row row = sheet.createRow(rowCount++);
            writeHeaderRow(row, header, dataStyle);
        }

        log.debug("Written {} header data rows", data.size());
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

        for (DetailInfo detail : data) {
            Row row = sheet.createRow(rowCount++);
            writeDetailRow(row, detail, dataStyle);
        }

        log.debug("Written {} detail data rows", data.size());
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
