package com.example.ledwisdom1.mesh;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.ledwisdom1.CallBack;
import com.example.ledwisdom1.Config;
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.adapter.CommonPagerAdapter;
import com.example.ledwisdom1.api.Resource;
import com.example.ledwisdom1.api.Status;
import com.example.ledwisdom1.common.RequestCreator;
import com.example.ledwisdom1.databinding.FragmentMeshDetailBinding;
import com.example.ledwisdom1.databinding.LayoutEdit2Binding;
import com.example.ledwisdom1.databinding.MeshLayoutDetailBinding;
import com.example.ledwisdom1.fragment.ProduceAvatarFragment;
import com.example.ledwisdom1.utils.KeyBoardUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ledwisdom1.example.com.zxinglib.camera.QRCodeUtil;

import static com.example.ledwisdom1.utils.ToastUtil.showToast;

/**
 * 蓝牙详情页面
 */
public class MeshDetailFragment extends Fragment implements CallBack, ProduceAvatarFragment.Listener {
    public static final String TAG = MeshDetailFragment.class.getSimpleName();
    private MeshViewModel viewModel;
    private FragmentMeshDetailBinding binding;
    private MeshLayoutDetailBinding meshLayoutDetailBinding;
    private LayoutEdit2Binding editBinding;
    private ReportMesh reportMesh = new ReportMesh();
    private DefaultMesh defaultMesh;


    public MeshDetailFragment() {
        // Required empty public constructor
    }

    public static MeshDetailFragment newInstance(DefaultMesh mesh) {
        Bundle args = new Bundle();
        args.putParcelable("mesh", mesh);
        MeshDetailFragment fragment = new MeshDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defaultMesh = getArguments().getParcelable("mesh");
        if (defaultMesh != null) {
            reportMesh.homeName = defaultMesh.aijiaName;
            reportMesh.meshName = defaultMesh.name;
            reportMesh.imageUrl = Config.IMG_PREFIX.concat(defaultMesh.aijiaIcon);
            reportMesh.deviceCount = defaultMesh.deviceCount;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mesh_detail, container, false);
//        binding.toolbar.setNavigationIcon(R.drawable.ic_back_36dp);
        binding.toolbar.setNavigationOnClickListener(this::handleBackPress);
        binding.toolbar.inflateMenu(R.menu.mesh_detail);
        binding.toolbar.setOnMenuItemClickListener(this::onMenuItemClick);
        final String[] title = getResources().getStringArray(R.array.mesh_detail);
        binding.setTitle(title[0]);

        meshLayoutDetailBinding = DataBindingUtil.inflate(inflater, R.layout.mesh_layout_detail, container, false);
        meshLayoutDetailBinding.setHandler(this);
        meshLayoutDetailBinding.setReportMesh(reportMesh);
        String sharetext = RequestCreator.createShareMeshCode(defaultMesh.id, null, defaultMesh.creater);
        Bitmap bitmap = QRCodeUtil.createQRCode(sharetext, 300, 300);
        if (bitmap != null) {
            meshLayoutDetailBinding.ivTdCode.setImageBitmap(bitmap);
        }

        editBinding = DataBindingUtil.inflate(inflater, R.layout.layout_edit2, container, false);
        editBinding.setHandler(this);

        List<View> viewList = new ArrayList<>();
        viewList.add(meshLayoutDetailBinding.getRoot());
        viewList.add(editBinding.getRoot());
        CommonPagerAdapter pagerAdapter = new CommonPagerAdapter(viewList);
        binding.viewPager.setAdapter(pagerAdapter);
        binding.viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                binding.setTitle(title[position]);
            }
        });
        return binding.getRoot();
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MeshViewModel.class);
        meshLayoutDetailBinding.setViewModel(viewModel);
        subscribeUI(viewModel);

    }

    private void subscribeUI(MeshViewModel viewModel) {
        viewModel.modifyMeshObserver.observe(this, new Observer<Resource<Boolean>>() {
            @Override
            public void onChanged(@Nullable Resource<Boolean> resource) {
                meshLayoutDetailBinding.setResource(resource);
                if (Status.SUCCESS == resource.status) {
                    showToast(resource.message);
                    getActivity().finish();
                } else if (Status.ERROR == resource.status) {
                    showToast(resource.message);
                }
            }
        });

    }



    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_check:
                handleConfirm();
                return true;
        }
        return false;
    }

    private void handleBackPress(View view) {
        int currentItem = binding.viewPager.getCurrentItem();
        if (currentItem == 1) {
            KeyBoardUtils.closeKeyboard(editBinding.content,getActivity());
            binding.viewPager.setCurrentItem(0);
        } else {
            getActivity().finish();
        }
    }

    private void handleConfirm() {
        int currentItem = binding.viewPager.getCurrentItem();
        switch (currentItem) {
            case 0:
                if (!defaultMesh.isMine) {
                    showToast("不是自己的蓝牙网络");
                    return;
                }
                if (null == reportMesh.homeIcon) {
                    showToast("还没有选择头像");
                    return;
                }
                viewModel.modifymeshRequest.setValue(reportMesh);
                break;
            case 1:
                String name = editBinding.getName();
                if (TextUtils.isEmpty(name) || name.length() > 10) {
                    editBinding.content.setError("名称在1到10个字符之间");
                    editBinding.content.requestFocus();
                    return;
                }
                KeyBoardUtils.closeKeyboard(editBinding.content,getActivity());
                binding.viewPager.setCurrentItem(0);
                reportMesh.homeName = name;
                meshLayoutDetailBinding.modifyName.setValue(name);
                break;
        }
    }

    @Override
    public void handleClick(View view) {
        switch (view.getId()) {
            case R.id.modify_mesh:
                if (!defaultMesh.isMine) {
                    showToast("不是自己的蓝牙网络");
                    return;
                }
                ProduceAvatarFragment.newInstance().show(getChildFragmentManager(), ProduceAvatarFragment.TAG);
                break;
            case R.id.modify_name:
                if (!defaultMesh.isMine) {
                    showToast("不是自己的蓝牙网络");
                    return;
                }
                editBinding.setName(reportMesh.homeName);
                binding.viewPager.setCurrentItem(1);
                break;
            case R.id.clear:
                editBinding.setName("");
                break;
        }
    }

    @Override
    public void onItemClicked(File file) {
        reportMesh.homeIcon = file;
        meshLayoutDetailBinding.modifyMesh.setValue(file.getAbsolutePath());
    }

}
