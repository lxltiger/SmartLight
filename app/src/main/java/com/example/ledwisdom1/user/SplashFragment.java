package com.example.ledwisdom1.user;


import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ledwisdom1.CallBack;
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.FragmentSplashBinding;
import com.example.ledwisdom1.view.RGBView;

/**
 * A simple {@link Fragment} subclass.
 */
@Deprecated
public class SplashFragment extends Fragment implements CallBack{
    public static final String TAG = SplashFragment.class.getSimpleName();
    private VectorDrawableCompat vectorDrawableCompat;
    FragmentSplashBinding binding;

    public static SplashFragment newInstance(/**String param1, String param2*/) {
        SplashFragment fragment = new SplashFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vectorDrawableCompat = VectorDrawableCompat.create(getResources(), R.drawable.ic_arrow_drop_down_black_24dp, getActivity().getTheme());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false);
        binding.setHandler(this);
        binding.ivRgb.setOnColorChangedListenner(listener);
        float degreeByColor = binding.ivRgb.getDegreeByColor(255, 0, 0);
//        tintIndicator(binding.indicator, color);
        return binding.getRoot();
    }

    private void tintIndicator(ImageView view, int color) {
        if (vectorDrawableCompat != null) {
            vectorDrawableCompat.setTint(color);
            view.setImageDrawable(vectorDrawableCompat);
        }

    }

    private RGBView.OnColorChangedListener listener = new RGBView.OnColorChangedListener() {
        @Override
        public void onColorChanged(int red, int green, int blue, float degree) {
            binding.view.setRotation(degree);
            Log.d(TAG, "degree:" + degree);
            tintIndicator(binding.indicator, Color.rgb(red,green, blue));
            float degreeByColor = binding.ivRgb.getDegreeByColor(red, green, blue);
            Log.d(TAG, "degreeByColor:" + degreeByColor);

        }


    };


    @Override
    public void handleClick(View view) {

    }
}
