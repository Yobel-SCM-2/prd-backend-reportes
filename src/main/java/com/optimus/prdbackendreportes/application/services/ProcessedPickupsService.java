package com.optimus.prdbackendreportes.application.services;

import com.optimus.prdbackendreportes.application.port.input.ProcessedPickupsUseCase;
import com.optimus.prdbackendreportes.domain.exception.NoDataFoundException;
import com.optimus.prdbackendreportes.domain.exception.ReportGenerationException;
import com.optimus.prdbackendreportes.domain.model.entity.DetailInfo;
import com.optimus.prdbackendreportes.domain.model.entity.HeaderInfo;
import com.optimus.prdbackendreportes.domain.model.valueobject.Account;
import com.optimus.prdbackendreportes.domain.model.valueobject.ProcessBatch;
import com.optimus.prdbackendreportes.domain.model.valueobject.ProcessDate;
import com.optimus.prdbackendreportes.domain.port.output.ProcessedPickupsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProcessedPickupsService implements ProcessedPickupsUseCase {

    private final ProcessedPickupsRepository repository;

    @Override
    public List<HeaderInfo> generateHeaderInfoData(Account account, ProcessDate processDate, ProcessBatch processBatch) {
        try {
            log.info("Generating info cabecera for account: {}, date: {}, batch: {}",
                    account.value(), processDate.value(), processBatch.value());

            List<HeaderInfo> data = repository.findHeaderInfo(account, processDate, processBatch);

            if (data.isEmpty()) {
                throw new NoDataFoundException("No se encontraron datos de cabecera para los parámetros especificados");
            }

            log.info("Found {} cabecera items", data.size());
            return data;

        } catch (NoDataFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error generating info cabecera", e);
            throw new ReportGenerationException("Error al obtener datos de cabecera: " + e.getMessage(), e);
        }
    }

    @Override
    public List<DetailInfo> generateDetailInfoData(Account account, ProcessDate processDate, ProcessBatch processBatch) {
        try {
            log.info("Generating info detalle for account: {}, date: {}, batch: {}",
                    account.value(), processDate.value(), processBatch.value());

            List<DetailInfo> data = repository.findDetailInfo(account, processDate, processBatch);

            if (data.isEmpty()) {
                throw new NoDataFoundException("No se encontraron datos de detalle para los parámetros especificados");
            }

            log.info("Found {} detalle items", data.size());
            return data;

        } catch (NoDataFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error generating info detalle", e);
            throw new ReportGenerationException("Error al obtener datos de detalle: " + e.getMessage(), e);
        }
    }
}
