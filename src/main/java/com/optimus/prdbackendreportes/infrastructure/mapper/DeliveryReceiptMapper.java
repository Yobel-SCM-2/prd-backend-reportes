package com.optimus.prdbackendreportes.infrastructure.mapper;

import com.optimus.prdbackendreportes.domain.model.entity.DeliveryReceiptItem;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Component
@Log4j2
public class DeliveryReceiptMapper implements RowMapper<DeliveryReceiptItem> {

    @Override
    public DeliveryReceiptItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            return new DeliveryReceiptItem(
                    extractString(rs, "COD_CUENTA"),
                    extractString(rs, "TIP_ORDEN"),
                    extractString(rs, "NRO_ORDEN"),
                    extractString(rs, "SEC_PED"),
                    extractString(rs, "COD_DESTINO"),
                    extractString(rs, "NOMB_APELL"),
                    extractString(rs, "DIREC_ENT"),
                    extractString(rs, "REF_DIREC_ENT"),
                    extractString(rs, "NRO_TLF"),
                    extractLocalDateTime(rs, "FEC_REGISTRO"),
                    extractLocalDateTime(rs, "FEC_DESPACHO"),
                    extractString(rs, "COD_PRODUCTO"),
                    extractString(rs, "DES_PRODUCTO"),
                    extractInt(rs, "CTD_PICK")
            );
        } catch (SQLException e) {
            log.error("Error mapping DeliveryReceiptItem at row {}: {}", rowNum, e.getMessage());
            throw e;
        }
    }

    private String extractString(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value != null ? value.trim() : null;
    }

    private int extractInt(ResultSet rs, String columnName) throws SQLException {
        return rs.getInt(columnName);
    }

    private LocalDateTime extractLocalDateTime(ResultSet rs, String columnName) throws SQLException {
        java.sql.Timestamp timestamp = rs.getTimestamp(columnName);
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }
}
