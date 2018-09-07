package com.example.ledwisdom1.utils;

import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

import com.telink.TelinkApplication;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.util.EventListener;

public class MeshEventManager {

    public static void bindEventListener(LifecycleOwner lifecycleOwner, EventListener<String> eventListener, TelinkApplication application) {
         new EventObserver(lifecycleOwner,eventListener,application);
    }


    private static class EventObserver implements DefaultLifecycleObserver{
        private final EventListener<String> eventListener;
        private TelinkApplication application;

        private EventObserver(LifecycleOwner lifecycleOwner, EventListener<String> eventListener, TelinkApplication application) {
            lifecycleOwner.getLifecycle().addObserver(this);
            this.eventListener = eventListener;
            this.application = application;
        }


        @Override
        public void onResume(@NonNull LifecycleOwner owner) {
//            SmartLightApp smartLightApp = SmartLightApp.INSTANCE();
            application.addEventListener(NotificationEvent.ONLINE_STATUS, eventListener);
            application.addEventListener(DeviceEvent.STATUS_CHANGED, eventListener);
        }

        @Override
        public void onPause(@NonNull LifecycleOwner owner) {
            application.removeEventListener(eventListener);
        }
    }
}
