package com.example.ledwisdom1.scene;

import android.app.Activity;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.example.ledwisdom1.CallBack;
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.DialogSceneBinding;
import com.example.ledwisdom1.utils.AutoClearValue;


/**
 * 场景 情景 修改名称的对话框
 */
public class SceneDialog extends DialogFragment implements CallBack {
    public static final String TAG = SceneDialog.class.getSimpleName();
    private AutoClearValue<DialogSceneBinding> binding;

    public static SceneDialog newInstance(ObservableField<String> content) {
        SceneDialog fragment = new SceneDialog();
        Bundle args = new Bundle();
        args.putSerializable("content", content);
//        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        DialogSceneBinding dialogSceneBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_scene, null, false);
        binding = new AutoClearValue<>(this, dialogSceneBinding);
        final Activity activity = getActivity();
        return new AlertDialog.Builder(activity)
                .setView(dialogSceneBinding.getRoot())
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
        binding.get().setHandler(this);
        Bundle arguments = getArguments();
        if (arguments != null) {
            ObservableField<String> content = (ObservableField<String>) arguments.getSerializable("content");
            binding.get().setContent(content);
        }
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


    @Override
    public void handleClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                this.dismiss();
                break;
            case R.id.btn_ok:
                String content = binding.get().getContent().get();
                if (TextUtils.isEmpty(content)) {
                    binding.get().content.setError("内容不能为空");
                    binding.get().content.requestFocus();
                    return;
                }
                this.dismiss();
                break;
        }
    }


}