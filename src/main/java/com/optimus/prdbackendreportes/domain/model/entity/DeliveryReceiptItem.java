package com.optimus.prdbackendreportes.domain.model.entity;

import java.time.LocalDateTime;

public class DeliveryReceiptItem {
    private final String account;
    private final String orderType;
    private final String orderNumber;
    private final String sequenceNumber;
    private final String destinationCode;
    private final String customerName;
    private final String deliveryAddress;
    private final String addressReference;
    private final String phoneNumber;
    private final LocalDateTime registrationDate;
    private final LocalDateTime dispatchDate;
    private final String productCode;
    private final String productDescription;
    private final int pickQuantity;

    public DeliveryReceiptItem(String account, String orderType, String orderNumber, String sequenceNumber, String destinationCode, String customerName, String deliveryAddress, String addressReference, String phoneNumber, LocalDateTime registrationDate, LocalDateTime dispatchDate, String productCode, String productDescription, int pickQuantity) {
        this.account = account;
        this.orderType = orderType;
        this.orderNumber = orderNumber;
        this.sequenceNumber = sequenceNumber;
        this.destinationCode = destinationCode;
        this.customerName = customerName;
        this.deliveryAddress = deliveryAddress;
        this.addressReference = addressReference;
        this.phoneNumber = phoneNumber;
        this.registrationDate = registrationDate;
        this.dispatchDate = dispatchDate;
        this.productCode = productCode;
        this.productDescription = productDescription;
        this.pickQuantity = pickQuantity;
    }

    public String getAccount() {
        return account;
    }

    public String getOrderType() {
        return orderType;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getSequenceNumber() {
        return sequenceNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getDestinationCode() {
        return destinationCode;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getAddressReference() {
        return addressReference;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDateTime getDispatchDate() {
        return dispatchDate;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public int getPickQuantity() {
        return pickQuantity;
    }
}
