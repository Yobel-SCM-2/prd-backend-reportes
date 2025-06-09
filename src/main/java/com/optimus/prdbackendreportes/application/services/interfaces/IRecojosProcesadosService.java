package com.optimus.prdbackendreportes.application.services.interfaces;

import com.optimus.prdbackendreportes.domain.models.dto.request.InfoRequest;
import com.optimus.prdbackendreportes.domain.models.dto.response.FileInfoResponse;

public interface IRecojosProcesadosService {

    byte[] generateInfoCabeceraReport(InfoRequest request);

    FileInfoResponse getFileInfoCabecera(InfoRequest request);

    FileInfoResponse getFileInfoDetalle(InfoRequest request);

    byte[] generateInfoDetalleReport(InfoRequest request);
}
