package com.example.ledwisdom1.home;


import android.app.Activity;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.clock.ClockActivity;
import com.example.ledwisdom1.databinding.FragmentMoreBinding;
import com.example.ledwisdom1.mesh.DefaultMesh;
import com.example.ledwisdom1.mesh.ReportMesh;
import com.example.ledwisdom1.scene.GroupSceneActivity;
import com.example.ledwisdom1.user.UserActivity;
import com.example.ledwisdom1.utils.AutoClearValue;
import com.example.ledwisdom1.utils.ImageUtil;
import com.example.ledwisdom1.utils.SDCardUtils;

import java.io.File;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * 主页更多界面
 */
public class MoreFragment extends Fragment implements View.OnClickListener {


    public static final String TAG = MoreFragment.class.getSimpleName();
    private AutoClearValue<FragmentMoreBinding> binding;
    private HomeViewModel viewModel;
    private ReportMesh reportMesh;

    public static MoreFragment newInstance() {

        Bundle args = new Bundle();

        MoreFragment fragment = new MoreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
//        binding.setViewModel(viewModel);
        subscribeUI(viewModel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentMoreBinding moreBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false);
        moreBinding.setHandler(this);
        binding = new AutoClearValue<>(this, moreBinding);
        outputFile = new File(MoreFragment.this.getActivity().getExternalFilesDir(null),
                System.currentTimeMillis() + ".jpg");
        imgUri = Uri.fromFile(outputFile);
        return moreBinding.getRoot();

    }

    private void subscribeUI(HomeViewModel homeViewModel) {
        reportMesh = new ReportMesh();
        homeViewModel.defaultMeshObserver.observe(this, new Observer<DefaultMesh>() {
            @Override
            public void onChanged(@Nullable DefaultMesh defaultMesh) {
                binding.get().setHomeIcon(defaultMesh.aijiaIcon);
                binding.get().setName(defaultMesh.aijiaName);
            }
        });
    }

    public void handleClick(View v) {
        switch (v.getId()) {
            case R.id.portrait://点击头像
//                show();
                break;
            case R.id.btn_scene:
//                GroupSceneActivity.start(getActivity(), GroupSceneActivity.ACTION_SCENE_LIST);
            {
                Intent intent = new Intent(getActivity(), GroupSceneActivity.class);
                intent.putExtra("action", GroupSceneActivity.ACTION_SCENE_LIST);
                startActivityForResult(intent, 10);
            }
            break;
            case R.id.btn_clock:

            ClockActivity.start(getActivity(),ClockActivity.ACTION_CLOCK_LIST,null);
            break;
            case R.id.about_us: {
                Intent intent = new Intent(getActivity(), UserActivity.class);
                intent.putExtra("action", UserActivity.ACTION_ABOUT_US);
                startActivity(intent);
            }
            break;
            case R.id.feed_back: {
                Intent intent = new Intent(getActivity(), UserActivity.class);
                intent.putExtra("action", UserActivity.ACTION_FEED_BACK);
                startActivity(intent);
            }
            break;
            case R.id.setting: {
                Intent intent = new Intent(getActivity(), UserActivity.class);
                intent.putExtra("action", UserActivity.ACTION_SETTING);
                //有可能退出
                startActivityForResult(intent, 0);
            }
            break;

        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
//            getActivity().finish();
//        }
//    }


    Dialog dialog;

    public void show() {
        dialog = new Dialog(MoreFragment.this.getActivity(), R.style.ActionSheetDialogStyle);
        //填充对话框的布局
        View inflate = LayoutInflater.from(MoreFragment.this.getActivity()).inflate(R.layout.dialog_layout, null);
        //初始化控件
        inflate.findViewById(R.id.choosePhoto).setOnClickListener(this);
        inflate.findViewById(R.id.takePhoto).setOnClickListener(this);
        inflate.findViewById(R.id.btn_cancel).setOnClickListener(this);
        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        if (dialogWindow == null) {
            return;
        }
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 20;//设置Dialog距离底部的距离
        //将属性设置给窗体
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
    }

    // 图片路径
    private File outputFile;
    // 图片Uri
    private Uri imgUri;
    private String path = null;

    //Activity 不能有这个方法 否则会拦截
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

        //用户退出
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            getActivity().finish();
        }

        if (requestCode == 1) {
            if (null != data) {
                Uri uri = data.getData();
                Cursor cursor = MoreFragment.this.getActivity().getContentResolver().query(uri, null, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String path = null;
                if (cursor != null && cursor.moveToFirst()) {
                    path = "file://" + cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                }
                startPhotoZoom(Uri.parse(path), 3);
            }

        } else if (requestCode == 2) {
            try {
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inSampleSize = 8;
                /**
                 * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转
                 */
                int degree = readPictureDegree(outputFile.getAbsolutePath());
                Bitmap cameraBitmap = BitmapFactory.decodeFile(outputFile.getAbsolutePath(), bitmapOptions);
                Bitmap bitmap = cameraBitmap;
                /**
                 * 把图片旋转为正的方向
                 */
                bitmap = rotaingImageView(degree, bitmap);
            } catch (Exception e) {

            }
            // 裁剪拍照的图片
            startPhotoZoom(imgUri, 4);
        }
        // 裁剪拍照的图片
        else if (requestCode == 3) {
            setImg(data);
        } else if (requestCode == 4) {
            setImg(data);
        } else if (requestCode == 10&&resultCode==Activity.RESULT_OK) {
            Log.d(TAG, "get new");
            viewModel.sceneListRequest.setValue(1);
        }
    }

    private void setImg(Intent data) {
        Bitmap bitmap2 = null;
        if (null == data.getData()) {
            Bundle extras = data.getExtras();
            bitmap2 = (Bitmap) extras.get("data");
        } else {
            bitmap2 = ImageUtil.getBitmapFromUri(MoreFragment.this.getActivity(), Uri.parse(data.getData().toString()));
        }
        File file_upload = SDCardUtils.createPrivatePhotoFile(MoreFragment.this.getActivity(), System.currentTimeMillis() + ".png");
        ImageUtil.compressToFile(bitmap2, file_upload);
        binding.get().portrait.setImageBitmap(bitmap2);
        reportMesh.homeIcon = file_upload;
    }


    /**
     * 裁剪图片
     *
     * @param uri
     */
    private String cropImgPath;

    public void startPhotoZoom(Uri uri, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");//  crop=true 有这句才能出来最后的裁剪页面.
        intent.putExtra("aspectX", 1);// 这两项为裁剪框的比例.
        intent.putExtra("aspectY", 1);// x:y=1:1
        intent.putExtra("outputX", 200);// 图片输出大小
        intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//返回格式
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImgPath);
        startActivityForResult(intent, requestCode);
    }


    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     * * @param angle
     *
     * @param bitmap
     * @return Bitmap
     */

    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takePhoto:
                dialog.dismiss();
                takePhoto();
                break;
            case R.id.choosePhoto:
                dialog.dismiss();
                pickPhoto();
                break;
            case R.id.btn_cancel:
                dialog.dismiss();
                break;
        }
    }

    //
    // 拍照
    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, 2);
    }

    // 选择相册
    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 1);
    }


}
