package com.oguzhanozgokce.bookshare.common;


import com.oguzhanozgokce.bookshare.domain.error.Error;

import java.util.function.Consumer;

public final class Result<T, E extends Error> {
    private final T data;
    private final E error;

    private Result(T data, E error) {
        this.data = data;
        this.error = error;
    }

    public static <T, E extends Error> Result<T, E> success(T data) {
        return new Result<>(data, null);
    }

    public static <T, E extends Error> Result<T, E> error(E error) {
        return new Result<>(null, error);
    }

    public boolean isSuccess() { return data != null; }
    public boolean isError() { return error != null; }
    public T getData() { return data; }
    public E getError() { return error; }
    public void fold(Consumer<T> onSuccess, Consumer<String> onError) {
        if (isSuccess()) {
            onSuccess.accept(data);
        } else if (isError()) {
            onError.accept(error.getMessage());
        }
    }
}
