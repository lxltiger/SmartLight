package com.example.ledwisdom1.utils;

import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;

public class DialogManager {
    public static final String TAG = "DialogManager";

    private FragmentManager mSupportFragmentManager;

    public DialogManager(@NonNull FragmentActivity activity) {
        mSupportFragmentManager = activity.getSupportFragmentManager();
    }


    /**
     * 显示DialogFragment
     *
     * @param tag            fragment的唯一标记
     * @param dialogFragment 对话框
     */
    public void showDialog(String tag, DialogFragment dialogFragment) {
        if (TextUtils.isEmpty(tag) || null == dialogFragment) {
            Log.e(TAG, "showDialog: invalid parameters");
            return;
        }
        FragmentTransaction fragmentTransaction = mSupportFragmentManager.beginTransaction();
        DialogFragment fragment = (DialogFragment) mSupportFragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.add(dialogFragment, tag);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void closeDialogFragment(String tag) {
        if (TextUtils.isEmpty(tag)) {
            Log.e(TAG, "showDialog: invalid parameters");
            return;
        }
        DialogFragment fragment = (DialogFragment) mSupportFragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            fragment.dismissAllowingStateLoss();
        }
    }
}