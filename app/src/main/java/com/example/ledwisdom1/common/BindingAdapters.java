package com.example.ledwisdom1.common;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.ledwisdom1.Config;
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.view.CircleTransform;
import com.example.ledwisdom1.view.ImageTransformationType;
import com.example.ledwisdom1.view.RoundTransform;

/**
 * xml中不要出现复杂的表达式
 * 不要出现中文字符 负责编译失败
 * 在这里处理负责逻辑
 */
public class BindingAdapters {
    private static final String TAG = "BindingAdapters";
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


    @android.databinding.BindingAdapter("deviceStatus")
    public static void setDeviceStatus(ImageView view, int brightness) {
        if (brightness > 0) {
            view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_green_circle));
        } else if (brightness == 0) {
            view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_red_circle));
        } else {
            view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_grey_circle));

        }

    }

    /**
     * @param view
     * @param type
     */
    @BindingAdapter("deviceIcon")
    public static void setDynamicIcon(ImageView view, int type) {
        switch (type) {
            case Config.LAMP_TYPE:
                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.icon_light_rgb));
                break;
            case Config.SOCKET_TYPE:
                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.icon_socket));
                break;
            case Config.PANEL_TYPE:
                view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.icon_panel));
                break;

        }
    }

    @BindingAdapter(value = {"imageUrl", "type"}, requireAll = false)
    public static void loadImageUrl(ImageView view, String url, ImageTransformationType type) {
        if (!TextUtils.isEmpty(url)) {
            Context context = view.getContext();
            if (type == null) {
                type = ImageTransformationType.NONE;
            }
            switch (type) {
                case ROUND:
//                    Glide.with(context).load(Config.IMG_PREFIX.concat(url)).transform(new RoundTransform(context, 2)).listener(drawableRequestListener).into(view);
                    Glide.with(context).load(Config.IMG_PREFIX.concat(url)).transform(new RoundTransform(context, 2)).crossFade(1000).into(view);
                    break;
                case CIRCLE:
                    Glide.with(context).load(Config.IMG_PREFIX.concat(url)).transform(new CircleTransform(context)).crossFade(1000).into(view);
                    break;
                case NONE:
                default:
                    Glide.with(context).load(Config.IMG_PREFIX.concat(url)).listener(drawableRequestListener).into(view);
                    break;
            }
        }
    }

    private static RequestListener<String,GlideDrawable> drawableRequestListener=new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            Log.e(TAG,e.toString()+"  model:"+model+" isFirstResource: "+isFirstResource);

            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            Log.e(TAG,"isFromMemoryCache:"+isFromMemoryCache+"  model:"+model+" isFirstResource: "+isFirstResource);

            return false;
        }
    };


    /*
     *  注意：参数的顺序要和value中的一致
     * */
    @BindingAdapter(value = {"dynamicImage", "resId"}, requireAll = false)
    public static void loadDynamicImage(ImageView view, String dynamicImage, int resId) {
        if (!TextUtils.isEmpty(dynamicImage)) {
            Glide.with(view.getContext()).load(dynamicImage).listener(drawableRequestListener).into(view);
        } else if (resId > 0) {
            view.setImageResource(resId);
        }
    }

    @BindingAdapter(value = {"avatar", "resId"}, requireAll = false)
    public static void loadAvatar(ImageView view, String avatar, int resId) {
        Context context = view.getContext();
        if (!TextUtils.isEmpty(avatar)) {
            Glide.with(context).load(avatar).transform(new CircleTransform(context)).listener(drawableRequestListener).into(view);
        } else  {
            Glide.with(context).load(R.drawable.pic_portrait).transform(new CircleTransform(context)).into(view);
        }


    }




    @BindingAdapter("visibleGone")
    public static void setVisible(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    @BindingAdapter("customGravity")
    public static void setCheckBoxGravity(CheckBox view, boolean isChecked) {
        view.setGravity(isChecked ? Gravity.START | Gravity.CENTER_VERTICAL : Gravity.END | Gravity.CENTER_VERTICAL);
    }
}
