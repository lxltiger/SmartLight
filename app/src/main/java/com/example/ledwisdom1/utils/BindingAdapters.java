package com.example.ledwisdom1.utils;

import android.databinding.BindingAdapter;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.ledwisdom1.Config;
import com.example.ledwisdom1.R;

/**
 * xml中不要出现复杂的表达式
 * 不要出现中文字符 负责编译失败
 * 在这里处理负责逻辑
 */
public class BindingAdapters {

    public static final int LIGHT_HIDE = -1;
    public static final int LIGHT_OFF = 0;
    public static final int LIGHT_ON = 1;
    public static final int LIGHT_CUT = 2;
    public static final int LIGHT_SELECTED = 3;


    public static final int ADD = 0;
    public static final int ADDING = 1;
    public static final int ADDED = 2;


    public static final int INVISIBLE = -1;
    public static final int REFRESH = 0;

    /**
     * 动态设置 添加灯具按钮 的图标
     *
     * @param view
     * @param status
     */
    @android.databinding.BindingAdapter("icon")
    public static void setAddIcon(ImageView view, int status) {
        switch (status) {
            case ADD:
                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.icon_add));
                break;
            case ADDING:
                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.icon_refresh));
                break;
            case ADDED:
                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.icon_add_ok));
                break;
            default:
                view.setVisibility(View.GONE);

        }
    }

    @BindingAdapter(value = {"show", "msg"}, requireAll = false)
    public static void setWarning(EditText editText, boolean show, String msg) {
        if (show) {
            editText.setError(msg);
            editText.requestFocus();
        } else {
            editText.setError(null);

        }
    }

    /**
     * 灯的状态
     * 如果有出现INVISIBLE的可能，就要加上VISIBLE，否则一旦设为不可见，再设为其他状态将依旧不可见
     */
    @android.databinding.BindingAdapter("lightStatus")
    public static void setLightStatus(ImageView view, int status) {
        view.setVisibility(View.VISIBLE);
        switch (status) {
            case LIGHT_OFF:
                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_red_circle));
                break;
            case LIGHT_CUT:
                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_grey_circle));
                break;
            case LIGHT_ON:
                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_green_circle));
                break;
            case LIGHT_SELECTED:
                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.icon_add_ok));
                break;
            case LIGHT_HIDE:
            default:
                view.setVisibility(View.INVISIBLE);
                break;
        }
    }

    /**
     * 标题栏的动态按钮
     *
     * @param view
     * @param type
     */
    @BindingAdapter("dynamicIcon")
    public static void setDynamicIcon(ImageView view, int type) {
        switch (type) {
            case REFRESH:
//                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.refash2));
                break;
            case LIGHT_ON:
//                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.icon_light_on));
                break;
            case LIGHT_CUT:
//                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.icon_light_cut));
                break;
            case INVISIBLE:
            default:
                view.setVisibility(View.GONE);
        }
    }

    @BindingAdapter("imageUrl")
    public static void loadImageUrl(ImageView view, String url) {
      /*  if ("add".equals(url)) {
           view.setImageResource(R.drawable.scene_pic_add);
        }else{
            Glide.with(view.getContext()).load(url).into(view);
        }*/
        if (!TextUtils.isEmpty(url)) {
            Glide.with(view.getContext()).load(Config.IMG_PREFIX.concat(url)).into(view);
        }

    }


    /*加载条目的图片，先显示默认图片 等拍照再设置剪裁后的图片
    *  注意：参数的顺序要和value中的一致
    * */
    @BindingAdapter(value = {"dynamicImage","resId"},requireAll = false)
    public static void loadDynamicImage(ImageView view,String dynamicImage, int resId) {
        if(!TextUtils.isEmpty(dynamicImage)){
            Glide.with(view.getContext()).load(dynamicImage).into(view);
        }else if(resId!=-1){
            view.setImageResource(resId);
        }

    }



    @BindingAdapter("visibleGone")
    public static void setVisible(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
