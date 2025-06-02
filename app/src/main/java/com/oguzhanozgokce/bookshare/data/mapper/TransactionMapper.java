package com.oguzhanozgokce.bookshare.data.mapper;

import com.oguzhanozgokce.bookshare.data.model.BorrowRecordDto;
import com.oguzhanozgokce.bookshare.data.model.PurchaseRecordDto;
import com.oguzhanozgokce.bookshare.domain.model.BorrowRecord;
import com.oguzhanozgokce.bookshare.domain.model.PurchaseRecord;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TransactionMapper {

    private static final SimpleDateFormat firestoreDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);

    static {
        firestoreDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    // PurchaseRecord Mapper
    public static PurchaseRecordDto toPurchaseRecordDto(PurchaseRecord record) {
        if (record == null) return null;
        return new PurchaseRecordDto(
                record.getTransactionId(),
                record.getListingId(),
                record.getBookTitle(),
                record.getPricePaid(),
                dateToString(record.getPurchaseDate()),
                record.getDeliveryAddress(),
                record.getPaymentMethod()
        );
    }

    public static PurchaseRecord toPurchaseRecordDomain(PurchaseRecordDto dto) {
        if (dto == null) return null;
        return new PurchaseRecord(
                dto.transactionId,
                dto.listingId,
                dto.bookTitle,
                dto.pricePaid,
                stringToDate(dto.purchaseDate),
                dto.deliveryAddress,
                dto.paymentMethod
        );
    }

    // BorrowRecord Mapper
    public static BorrowRecordDto toBorrowRecordDto(BorrowRecord record) {
        if (record == null) return null;
        return new BorrowRecordDto(
                record.getTransactionId(),
                record.getListingId(),
                record.getBookTitle(),
                dateToString(record.getBorrowStartDate()),
                dateToString(record.getBorrowEndDate()),
                record.getContactInfo()
        );
    }

    public static BorrowRecord toBorrowRecordDomain(BorrowRecordDto dto) {
        if (dto == null) return null;
        return new BorrowRecord(
                dto.transactionId,
                dto.listingId,
                dto.bookTitle,
                stringToDate(dto.borrowStartDate),
                stringToDate(dto.borrowEndDate),
                dto.contactInfo
        );
    }

    private static String dateToString(Date date) {
        if (date == null) return null;
        return firestoreDateFormat.format(date);
    }

    private static Date stringToDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) return null;
        try {
            return firestoreDateFormat.parse(dateString);
        } catch (ParseException e) {
            System.err.println("Error parsing date string: " + dateString + " - " + e.getMessage());
            // Fallback to parsing as Long (milliseconds) if ISO format fails
            try {
                return new Date(Long.parseLong(dateString));
            } catch (NumberFormatException nfe) {
                System.err.println("Error parsing date string as Long: " + dateString + " - " + nfe.getMessage());
                return null;
            }
        }
    }
}
