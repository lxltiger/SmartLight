package com.example.ledwisdom1.clock;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ledwisdom1.CallBack;
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.FragmentClockListBinding;

/**
 *
 * 闹钟列表
 */
public class ClockListFragment extends Fragment implements CallBack{
    public static final String TAG = ClockListFragment.class.getSimpleName();
    private ClockAdapter clockAdapter;
    private ClockViewModel viewModel;


    public ClockListFragment() {
        // Required empty public constructor
    }

    public static ClockListFragment newInstance() {
        Bundle args = new Bundle();
        ClockListFragment fragment = new ClockListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentClockListBinding mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_clock_list, container, false);
        mBinding.setHandler(this);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        clockAdapter = new ClockAdapter(handleClockListener);
        mBinding.recyclerView.setAdapter(clockAdapter);
        return mBinding.getRoot();
    }

    private OnHandleClockListener handleClockListener = clock -> ClockActivity.start(getContext(),ClockActivity.ACTION_CLOCK,clock);


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(ClockViewModel.class);
        /*viewModel.sceneListObserver.observe(this, new Observer<ApiResponse<SceneList>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<SceneList> apiResponse) {
                if (apiResponse.isSuccessful()) {
                    if (apiResponse.body != null) {
                        List<Scene> list = apiResponse.body.getList();
                        if (list != null) {
                            clockAdapter.addScenes(list);
                        }
                    }
                }
            }
        });*/
    }




    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
//        viewModel.sceneListRequest.setValue(1);
    }



    @Override
    public void handleClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                getActivity().finish();
                break;
            case R.id.btn_add:
                ClockActivity.start(getContext(),ClockActivity.ACTION_CLOCK,null);
                break;

        }
    }
}
