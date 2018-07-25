package com.example.ledwisdom1.clock;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.example.ledwisdom1.repository.HomeRepository;

public class ClockViewModel extends AndroidViewModel {
    private HomeRepository homeRepository;
    public ClockViewModel(@NonNull Application application) {
        super(application);
        homeRepository = HomeRepository.INSTANCE(application);
    }
}
