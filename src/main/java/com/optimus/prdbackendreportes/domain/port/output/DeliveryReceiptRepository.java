package com.optimus.prdbackendreportes.domain.port.output;

import com.optimus.prdbackendreportes.domain.model.entity.DeliveryReceiptItem;
import com.optimus.prdbackendreportes.domain.model.valueobject.Account;
import com.optimus.prdbackendreportes.domain.model.valueobject.OrderNumber;
import com.optimus.prdbackendreportes.domain.model.valueobject.ProcessBatch;
import com.optimus.prdbackendreportes.domain.model.valueobject.ProcessDate;

import java.util.List;

public interface DeliveryReceiptRepository {

    List<DeliveryReceiptItem> findDeliveryReceiptData(
            Account account,
            ProcessDate processDate,
            ProcessBatch processBatch,
            OrderNumber orderNumber,
            String schema
    );

    boolean existsDataFor(
            Account account,
            ProcessDate processDate,
            ProcessBatch processBatch,
            OrderNumber orderNumber,
            String schema
    );
}
