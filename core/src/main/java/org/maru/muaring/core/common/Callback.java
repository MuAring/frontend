package org.maru.muaring.core.common;

public interface Callback<T> {

    void onSuccess(T result);
    void onError(Exception e);
}