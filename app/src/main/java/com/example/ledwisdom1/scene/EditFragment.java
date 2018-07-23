package com.example.ledwisdom1.scene;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ledwisdom1.CallBack;
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.FragmentEditBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment implements CallBack {
    public static final String TAG = EditFragment.class.getSimpleName();
    private GroupSceneViewModel viewModel;
    private FragmentEditBinding binding;


    public static EditFragment newInstance(/**String param1, String param2*/) {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit, container, false);
        binding.setHandler(this);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(GroupSceneViewModel.class);
        String name = viewModel.name;
        binding.setName(name);
    }

    @Override
    public void handleClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancel:
                getActivity().onBackPressed();
                break;
            case R.id.iv_modify:
                String name = binding.getName();
                if (TextUtils.isEmpty(name)) {
                    binding.content.setError("名称不能为空");
                    binding.content.requestFocus();
                    return;
                }
                viewModel.name=name;
                getActivity().onBackPressed();
                break;
            case R.id.iv_clear:
                binding.setName("");
                break;
        }
    }


}
