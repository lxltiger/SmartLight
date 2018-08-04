package com.example.ledwisdom1.user;


import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.FragmentSplashBinding;

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
    FragmentSplashBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false);
        binding.btn.setOnClickListener((view)->{
            int colorText = binding.ivRgb.ColorText;
            int blue = Color.blue(colorText);
            int red = Color.red(colorText);
            int green = Color.green(colorText);
            Log.d(TAG, "blue:" + blue);
            Log.d(TAG, "red:" + red);
            Log.d(TAG, "green:" + green);

        });

        return binding.getRoot();
    }

}
