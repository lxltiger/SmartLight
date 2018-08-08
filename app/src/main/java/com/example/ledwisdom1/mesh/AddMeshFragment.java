package com.example.ledwisdom1.mesh;


import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ledwisdom1.CallBack;
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.api.Status;
import com.example.ledwisdom1.common.AutoClearValue;
import com.example.ledwisdom1.databinding.FragmentAddMeshBinding;
import com.example.ledwisdom1.fragment.ProduceAvatarFragment;
import com.example.ledwisdom1.sevice.TelinkLightService;

import java.io.File;
import java.util.UUID;

import static com.example.ledwisdom1.utils.ToastUtil.showToast;

/**
 * 添加mesh页面
 */
public class AddMeshFragment extends Fragment implements CallBack ,ProduceAvatarFragment.Listener{

    public static final String TAG = AddMeshFragment.class.getSimpleName();

    private AutoClearValue<FragmentAddMeshBinding> binding;
    private MeshViewModel meshViewModel;
    private ReportMesh reportMesh = new ReportMesh();

    public static AddMeshFragment newInstance() {
        Bundle args = new Bundle();
        AddMeshFragment fragment = new AddMeshFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public AddMeshFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportMesh.meshName = UUID.randomUUID().toString().substring(0, 6);
        reportMesh.meshPassword = UUID.randomUUID().toString().substring(0, 6);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentAddMeshBinding meshBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_mesh, container, false);
        meshBinding.setReportMesh(reportMesh);
        meshBinding.setHandler(this);
        binding = new AutoClearValue<>(this, meshBinding);
        return meshBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        meshViewModel = ViewModelProviders.of(this).get(MeshViewModel.class);
        meshViewModel.addMeshObserver.observe(this, resource -> {
            //控制进度条的可见性
            binding.get().setResource(resource);
            if (Status.SUCCESS == resource.status) {
                showToast(resource.message);
                //断开旧连接，这样进入HomeActivity会重连新的mesh
                TelinkLightService.Instance().idleMode(true);
                getActivity().finish();
            } else if (Status.ERROR == resource.status) {
                showToast(resource.message);
            }
        });

    }


    public void handleClick(View v) {
        switch (v.getId()) {
            case R.id.avatar:
                ProduceAvatarFragment.newInstance().show(getChildFragmentManager(), ProduceAvatarFragment.TAG);
                break;
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.confirm:
                if (TextUtils.isEmpty(reportMesh.homeName)||reportMesh.homeName.length()>10) {
                    Toast.makeText(getActivity(), "名称在1到10个字符之间", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (null == reportMesh.homeIcon) {
                    Toast.makeText(getActivity(), "还没有选择头像", Toast.LENGTH_SHORT).show();
                    return;
                }
                meshViewModel.meshObserver.setValue(reportMesh);
                break;
        }
    }


    @Override
    public void onItemClicked(File file) {
        Glide.with(this).load(file).into(binding.get().avatar);
        reportMesh.homeIcon = file;
    }
}
