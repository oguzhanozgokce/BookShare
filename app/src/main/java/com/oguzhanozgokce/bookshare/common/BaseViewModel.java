package com.oguzhanozgokce.bookshare.common;

import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.function.Function;

/**
 * BaseViewModel, UI durumlarını yönetmek için kullanılan genel ViewModel şablonudur.
 *
 * - UI ile ilişkili durumlar `_uiState` üzerinden Mutable olarak tutulur.
 * - Dışarıdan erişim için sadece `uiState` (Immutable) expose edilir.
 * - `updateState()` metodu sayesinde hem main thread hem de background thread'den
 *   güvenli bir şekilde state güncellemesi yapılabilir.
 *
 * 🔁 `LiveData.setValue()` yalnızca main thread üzerinde çağrılabilirken,
 *     `LiveData.postValue()` background thread'lerden main thread'e veri gönderir.
 *     Bu nedenle `Looper.myLooper()` ile mevcut thread kontrol edilir.
 *
 * @param <T> UI state model sınıfı
 */

public abstract class BaseViewModel<T> extends ViewModel {
    protected final MutableLiveData<T> _uiState = new MutableLiveData<>();
    public LiveData<T> uiState = _uiState;

    public BaseViewModel() {
        _uiState.setValue(initialState());
    }

    /**
     * Başlangıç UI durumunu sağlayan soyut fonksiyon. Türetilen ViewModel'de override edilmelidir.
     */
    protected abstract T initialState();

    /**
     * UI state'ini güncellemek için kullanılır.
     * Mevcut thread kontrol edilir ve uygun şekilde `setValue` veya `postValue` çağrılır.
     *
     * @param updater Güncel state'e uygulanacak fonksiyon
     */
    protected void updateState(Function<T, T> updater) {
        T current = _uiState.getValue();
        if (current == null) current = initialState();

        if (Looper.myLooper() == Looper.getMainLooper()) {
            _uiState.setValue(updater.apply(current));
        } else {
            _uiState.postValue(updater.apply(current));
        }
    }
}
