package com.oguzhanozgokce.bookshare.domain.model;

import java.util.Date;

public class PurchaseRecord {
    private final String transactionId;
    private final String listingId;
    private final String bookTitle;
    private final double pricePaid;
    private final Date purchaseDate;
    private final String deliveryAddress;
    private final String paymentMethod;

    public PurchaseRecord(String transactionId, String listingId, String bookTitle, double pricePaid, Date purchaseDate, String deliveryAddress, String paymentMethod) {
        this.transactionId = transactionId;
        this.listingId = listingId;
        this.bookTitle = bookTitle;
        this.pricePaid = pricePaid;
        this.purchaseDate = purchaseDate;
        this.deliveryAddress = deliveryAddress;
        this.paymentMethod = paymentMethod;
    }

    // Getters
    public String getTransactionId() {
        return transactionId;
    }

    public String getListingId() {
        return listingId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public double getPricePaid() {
        return pricePaid;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}
