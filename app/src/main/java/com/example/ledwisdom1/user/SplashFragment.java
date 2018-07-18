package com.example.ledwisdom1.user;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.databinding.DataBindingUtil;

import com.example.ledwisdom1.databinding.FragmentSplashBinding;

import com.example.ledwisdom1.R;

/**
 * A simple {@link Fragment} subclass.
 */
@Deprecated
public class SplashFragment extends Fragment {
    public static final String TAG = SplashFragment.class.getSimpleName();


    public static SplashFragment newInstance(/**String param1, String param2*/) {
        SplashFragment fragment = new SplashFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentSplashBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
