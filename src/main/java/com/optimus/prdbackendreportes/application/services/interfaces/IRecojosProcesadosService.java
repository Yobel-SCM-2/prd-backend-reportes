package com.optimus.prdbackendreportes.application.services.interfaces;

import com.optimus.prdbackendreportes.domain.models.dto.request.InfoCabeceraRequest;
import com.optimus.prdbackendreportes.domain.models.dto.response.FileInfoResponse;

public interface IRecojosProcesadosService {

    byte[] generateInfoCabeceraReport(InfoCabeceraRequest request);

    FileInfoResponse getFileInfo(InfoCabeceraRequest request);
}
