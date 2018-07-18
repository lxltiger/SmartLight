package com.example.ledwisdom1.user;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ledwisdom1.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutUSFragment extends Fragment {


    public AboutUSFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_about_us, container, false);
        view.findViewById(R.id.iv_back).setOnClickListener((view1)->{getActivity().finish();});
        return view;
    }

}
