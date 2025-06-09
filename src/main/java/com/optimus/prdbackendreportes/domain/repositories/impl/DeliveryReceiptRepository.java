package com.optimus.prdbackendreportes.domain.repositories.impl;

import com.optimus.prdbackendreportes.domain.models.dto.response.DeliveryReceiptItem;
import com.optimus.prdbackendreportes.domain.repositories.IDeliveryReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.optimus.prdbackendreportes.utils.constants.ReportConstants.PACKAGE_NAME;

/**
 * Implementación del repositorio que se conecta a la base de datos Oracle
 * para obtener datos de constancias de entrega.
 */
@Repository
@RequiredArgsConstructor
public class DeliveryReceiptRepository implements IDeliveryReceiptRepository {

    private final DataSource dataSource;

    @Override
    public List<DeliveryReceiptItem> getDeliveryReceiptData(
            String account,
            LocalDate processDate,
            int processBatch,
            String orderNumber,
            String schema) {

        SimpleJdbcCall jdbcCall = createProcedureCall("RPT_CONSTANCIA_ENTREGA");

        // Usar MapSqlParameterSource para evitar problemas con valores nulos
        MapSqlParameterSource paramSource = new MapSqlParameterSource()
                .addValue("p_schema", schema)
                .addValue("p_cod_cuenta", account)
                .addValue("p_fec_proceso", java.sql.Date.valueOf(processDate))
                .addValue("p_lot_proceso", processBatch);

        // Manejar orderNumber correctamente
        if (orderNumber != null && !orderNumber.isEmpty()) {
            paramSource.addValue("p_nro_orden", orderNumber);
        } else {
            paramSource.addValue("p_nro_orden", "");
        }

        Map<String, Object> result = jdbcCall.execute(paramSource);

        @SuppressWarnings("unchecked")
        List<DeliveryReceiptItem> items = (List<DeliveryReceiptItem>) result.get("p_resultado");

        return items != null ? items : List.of();
    }

    @Override
    public int validateDataExists(
            String account,
            LocalDate processDate,
            int processBatch,
            String orderNumber,
            String schema) {

        // Este método usaría un procedimiento simplificado que sólo cuenta registros
        // Para el ejemplo, usaremos el mismo procedimiento pero contaremos los resultados
        List<DeliveryReceiptItem> data = getDeliveryReceiptData(account, processDate, processBatch, orderNumber, schema);
        return data != null ? data.size() : 0;
    }

    private SimpleJdbcCall createProcedureCall(String procedureName) {
        return new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_NAME)
                .withProcedureName(procedureName)
                .declareParameters(
                        new SqlParameter("p_schema", Types.VARCHAR),
                        new SqlParameter("p_cod_cuenta", Types.VARCHAR),
                        new SqlParameter("p_fec_proceso", Types.DATE),
                        new SqlParameter("p_lot_proceso", Types.INTEGER),
                        new SqlParameter("p_nro_orden", Types.VARCHAR),
                        new SqlOutParameter("p_resultado", Types.REF_CURSOR, new DeliveryReceiptRowMapper())
                );
    }

    /**
     * RowMapper para convertir resultados del cursor a objetos DeliveryReceiptItem
     */
    private static class DeliveryReceiptRowMapper implements RowMapper<DeliveryReceiptItem> {
        @Override
        public DeliveryReceiptItem mapRow(ResultSet rs, int rowNum) throws SQLException {
            // Verificación de nulos para evitar NullPointerException
            return new DeliveryReceiptItem(
                    rs.getString("COD_CUENTA"),
                    rs.getString("TIP_ORDEN"),
                    rs.getString("NRO_ORDEN"),
                    rs.getString("SEC_PED"),
                    rs.getString("COD_DESTINO"),
                    rs.getString("NOMB_APELL"),
                    rs.getString("DIREC_ENT"),
                    rs.getString("REF_DIREC_ENT"),
                    rs.getString("NRO_TLF"),
                    rs.getTimestamp("FEC_REGISTRO") != null ? rs.getTimestamp("FEC_REGISTRO").toLocalDateTime() : null,
                    rs.getTimestamp("FEC_DESPACHO") != null ? rs.getTimestamp("FEC_DESPACHO").toLocalDateTime() : null,
                    rs.getString("COD_PRODUCTO"),
                    rs.getString("DES_PRODUCTO"),
                    rs.getInt("CTD_PICK")
            );
        }
    }
}