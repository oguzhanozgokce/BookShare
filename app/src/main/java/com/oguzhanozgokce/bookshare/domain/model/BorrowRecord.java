package com.oguzhanozgokce.bookshare.domain.model;

import java.util.Date;

public class BorrowRecord {
    private final String transactionId;
    private final String listingId;
    private final String bookTitle;
    private final Date borrowStartDate;
    private final Date borrowEndDate;
    private final String contactInfo; // Ad, Soyad, Telefon, Adres, Şehir, Posta Kodu
    // private ListingStatus status; // Bu bilgi Listing objesinden gelebilir.

    public BorrowRecord(String transactionId, String listingId, String bookTitle, Date borrowStartDate, Date borrowEndDate, String contactInfo) {
        this.transactionId = transactionId;
        this.listingId = listingId;
        this.bookTitle = bookTitle;
        this.borrowStartDate = borrowStartDate;
        this.borrowEndDate = borrowEndDate;
        this.contactInfo = contactInfo;
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

    public Date getBorrowStartDate() {
        return borrowStartDate;
    }

    public Date getBorrowEndDate() {
        return borrowEndDate;
    }

    public String getContactInfo() {
        return contactInfo;
    }
}
