package com.optimus.prdbackendreportes.infrastructure.adapter.output.database;

import com.optimus.prdbackendreportes.domain.model.entity.DeliveryReceiptItem;
import com.optimus.prdbackendreportes.domain.model.valueobject.Account;
import com.optimus.prdbackendreportes.domain.model.valueobject.OrderNumber;
import com.optimus.prdbackendreportes.domain.model.valueobject.ProcessBatch;
import com.optimus.prdbackendreportes.domain.model.valueobject.ProcessDate;
import com.optimus.prdbackendreportes.domain.port.output.DeliveryReceiptRepository;
import com.optimus.prdbackendreportes.infrastructure.mapper.DeliveryReceiptMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import static com.optimus.prdbackendreportes.domain.model.constants.ReportConstants.PACKAGE_NAME;

@Repository
@Log4j2
@RequiredArgsConstructor
public class DeliveryReceiptRepositoryImpl implements DeliveryReceiptRepository {

    private final DataSource dataSource;
    private final DeliveryReceiptMapper mapper;

    @Override
    public List<DeliveryReceiptItem> findDeliveryReceiptData(Account account, ProcessDate processDate,
                                                             ProcessBatch processBatch, OrderNumber orderNumber, String schema) {

        log.debug("Finding delivery receipt data for account: {}, processDate: {}, processBatch: {}, orderNumber: {}",
                account.value(), processDate.value(), processBatch.value(), orderNumber.value());

        try {
            SimpleJdbcCall jdbcCall = createProcedureCall("RPT_CONSTANCIA_ENTREGA");
            MapSqlParameterSource paramSource = buildParameterSource(account, processDate, processBatch, orderNumber, schema);

            Map<String, Object> result = jdbcCall.execute(paramSource);

            @SuppressWarnings("unchecked")
            List<DeliveryReceiptItem> items = (List<DeliveryReceiptItem>) result.get("p_resultado");

            List<DeliveryReceiptItem> finalItems = items != null ? items : List.of();
            log.debug("Found {} delivery receipt items", finalItems.size());

            return finalItems;

        } catch (DataAccessException e) {
            log.error("Database error while finding delivery receipt data", e);
            throw new RuntimeException("Error al consultar datos de constancia de entrega", e);
        } catch (Exception e) {
            log.error("Unexpected error while finding delivery receipt data", e);
            throw new RuntimeException("Error inesperado al consultar datos", e);
        }
    }

    @Override
    public boolean existsDataFor(Account account, ProcessDate processDate, ProcessBatch processBatch,
                                 OrderNumber orderNumber, String schema) {

        log.debug("Checking existence of delivery receipt data for account: {}, processDate: {}, processBatch: {}",
                account.value(), processDate.value(), processBatch.value());

        try {
            List<DeliveryReceiptItem> data = findDeliveryReceiptData(account, processDate, processBatch, orderNumber, schema);
            boolean exists = !data.isEmpty();

            log.debug("Data exists: {}", exists);
            return exists;

        } catch (Exception e) {
            log.error("Error checking data existence", e);
            return false;
        }
    }

    private MapSqlParameterSource buildParameterSource(Account account, ProcessDate processDate,
                                                       ProcessBatch processBatch, OrderNumber orderNumber, String schema) {
        return new MapSqlParameterSource()
                .addValue("p_schema", schema)
                .addValue("p_cod_cuenta", account.value())
                .addValue("p_fec_proceso", java.sql.Date.valueOf(processDate.value()))
                .addValue("p_lot_proceso", processBatch.value())
                .addValue("p_nro_orden", orderNumber.isEmpty() ? "" : orderNumber.value());
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
                        new SqlOutParameter("p_resultado", Types.REF_CURSOR, mapper)
                );
    }
}
