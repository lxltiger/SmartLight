package com.example.ledwisdom1.mesh;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ledwisdom1.CallBack;
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.api.Resource;
import com.example.ledwisdom1.api.Status;
import com.example.ledwisdom1.databinding.FragmentMeshListBinding;
import com.example.ledwisdom1.sevice.TelinkLightService;

import java.util.ArrayList;
import java.util.List;

import static com.example.ledwisdom1.utils.ToastUtil.showToast;

/**
 * 蓝牙网络列表界面
 */
public class MeshListFragment extends Fragment implements CallBack {
    public static final String TAG = MeshListFragment.class.getSimpleName();

    private MeshAdapter myMeshAdapter;
    private MeshAdapter friendMeshAdapter;
    private FragmentMeshListBinding binding;
    private MeshViewModel viewModel;

    public MeshListFragment() {
        // Required empty public constructor
    }

    public static MeshListFragment newInstance() {
        Bundle args = new Bundle();
        MeshListFragment fragment = new MeshListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mesh_list, container, false);
        FragmentActivity activity = getActivity();
        binding.myMeshes.setLayoutManager(new GridLayoutManager(activity, 3));
        myMeshAdapter = new MeshAdapter(onMeshListener);
        binding.myMeshes.setAdapter(myMeshAdapter);

        binding.friendMeshes.setLayoutManager(new GridLayoutManager(activity, 3));
        friendMeshAdapter = new MeshAdapter(onMeshListener);
        binding.friendMeshes.setAdapter(friendMeshAdapter);

        binding.setHandler(this);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MeshViewModel.class);
        subscribeUI(viewModel);
        viewModel.meshListRequest.setValue(1);
    }

    private void subscribeUI(MeshViewModel viewModel) {
        viewModel.meshListObserver.observe(this, resource -> {
            binding.setResource(resource);
            if (Status.SUCCESS == resource.status) {
                List<Mesh> data = resource.data;
                List<Mesh> mine = new ArrayList<>();
                List<Mesh> friend = new ArrayList<>();
                for (Mesh mesh : data) {
                    if (mesh.isMyMesh()) {
                        mine.add(mesh);
                    } else {
                        friend.add(mesh);
                    }
                }
                myMeshAdapter.addMeshes(mine);
                friendMeshAdapter.addMeshes(friend);

            } else if (Status.ERROR == resource.status) {
                showToast(resource.message);
            }

        });

        viewModel.setDefaultMeshObserver.observe(this, resource -> {
            binding.setResource(resource);
            if (Status.SUCCESS == resource.status) {
                TelinkLightService.Instance().idleMode(true);
                showToast(resource.message);
                getActivity().finish();
            } else if (Status.ERROR == resource.status) {
                showToast(resource.message);
            }

        });

        viewModel.deleteMeshObserver.observe(this, new Observer<Resource<Mesh>>() {
            @Override
            public void onChanged(@Nullable Resource<Mesh> resource) {
                binding.setResource(resource);
                if (Status.SUCCESS == resource.status) {
                    if (resource.data.isMyMesh()) {
                        myMeshAdapter.removeMesh(resource.data);
                    } else {
                        friendMeshAdapter.removeMesh(resource.data);
                    }
                    showToast(resource.message);
                } else if (Status.ERROR == resource.status) {
                    showToast(resource.message);
                }

            }
        });
    }

    private OnMeshListener onMeshListener = new OnMeshListener() {
        @Override
        public void onItemClick(View view, Mesh meshBean) {
            //防止删除图标过小点击不到
            if (meshBean.isShowDeleteIcon()) {
                showDialog(meshBean);
            } else {
                viewModel.setDefaultMeshRequest.setValue(meshBean);
            }
        }

        @Override
        public void onDeleteClick(Mesh meshBean) {
            showDialog(meshBean);
        }

        @Override
        public boolean onItemLongClick(Mesh light) {
            myMeshAdapter.showDeleteIcon(true);
            friendMeshAdapter.showDeleteIcon(true);
            return true;
        }
    };

    private void showDialog(Mesh meshBean) {
        AlertDialog.Builder buidler = new AlertDialog.Builder(getActivity()).setTitle("确认删除")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.deleteMeshRequest.setValue(meshBean);
                        dialog.dismiss();

                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        buidler.create().show();
    }


    @Override
    public void handleClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.add_mesh:
                MeshActivity2.start(getActivity(),MeshActivity2.ACTION_ADD_MESH);
                break;
        }
    }

    public void handleBackPressed() {
        if (myMeshAdapter.isDeleteMode()) {
            myMeshAdapter.showDeleteIcon(false);
            friendMeshAdapter.showDeleteIcon(false);
        }else{
            getActivity().finish();
        }
    }
}
