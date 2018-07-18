package com.example.ledwisdom1.device;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ledwisdom1.CallBack;
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.FragmentAddDeviceBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddDeviceFragment extends Fragment implements CallBack {
    public static final String TAG = AddDeviceFragment.class.getSimpleName();
    private DeviceViewModel viewModel;


    public static AddDeviceFragment newInstance(/**String param1, String param2*/) {
        AddDeviceFragment fragment = new AddDeviceFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentAddDeviceBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_device, container, false);
        binding.setHandler(this);
        return binding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(DeviceViewModel.class);
    }

    @Override
    public void handleClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                viewModel.navigation.setValue(DeviceActivity.FINISH);
                break;

            case R.id.add_hub:
                viewModel.navigation.setValue(DeviceActivity.NAVIGATE_TO_ADD_HUB);
                break;
            case R.id.add_other:
                viewModel.navigation.setValue(DeviceActivity.NAVIGATE_TO_ADD_LAMP);
                break;
        }
    }

}
