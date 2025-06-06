package com.optimus.prdbackendreportes.application.services.impl;

import com.optimus.prdbackendreportes.application.services.interfaces.IRecojosProcesadosService;
import com.optimus.prdbackendreportes.domain.models.dto.request.InfoCabeceraRequest;
import com.optimus.prdbackendreportes.domain.models.dto.response.InfoCabecera;
import com.optimus.prdbackendreportes.domain.repositories.IRecojosProcesadosRepository;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Log4j2
public class RecojosProcesadosServiceImpl implements IRecojosProcesadosService {

    private final IRecojosProcesadosRepository repository;

    @Override
    public void generateInfoCabeceraReport(HttpServletResponse response, InfoCabeceraRequest request) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = writeHeaderLine(workbook, request);
            writeDataLines(workbook, sheet, request);

            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.close();
        }
    }

    private XSSFSheet writeHeaderLine(XSSFWorkbook workbook, InfoCabeceraRequest request) {
        String sheetName = "InformeRecojos_" + request.account() + "_" + request.processDate() + request.processBatch();
        XSSFSheet sheet = workbook.createSheet(sheetName);
        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "Código de cuenta", style);
        createCell(row, 1, "Fecha de Proceso", style);
        createCell(row, 2, "Lote de Proceso", style);
        createCell(row, 3, "Zona de Consultora", style);
        createCell(row, 4, "Fecha de Emisión", style);
        createCell(row, 5, "Número de Boleta", style);
        createCell(row, 6, "Secuencia de Boleta", style);
        createCell(row, 7, "Código de Consultora", style);
        createCell(row, 8, "Nombre de Consultora", style);
        createCell(row, 9, "Total de Unidades", style);
        createCell(row, 10, "Número de Pedido Relacionado", style);
        createCell(row, 11, "Secuencia de Pedido Relacionado", style);

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

    private void writeDataLines(XSSFWorkbook workbook, XSSFSheet sheet, InfoCabeceraRequest request) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (InfoCabecera detalle : repository.getInfoCabecera(request.account(), request.processDate(), request.processBatch())) {
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
            createCell(row, columnCount++, detalle.getRelatedOrderNumber(), style);
            createCell(row, columnCount++, detalle.getRelatedOrderSequence(), style);

            // Opcional: Auto-ajustar columnas después de escribir datos
            if (rowCount == 2) { // Solo en la primera fila de datos
                for (int i = 0; i < 12; i++) {
                    sheet.autoSizeColumn(i);
                }
            }
        }
    }
}
