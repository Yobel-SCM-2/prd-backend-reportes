package com.optimus.prdbackendreportes.application.port.input;

import com.optimus.prdbackendreportes.domain.model.entity.DeliveryReceiptItem;
import com.optimus.prdbackendreportes.domain.model.valueobject.Account;
import com.optimus.prdbackendreportes.domain.model.valueobject.OrderNumber;
import com.optimus.prdbackendreportes.domain.model.valueobject.ProcessBatch;
import com.optimus.prdbackendreportes.domain.model.valueobject.ProcessDate;

import java.util.List;

public interface DeliveryReceiptUseCase {

    List<DeliveryReceiptItem> generateReportData(Account account, ProcessDate processDate, ProcessBatch processBatch, OrderNumber orderNumber, String schema);

    void validateReportData(Account account, ProcessDate processDate, ProcessBatch processBatch, OrderNumber orderNumber, String schema);
}
