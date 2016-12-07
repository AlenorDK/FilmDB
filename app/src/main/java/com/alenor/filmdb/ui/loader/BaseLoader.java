package com.alenor.filmdb.ui.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public abstract class BaseLoader<T> extends AsyncTaskLoader<T> {

    private T result;

    public BaseLoader(Context context) {
        super(context);
    }

    @Override
    public T loadInBackground() {
        return null;
    }

    @Override
    public void deliverResult(T data) {
        result = data;

        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        if (result != null) {
            deliverResult(result);
        }

        if (result == null || takeContentChanged()) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
    }
}