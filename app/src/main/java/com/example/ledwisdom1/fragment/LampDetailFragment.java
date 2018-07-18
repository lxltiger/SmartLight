package com.example.ledwisdom1.fragment;


import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.FragmentLampDetailBinding;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.model.TitleBar;
import com.example.ledwisdom1.utils.BindingAdapters;

/**
 * A simple {@link Fragment} subclass.
 * 灯具详情设置
 */
public class LampDetailFragment extends Fragment implements TitleBar.OnTitleClickListener {
    private static final String TAG = LampDetailFragment.class.getSimpleName();
    private FragmentLampDetailBinding mBinding;
    public ObservableField<Lamp> mLamp;

    public LampDetailFragment() {
        // Required empty public constructor
    }

    public static LampDetailFragment newInstance(Lamp lamp) {
        Bundle args = new Bundle();
        args.putParcelable("lamp", lamp);
        LampDetailFragment fragment = new LampDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_lamp_detail, container, false);
        TitleBar titleBar = new TitleBar(true, getString(R.string.lampsetting_item_text), BindingAdapters.INVISIBLE, this);
        mBinding.setTitleBar(titleBar);
        Bundle arguments = getArguments();
        if (arguments != null) {
            Lamp lamp = arguments.getParcelable("lamp");
            mLamp =new ObservableField<>(lamp);
        }
        mBinding.setHandler(this);
        return mBinding.getRoot();
    }




    @Override
    public void onTitleClick(View view) {
        //处理返回
        getActivity().onBackPressed();
    }

    public void handleClick(View view) {
        Log.d(TAG, "mLamp.get():" + mLamp.get());
        switch (view.getId()) {
            case R.id.confirm:
                getActivity().onBackPressed();
                break;
            case R.id.lamp_gate:
                // TODO: 2018/6/11 0011 选择网关
                break;
        }
    }
}
