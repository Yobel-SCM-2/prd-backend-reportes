package com.optimus.prdbackendreportes.application.services.interfaces;

import com.optimus.prdbackendreportes.domain.models.dto.request.InfoCabeceraRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IRecojosProcesadosService {

    void generateInfoCabeceraReport(HttpServletResponse response, InfoCabeceraRequest request) throws Exception;
}
