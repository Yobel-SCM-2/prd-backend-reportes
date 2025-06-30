package com.optimus.prdbackendreportes.domain.port.output;

import com.optimus.prdbackendreportes.domain.model.entity.DetailInfo;
import com.optimus.prdbackendreportes.domain.model.entity.HeaderInfo;
import com.optimus.prdbackendreportes.domain.model.valueobject.Account;
import com.optimus.prdbackendreportes.domain.model.valueobject.ProcessBatch;
import com.optimus.prdbackendreportes.domain.model.valueobject.ProcessDate;

import java.util.List;

public interface ProcessedPickupsRepository {

    List<HeaderInfo> findHeaderInfo(
            Account account,
            ProcessDate processDate,
            ProcessBatch processBatch
    );

    List<DetailInfo> findDetailInfo(
            Account account,
            ProcessDate processDate,
            ProcessBatch processBatch
    );
}