package com.optimus.prdbackendreportes.domain.repositories.impl;

import com.optimus.prdbackendreportes.domain.models.dto.response.DeliveryReceiptItem;
import com.optimus.prdbackendreportes.domain.repositories.IDeliveryReceiptRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Implementación del repositorio que se conecta a la base de datos Oracle
 * para obtener datos de constancias de entrega.
 */
@Repository
public class DeliveryReceiptRepository implements IDeliveryReceiptRepository {

    private final DataSource dataSource;
    private static final String PACKAGE_NAME = "PKG_PRD_REPORTES";

    public DeliveryReceiptRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<DeliveryReceiptItem> getDeliveryReceiptData(
            String account,
            LocalDate processDate,
            int processBatch,
            String orderNumber,
            String schema) {

        SimpleJdbcCall jdbcCall = createProcedureCall("RPT_CONSTANCIA_ENTREGA");

        Map<String, Object> result;

        if (orderNumber != null && !orderNumber.isEmpty()) {
            result = jdbcCall.execute(
                    Map.of(
                            "p_schema", schema,
                            "p_cod_cuenta", account,
                            "p_fec_proceso", java.sql.Date.valueOf(processDate),
                            "p_lot_proceso", processBatch,
                            "p_nro_orden", orderNumber
                    )
            );
        } else {
            result = jdbcCall.execute(
                    Map.of(
                            "p_schema", schema,
                            "p_cod_cuenta", account,
                            "p_fec_proceso", java.sql.Date.valueOf(processDate),
                            "p_lot_proceso", processBatch,
                            "p_nro_orden", ""
                    )
            );
        }

        return (List<DeliveryReceiptItem>) result.get("p_resultado");
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
        return data.size();
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
                    rs.getTimestamp("FEC_REGISTRO").toLocalDateTime(),
                    rs.getTimestamp("FEC_DESPACHO").toLocalDateTime(),
                    rs.getString("COD_PRODUCTO"),
                    rs.getString("DES_PRODUCTO"),
                    rs.getInt("CTD_PICK")
            );
        }
    }
}