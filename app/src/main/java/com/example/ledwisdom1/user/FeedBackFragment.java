package com.example.ledwisdom1.user;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ledwisdom1.CallBack;
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.FragmentFeedBackBinding;
import com.example.ledwisdom1.utils.AutoClearValue;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedBackFragment extends Fragment implements CallBack {

    private UserViewModel userViewModel;
    private AutoClearValue<FragmentFeedBackBinding> binding;

    public FeedBackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentFeedBackBinding feedBackBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed_back, container, false);
        feedBackBinding.setHandler(this);
        binding = new AutoClearValue<>(this, feedBackBinding);
        return feedBackBinding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        binding.get().setViewModel(userViewModel);
        userViewModel.feedBackResponseObserver.observe(this, apiResponse -> {
            if (apiResponse.isSuccessful()) {
                if (apiResponse.body.succeed()) {
                    getActivity().finish();
                }
                showToast(apiResponse.body.resultMsg);
            } else {
                showToast(apiResponse.errorMsg);
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void handleClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                getActivity().finish();
                break;

            case R.id.submit:
                userViewModel.feedBack();
                break;
        }
    }
}
