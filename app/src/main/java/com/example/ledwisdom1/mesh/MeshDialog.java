package com.example.ledwisdom1.mesh;

import android.app.Activity;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.DialogMeshBinding;

import static com.example.ledwisdom1.mesh.MeshActivity.TYPE_EDIT_ACCOUNT;
import static com.example.ledwisdom1.mesh.MeshActivity.TYPE_EDIT_NAME;
import static com.example.ledwisdom1.mesh.MeshActivity.TYPE_EDIT_PSW;


/**
 *
 *
 */
public class MeshDialog extends DialogFragment {
    public static final String TAG = MeshDialog.class.getSimpleName();
    private DialogMeshBinding binding;
    private MeshViewModel meshViewModel;
    public final ObservableField<String> content = new ObservableField<>();

    public static MeshDialog newInstance() {
        MeshDialog fragment = new MeshDialog();
        Bundle args = new Bundle();
//        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_mesh, null, false);
        final Activity activity = getActivity();
        return new AlertDialog.Builder(activity)
                .setView(binding.getRoot())
                .create();
    }

   /* @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_mesh, container, false);
        return binding.getRoot();
    }*/

   /* @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setOnKeyListener((dialog, keyCode, event) -> keyCode == KeyEvent.KEYCODE_BACK);
    }*/

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        meshViewModel = ViewModelProviders.of(getActivity()).get(MeshViewModel.class);
        binding.setHandler(this);
        binding.setViewModel(meshViewModel);
        populate();
    }

    private void populate() {
        int type = meshViewModel.type.get();
        Log.d(TAG, "type:" + type);
        String value = "";
        switch (type) {
            case TYPE_EDIT_NAME:
                value = meshViewModel.name.get();
                break;
            case TYPE_EDIT_ACCOUNT:
                value = meshViewModel.account.get();
                break;
            case TYPE_EDIT_PSW:
                value = meshViewModel.password.get();
                break;
        }

        content.set(value);

    }

   /* @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        if (window != null) {
            DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
            window.setLayout(displayMetrics.widthPixels*4/5, displayMetrics.heightPixels/3);
        }
    }*/


    public void handleClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                this.dismiss();
                break;
            case R.id.btn_ok:
                updateContent();
                break;
        }
    }


    /**
     *
     */
    private void updateContent() {
        int type = meshViewModel.type.get();
        String value = content.get();
        switch (type) {
            case TYPE_EDIT_NAME:
                if (value.length() < 6 || value.length() > 10) {
                    binding.name.setError("名称为6-10数字或字母");
                    binding.name.requestFocus();
                    return;
                }
                meshViewModel.name.set(value);
                break;
            case TYPE_EDIT_ACCOUNT:
                if (value.length() < 6 || value.length() > 10) {
                    binding.account.setError("名称为6-10数字或字母");
                    binding.account.requestFocus();
                    return;
                }
                meshViewModel.account.set(value);
                break;
            case TYPE_EDIT_PSW:
                if (value.length() != 6) {
                    binding.psw.setError("密码长度为6");
                    binding.psw.requestFocus();
                    return;
                }
                meshViewModel.password.set(value);
                break;
        }
        this.dismiss();

    }
}