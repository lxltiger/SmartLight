package com.example.ledwisdom1.mesh;


import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.FragmentMeshBinding;
import com.example.ledwisdom1.model.QRCode;
import com.google.gson.Gson;

import ledwisdom1.example.com.zxinglib.camera.CaptureActivity;

/**
 * A simple {@link Fragment} subclass.
 * 蓝牙网络列表界面
 * todo  上下刷新
 */
@Deprecated
public class MeshFragment extends Fragment {
    public static final String TAG = MeshFragment.class.getSimpleName();
    private MeshAdapter mAdapter;

    public MeshFragment() {
        // Required empty public constructor
    }

    public static MeshFragment newInstance() {
        Bundle args = new Bundle();
        MeshFragment fragment = new MeshFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentMeshBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mesh, container, false);
        binding.mesh.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mAdapter = new MeshAdapter(mOnMeshListener);
        binding.mesh.setAdapter(mAdapter);
        binding.setHandler(this);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        loadMeshes();
    }

    public void onIconClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.add:
                MeshActivity activity = (MeshActivity) getActivity();
//                activity.loadFragment(true, new AddMeshFragment());
                break;
            case R.id.scan:
//                todo 摄像头权限
                startActivityForResult(new Intent(getActivity(), CaptureActivity.class), 0);
                break;
        }
    }

    /**
     * 选中一个条目后置顶 并更新当前mesh网络
     * 侧滑显示删除选项
     */
    /*private OnMeshListener mOnMeshListener = new OnMeshListener() {
        @Override
        public void onItemClick(View view, MeshBean meshBean) {
//            mAdapter.moveToTop(meshBean);
            SmartLightApp lightApp = SmartLightApp.INSTANCE();
            //更新内存
            lightApp.setMesh(meshBean);
//            保存到本地
            SharePrefencesUtil.saveMesh(meshBean);
        }

        @Override
        public void onDeleteClick(MeshBean meshBean) {
            // TODO: 2018/6/8 0008 判断是否有设备在使用此蓝牙网络
            deleteMesh(meshBean);
        }

        //显示mesh的二维码
        @Override
        public void onBarCodeClick(MeshBean meshBean) {
            QRCode qrCode = new QRCode(meshBean.getMeshPassword(), meshBean.getOthersId(), meshBean.getMeshName());
            QRCodeFragment tdBarCodeFragment = QRCodeFragment.newInstance(qrCode);
            tdBarCodeFragment.show(getFragmentManager(), QRCodeFragment.class.getSimpleName());

        }
    };*/





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            String result = data.getStringExtra("result");
            Log.d(TAG, result);

            QRCode qrCode = new Gson().fromJson(result, QRCode.class);
            if (qrCode != null) {
//            上传到网络
                //uploadMesh(qrCode.getMeshName(), qrCode.getMeshPassword(), qrCode.getOthersId());
            } else {
                Toast.makeText(getActivity(), "没能正确获取二维码结构", Toast.LENGTH_SHORT).show();
            }

        }

    }



}
