package com.example.ledwisdom1.utils;

import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;

public class Bundler {


    private Bundle bundle;

    private Bundler() {
        this.bundle = new Bundle();
    }

    public static Bundler start() {
        return new Bundler();
    }

    public Bundler put(@NonNull String key, int value) {
        bundle.putInt(key, value);
        return this;
    }

    public Bundler put(@NonNull String key, String value) {
        bundle.putString(key, value);
        return this;
    }


    private Bundle get() {
        return bundle;
    }

    @NonNull public Bundle end() {
        Parcel parcel = Parcel.obtain();
        bundle.writeToParcel(parcel, 0);
        int size = parcel.dataSize();
//        Logger.e(size);
        if (size > 500000) {
            bundle.clear();
        }
        return get();
    }

    public static boolean isValidBundleSize(@NonNull Bundle bundle) {
        Parcel parcel = Parcel.obtain();
        bundle.writeToParcel(parcel, 0);
        return parcel.dataSize() < 500000;
    }

    private void clearBundle(Bundle safeBundle) {
        safeBundle.clear();
        safeBundle = null;
    }
}
