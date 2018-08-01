package com.example.ledwisdom1.scene;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.FragmentSceneSettingBinding;

/**
 *
 *情景下的场景或情景灯具设置
 *
 */
public class SceneSettingFragment extends Fragment {
    public static final String TAG = SceneSettingFragment.class.getSimpleName();
    private FragmentSceneSettingBinding mBinding;

    public SceneSettingFragment() {
        // Required empty public constructor
    }

    public static SceneSettingFragment newInstance() {
        Bundle args = new Bundle();
        SceneSettingFragment fragment = new SceneSettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_scene_setting, container, false);


        return mBinding.getRoot();
    }




    /**
     * 底部按钮切换监听 设置SeekBar进度 改亮度
     *
     * @param group
     * @param checkedId
     */
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int brightness = 0;
        int resId = 0;
        switch (checkedId) {
            case R.id.rb_sleep:
                brightness = 40;
                resId = R.string.sleep;
                break;
            case R.id.rb_visit:
                brightness = 100;
                resId = R.string.com_visit;
                break;
            case R.id.rb_read:
//                brightness = 80;

                resId = R.string.readding;
                break;
            case R.id.rb_conservation:
                resId = R.string.conservation;
                brightness = 40;
                break;
        }
    }

    public void handleClick(View view) {
        Log.d(TAG, "handleClick: ");
        switch (view.getId()) {
            case R.id.iv_switch:
                break;
            case R.id.iv_back:
                getActivity().finish();
                break;
        }
    }


}
