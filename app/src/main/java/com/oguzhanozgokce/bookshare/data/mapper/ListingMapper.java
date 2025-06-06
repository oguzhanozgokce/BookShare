package com.oguzhanozgokce.bookshare.data.mapper;

import com.oguzhanozgokce.bookshare.data.model.ListingDto;
import com.oguzhanozgokce.bookshare.data.model.ListingType;
import com.oguzhanozgokce.bookshare.domain.model.Genre;
import com.oguzhanozgokce.bookshare.domain.model.Listing;

public class ListingMapper {
    public static Listing toDomain(ListingDto dto) {
        return new Listing(
                safe(dto.id),
                safe(dto.title),
                safe(dto.description),
                safe(dto.condition),
                safe(dto.location),
                ListingType.from(dto.type),
                dto.price != null ? dto.price : 0,
                safe(dto.address),
                dto.isShared != null && dto.isShared,
                Genre.from(dto.genre),
                safe(dto.startDate),
                safe(dto.endDate),
                safe(dto.createdAt),
                safe(dto.updatedAt),
                safe(dto.userId),
                safe(dto.author),
                safe(dto.isbn),
                dto.imageUrls != null ? dto.imageUrls : new java.util.ArrayList<>(),
                safe(dto.publisher),
                dto.publishYear != null ? dto.publishYear : 0,
                dto.pageCount != null ? dto.pageCount : 0,
                safe(dto.language)
        );
    }

    private static String safe(String value) {
        return value != null ? value : "";
    }
}
