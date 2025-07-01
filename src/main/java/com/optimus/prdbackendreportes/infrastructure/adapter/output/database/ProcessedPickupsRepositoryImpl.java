package com.optimus.prdbackendreportes.infrastructure.adapter.output.database;

import com.optimus.prdbackendreportes.domain.model.entity.DetailInfo;
import com.optimus.prdbackendreportes.domain.model.entity.HeaderInfo;
import com.optimus.prdbackendreportes.domain.model.valueobject.Account;
import com.optimus.prdbackendreportes.domain.model.valueobject.ProcessBatch;
import com.optimus.prdbackendreportes.domain.model.valueobject.ProcessDate;
import com.optimus.prdbackendreportes.domain.port.output.ProcessedPickupsRepository;
import com.optimus.prdbackendreportes.infrastructure.mapper.ProcessedPickupsMapper;
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
public class ProcessedPickupsRepositoryImpl implements ProcessedPickupsRepository {
    private final DataSource dataSource;
    private final ProcessedPickupsMapper mapper;

    @Override
    public List<HeaderInfo> findHeaderInfo(Account account, ProcessDate processDate, ProcessBatch processBatch) {

        log.debug("Finding info cabecera for account: {}, processDate: {}, processBatch: {}",
                account.value(), processDate.value(), processBatch.value());

        try {
            SimpleJdbcCall jdbcCall = createHeaderCall("RPT_DESCARGAR_INFO_CAB");
            MapSqlParameterSource paramSource = buildParameterSource(account, processDate, processBatch);

            Map<String, Object> result = jdbcCall.execute(paramSource);

            @SuppressWarnings("unchecked")
            List<HeaderInfo> items = (List<HeaderInfo>) result.get("p_resultado");

            List<HeaderInfo> finalItems = items != null ? items : List.of();
            log.debug("Found {} info cabecera items", finalItems.size());

            return finalItems;

        } catch (DataAccessException e) {
            log.error("Database error while finding info cabecera", e);
            throw new RuntimeException("Error al consultar datos de cabecera", e);
        } catch (Exception e) {
            log.error("Unexpected error while finding info cabecera", e);
            throw new RuntimeException("Error inesperado al consultar cabecera", e);
        }
    }

    @Override
    public List<DetailInfo> findDetailInfo(Account account, ProcessDate processDate, ProcessBatch processBatch) {

        log.debug("Finding info detalle for account: {}, processDate: {}, processBatch: {}",
                account.value(), processDate.value(), processBatch.value());

        try {
            SimpleJdbcCall jdbcCall = createDetailCall("RPT_DESCARGAR_INFO_DET");
            MapSqlParameterSource paramSource = buildParameterSource(account, processDate, processBatch);

            Map<String, Object> result = jdbcCall.execute(paramSource);

            @SuppressWarnings("unchecked")
            List<DetailInfo> items = (List<DetailInfo>) result.get("p_resultado");

            List<DetailInfo> finalItems = items != null ? items : List.of();
            log.debug("Found {} info detalle items", finalItems.size());

            return finalItems;

        } catch (DataAccessException e) {
            log.error("Database error while finding info detalle", e);
            throw new RuntimeException("Error al consultar datos de detalle", e);
        } catch (Exception e) {
            log.error("Unexpected error while finding info detalle", e);
            throw new RuntimeException("Error inesperado al consultar detalle", e);
        }
    }

    private MapSqlParameterSource buildParameterSource(Account account, ProcessDate processDate, ProcessBatch processBatch) {
        return new MapSqlParameterSource()
                .addValue("p_cod_cuenta", account.value())
                .addValue("p_fec_proceso", processDate.value().toString())
                .addValue("p_lot_proceso", processBatch.value());
    }

    private SimpleJdbcCall createHeaderCall(String procedureName) {
        return new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_NAME)
                .withProcedureName(procedureName)
                .declareParameters(
                        new SqlParameter("p_cod_cuenta", Types.VARCHAR),
                        new SqlParameter("p_fec_proceso", Types.VARCHAR),
                        new SqlParameter("p_lot_proceso", Types.INTEGER),
                        new SqlOutParameter("p_resultado", Types.REF_CURSOR, mapper.getHeaderInfoMapper())
                );
    }

    private SimpleJdbcCall createDetailCall(String procedureName) {
        return new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_NAME)
                .withProcedureName(procedureName)
                .declareParameters(
                        new SqlParameter("p_cod_cuenta", Types.VARCHAR),
                        new SqlParameter("p_fec_proceso", Types.VARCHAR),
                        new SqlParameter("p_lot_proceso", Types.INTEGER),
                        new SqlOutParameter("p_resultado", Types.REF_CURSOR, mapper.getDetailInfoMapper())
                );
    }
}
