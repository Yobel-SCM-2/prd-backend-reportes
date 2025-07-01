package com.optimus.prdbackendreportes.application.port.input;

import com.optimus.prdbackendreportes.domain.model.entity.DetailInfo;
import com.optimus.prdbackendreportes.domain.model.entity.HeaderInfo;
import com.optimus.prdbackendreportes.domain.model.valueobject.Account;
import com.optimus.prdbackendreportes.domain.model.valueobject.ProcessBatch;
import com.optimus.prdbackendreportes.domain.model.valueobject.ProcessDate;

import java.util.List;

public interface ProcessedPickupsUseCase {

    List<HeaderInfo> generateHeaderInfoData(Account account, ProcessDate processDate, ProcessBatch processBatch);

    List<DetailInfo> generateDetailInfoData(Account account, ProcessDate processDate, ProcessBatch processBatch);
}
