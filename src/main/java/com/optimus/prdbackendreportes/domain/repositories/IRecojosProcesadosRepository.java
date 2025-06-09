package com.optimus.prdbackendreportes.domain.repositories;

import com.optimus.prdbackendreportes.domain.models.dto.response.InfoCabecera;
import com.optimus.prdbackendreportes.domain.models.dto.response.InfoDetalle;

import java.util.List;

public interface IRecojosProcesadosRepository {

    List<InfoCabecera> getInfoCabecera(
            String account,
            String processDate,
            int processBatch
    );

    List<InfoDetalle> getInfoDetalle(
            String account,
            String processDate,
            int processBatch
    );
}
