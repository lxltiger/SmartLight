package com.example.ledwisdom1.common;

import android.arch.lifecycle.LiveData;

public class AbsentLiveData extends LiveData {


    private AbsentLiveData() {
        this.postValue(null);
    }

    public static <T> LiveData<T> create() {
        return new AbsentLiveData();
    }
}
