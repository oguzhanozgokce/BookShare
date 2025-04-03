package com.oguzhanozgokce.bookshare.data.model;

public enum ListingType {
    FOR_SALE, FOR_RENT;

    public static ListingType from(String raw) {
        if (raw == null) return FOR_SALE;
        switch (raw.toLowerCase()) {
            case "for_rent": return FOR_RENT;
            case "for_sale":
            default: return FOR_SALE;
        }
    }
}
