package com.oguzhanozgokce.bookshare.data.mapper;

import com.oguzhanozgokce.bookshare.data.model.ListingDto;
import com.oguzhanozgokce.bookshare.data.model.ListingType;
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
                safe(dto.startDate),
                safe(dto.endDate),
                safe(dto.createdAt),
                safe(dto.updatedAt)
        );
    }

    private static String safe(String value) {
        return value != null ? value : "";
    }
}
