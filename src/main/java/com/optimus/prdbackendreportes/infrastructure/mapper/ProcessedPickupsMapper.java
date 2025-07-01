package com.optimus.prdbackendreportes.infrastructure.mapper;

import com.optimus.prdbackendreportes.domain.model.entity.DetailInfo;
import com.optimus.prdbackendreportes.domain.model.entity.HeaderInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@Log4j2
public class ProcessedPickupsMapper {

    public RowMapper<HeaderInfo> getHeaderInfoMapper() {
        return new HeaderInfoRowMapper();
    }

    public RowMapper<DetailInfo> getDetailInfoMapper() {
        return new DetailInfoRowMapper();
    }

    private static class HeaderInfoRowMapper implements RowMapper<HeaderInfo> {

        @Override
        public HeaderInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            try {
                return new HeaderInfo(
                        extractString(rs, "CBR_CUENTA"),
                        extractString(rs, "CBR_FCH_PROCESO"),
                        extractInteger(rs, "CBR_LOTE_PROCESO"),
                        extractString(rs, "CBR_ZONA_CONSULTORA"),
                        extractString(rs, "FCH_EMISION"),
                        extractString(rs, "NUM_BOLETA"),
                        extractString(rs, "SEC_BOLETA"),
                        extractString(rs, "COD_CONSULTORA"),
                        extractString(rs, "NOMBRE_CONSULTORA"),
                        extractInteger(rs, "CBR_TOT_UNIDADES"),
                        extractString(rs, "NRO_PEDIDO_RELACIONADO"),
                        extractInteger(rs, "SEC_PEDIDO_RELACIONADO")
                );
            } catch (SQLException e) {
                log.error("Error mapping HeaderInfo at row {}: {}", rowNum, e.getMessage());
                throw e;
            }
        }
    }

    private static class DetailInfoRowMapper implements RowMapper<DetailInfo> {

        @Override
        public DetailInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            try {
                return new DetailInfo(
                        extractString(rs, "CBR_CUENTA"),
                        extractString(rs, "CBR_FCH_PROCESO"),
                        extractInteger(rs, "CBR_LOTE_PROCESO"),
                        extractString(rs, "CBR_ZONA_CONSULTORA"),
                        extractString(rs, "FCH_EMISION"),
                        extractString(rs, "NUM_BOLETA"),
                        extractString(rs, "SEC_BOLETA"),
                        extractString(rs, "COD_CONSULTORA"),
                        extractString(rs, "NOMBRE_CONSULTORA"),
                        extractInteger(rs, "CBR_TOT_UNIDADES"),
                        extractString(rs, "CBR_CTR"),
                        extractString(rs, "CBR_NRO_REL"),
                        extractInteger(rs, "CBR_SEC_PEDIDO"),
                        extractString(rs, "DBR_COD_PRODUCTO"),
                        extractString(rs, "DESC_PRODUCTO"),
                        extractInteger(rs, "DBR_UNIDADES_RECOJO"),
                        extractString(rs, "DBR_CAMP_ATENCION"),
                        extractString(rs, "DBR_TIPO_ATENCION")
                );
            } catch (SQLException e) {
                log.error("Error mapping DetailInfo at row {}: {}", rowNum, e.getMessage());
                throw e;
            }
        }
    }

    private static String extractString(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value != null ? value.trim() : null;
    }

    private static Integer extractInteger(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : value;
    }
}
