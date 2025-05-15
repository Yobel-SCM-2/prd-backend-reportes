package com.optimus.prdbackendreportes.domain.models.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryReceiptItem {
    String account;
    String orderType;
    String orderNumber;
    String sequenceNumber;
    String destinationCode;
    String customerName;
    String deliveryAddress;
    String addressReference;
    String phoneNumber;
    LocalDateTime registrationDate;
    LocalDateTime dispatchDate;
    String productCode;
    String productDescription;
    int pickQuantity;
}
