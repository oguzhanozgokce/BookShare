package com.oguzhanozgokce.bookshare.data.model;

import java.util.Date;

public class BorrowRecordDto {
    public String transactionId; // Benzersiz işlem ID'si
    public String listingId;
    public String bookTitle; // Kolay erişim için
    public String borrowStartDate; // Firestore'a yazmak için String veya Long (timestamp)
    public String borrowEndDate;   // Firestore'a yazmak için String veya Long (timestamp)
    public String contactInfo; // Ad, Soyad, Telefon, Adres, Şehir, Posta Kodu birleştirilmiş veya ayrı alanlar
    // public String status; // "REQUESTED", "ACCEPTED", "BORROWED", "RETURNED" (ListingStatus'ten yönetilebilir)

    public BorrowRecordDto() {
    }

    public BorrowRecordDto(String transactionId, String listingId, String bookTitle, String borrowStartDate, String borrowEndDate, String contactInfo) {
        this.transactionId = transactionId;
        this.listingId = listingId;
        this.bookTitle = bookTitle;
        this.borrowStartDate = borrowStartDate;
        this.borrowEndDate = borrowEndDate;
        this.contactInfo = contactInfo;
    }
}
