package com.oguzhanozgokce.bookshare.common;

import com.oguzhanozgokce.bookshare.domain.error.Error;

import java.util.function.Consumer;

public class ResultUtils {

    public static <T, E extends Error> void fold(
            Result<T, E> result,
            Consumer<T> onSuccess,
            Consumer<String> onError
    ) {
        if (result.isSuccess()) {
            onSuccess.accept(result.getData());
        } else if (result.isError()) {
            onError.accept(result.getError().getMessage());
        }
    }
}
