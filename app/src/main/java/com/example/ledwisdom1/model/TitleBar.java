package com.example.ledwisdom1.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.view.View;

/**
 * 标题栏UI控制模型
 */
@Deprecated
public class TitleBar {

//    public static final int ICON_VISIBLE=1;
    public static final int ICON_INVISIBLE=-1;
    /**
     * 返回按钮的可见性
     */
    public ObservableBoolean mShowIcon;

    /**
     * 标题
     */
    public ObservableField<String> mTitle;

    public OnTitleClickListener mTitleClickListener;


    /**
     * 标题右边的按钮 更加页面的不同而变化
     */
    public ObservableInt mDynamicIcon ;


    public TitleBar() {
    }

    public TitleBar(boolean showBackIcon, String title, int dynamicIcon,OnTitleClickListener listener/*,String action*/) {
        mShowIcon = new ObservableBoolean(showBackIcon);
        mTitle = new ObservableField<>(title);
        mTitleClickListener=listener;
//        mAction=new ObservableField<>(action);
        mDynamicIcon = new ObservableInt(dynamicIcon);

    }

    /**
     * 标题点击监听
     */
    public interface OnTitleClickListener{
         void onTitleClick(View view);
    }


}
