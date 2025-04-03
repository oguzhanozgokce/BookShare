package com.oguzhanozgokce.bookshare.domain.model;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.oguzhanozgokce.bookshare.R;

public enum Genre {
    EDUCATION(R.color.genre_education_bg),
    FICTION(R.color.genre_fiction_bg),
    SCIENCE(R.color.genre_science_bg),
    FANTASY(R.color.genre_fantasy_bg),
    UNKNOWN(R.color.genre_default_bg);

    private final int colorResId;

    Genre(int colorResId) {
        this.colorResId = colorResId;
    }

    public int getColorResId() {
        return colorResId;
    }

    public static Genre from(String raw) {
        if (raw == null) return UNKNOWN;
        switch (raw.toLowerCase()) {
            case "education":
                return EDUCATION;
            case "fiction":
                return FICTION;
            case "science":
                return SCIENCE;
            case "fantasy":
                return FANTASY;
            default:
                return UNKNOWN;
        }
    }
    public String getDisplayText() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public int resolveColor(Context context) {
        return ContextCompat.getColor(context, colorResId);
    }
}
