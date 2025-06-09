package com.optimus.prdbackendreportes.domain.repositories.impl;

import com.optimus.prdbackendreportes.domain.models.dto.response.InfoCabecera;
import com.optimus.prdbackendreportes.domain.models.dto.response.InfoDetalle;
import com.optimus.prdbackendreportes.domain.repositories.IRecojosProcesadosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import static com.optimus.prdbackendreportes.utils.constants.ReportConstants.PACKAGE_NAME;

@Repository
@RequiredArgsConstructor
public class RecojosProcesadosRepository implements IRecojosProcesadosRepository {

    private final DataSource dataSource;

    @Override
    public List<InfoCabecera> getInfoCabecera(String account, String processDate, int processBatch) {
        SimpleJdbcCall jdbcCall = createProcedureCallCabecera("RPT_DESCARGAR_INFO_CAB");

        MapSqlParameterSource paramSource = new MapSqlParameterSource()
                .addValue("p_cod_cuenta", account)
                .addValue("p_fec_proceso", processDate)
                .addValue("p_lot_proceso", processBatch);

        Map<String, Object> result = jdbcCall.execute(paramSource);

        @SuppressWarnings("unchecked")
        List<InfoCabecera> items = (List<InfoCabecera>) result.get("p_resultado");

        return items != null ? items : List.of();
    }

    @Override
    public List<InfoDetalle> getInfoDetalle(String account, String processDate, int processBatch) {
        SimpleJdbcCall jdbcCall = createProcedureCallDetalle("RPT_DESCARGAR_INFO_DET");

        MapSqlParameterSource paramSource = new MapSqlParameterSource()
                .addValue("p_cod_cuenta", account)
                .addValue("p_fec_proceso", processDate)
                .addValue("p_lot_proceso", processBatch);

        Map<String, Object> result = jdbcCall.execute(paramSource);

        @SuppressWarnings("unchecked")
        List<InfoDetalle> items = (List<InfoDetalle>) result.get("p_resultado");

        return items != null ? items : List.of();
    }

    /**
     * Crea la llamada al procedimiento para InfoCabecera
     */
    private SimpleJdbcCall createProcedureCallCabecera(String procedureName) {
        return new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_NAME)
                .withProcedureName(procedureName)
                .declareParameters(
                        new SqlParameter("p_cod_cuenta", Types.VARCHAR),
                        new SqlParameter("p_fec_proceso", Types.VARCHAR),
                        new SqlParameter("p_lot_proceso", Types.INTEGER),
                        new SqlOutParameter("p_resultado", Types.REF_CURSOR, new InfoCabeceraRowMapper())
                );
    }

    /**
     * Crea la llamada al procedimiento para InfoDetalle
     */
    private SimpleJdbcCall createProcedureCallDetalle(String procedureName) {
        return new SimpleJdbcCall(dataSource)
                .withCatalogName(PACKAGE_NAME)
                .withProcedureName(procedureName)
                .declareParameters(
                        new SqlParameter("p_cod_cuenta", Types.VARCHAR),
                        new SqlParameter("p_fec_proceso", Types.VARCHAR),
                        new SqlParameter("p_lot_proceso", Types.INTEGER),
                        new SqlOutParameter("p_resultado", Types.REF_CURSOR, new InfoDetalleRowMapper())
                );
    }

    /**
     * RowMapper para InfoCabecera
     */
    private static class InfoCabeceraRowMapper implements RowMapper<InfoCabecera> {
        @Override
        public InfoCabecera mapRow(java.sql.ResultSet rs, int rowNum) throws SQLException {
            return new InfoCabecera(
                    rs.getString("CBR_CUENTA"),
                    rs.getString("CBR_FCH_PROCESO"),
                    rs.getInt("CBR_LOTE_PROCESO"),
                    rs.getString("CBR_ZONA_CONSULTORA"),
                    rs.getString("FCH_EMISION"),
                    rs.getString("NUM_BOLETA"),
                    rs.getString("SEC_BOLETA"),
                    rs.getString("COD_CONSULTORA"),
                    rs.getString("NOMBRE_CONSULTORA"),
                    rs.getInt("CBR_TOT_UNIDADES"),
                    rs.getString("NRO_PEDIDO_RELACIONADO"),
                    rs.getInt("SEC_PEDIDO_RELACIONADO")
            );
        }
    }

    /**
     * RowMapper para InfoDetalle
     */
    private static class InfoDetalleRowMapper implements RowMapper<InfoDetalle> {
        @Override
        public InfoDetalle mapRow(java.sql.ResultSet rs, int rowNum) throws SQLException {
            return new InfoDetalle(
                    rs.getString("CBR_CUENTA"),
                    rs.getString("CBR_FCH_PROCESO"),
                    rs.getInt("CBR_LOTE_PROCESO"),
                    rs.getString("CBR_ZONA_CONSULTORA"),
                    rs.getString("FCH_EMISION"),
                    rs.getString("NUM_BOLETA"),
                    rs.getString("SEC_BOLETA"),
                    rs.getString("COD_CONSULTORA"),
                    rs.getString("NOMBRE_CONSULTORA"),
                    rs.getInt("CBR_TOT_UNIDADES"),
                    rs.getString("CBR_CTR"),
                    rs.getString("CBR_NRO_REL"),
                    rs.getInt("CBR_SEC_PEDIDO"),
                    rs.getString("DBR_COD_PRODUCTO"),
                    rs.getString("DESC_PRODUCTO"),
                    rs.getInt("DBR_UNIDADES_RECOJO"),
                    rs.getString("DBR_CAMP_ATENCION"),
                    rs.getString("DBR_TIPO_ATENCION")
            );
        }
    }
}