package com.example.ledwisdom1.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class AutoClearValue<T> {
    private T data;

    public AutoClearValue(Fragment fragment, T data) {
        this.data=data;
        FragmentManager fragmentManger = fragment.getFragmentManager();
        fragmentManger.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                if (f==fragment) {
                    AutoClearValue.this.data=null;
                    fm.unregisterFragmentLifecycleCallbacks(this);
                }
            }
        }, false);
    }

    public T get() {
        return data;
    }
}
