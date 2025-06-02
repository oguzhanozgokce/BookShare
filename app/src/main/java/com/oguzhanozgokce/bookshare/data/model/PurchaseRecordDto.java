package com.oguzhanozgokce.bookshare.data.model;

import java.util.Date;

public class PurchaseRecordDto {
    public String transactionId; // Benzersiz işlem ID'si
    public String listingId;
    public String bookTitle; // Kolay erişim için
    public double pricePaid;
    public String purchaseDate; // Firestore'a yazmak için String veya Long (timestamp)
    public String deliveryAddress; // Ad, Soyad, Telefon, Adres, Şehir, Posta Kodu birleştirilmiş veya ayrı alanlar
    public String paymentMethod; // "CREDIT_CARD", "CASH_ON_DELIVERY"

    public PurchaseRecordDto() {
    }

    public PurchaseRecordDto(String transactionId, String listingId, String bookTitle, double pricePaid, String purchaseDate, String deliveryAddress, String paymentMethod) {
        this.transactionId = transactionId;
        this.listingId = listingId;
        this.bookTitle = bookTitle;
        this.pricePaid = pricePaid;
        this.purchaseDate = purchaseDate;
        this.deliveryAddress = deliveryAddress;
        this.paymentMethod = paymentMethod;
    }
}
