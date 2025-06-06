package com.optimus.prdbackendreportes.domain.repositories;

import com.optimus.prdbackendreportes.domain.models.dto.response.InfoCabecera;

import java.util.List;

public interface IRecojosProcesadosRepository {

    List<InfoCabecera> getInfoCabecera(
            String account,
            String processDate,
            int processBatch
    );
}
