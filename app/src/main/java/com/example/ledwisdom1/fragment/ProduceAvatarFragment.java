package com.example.ledwisdom1.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.utils.ImageUtil;
import com.example.ledwisdom1.utils.SDCardUtils;

import java.io.File;
import java.io.IOException;

/**
 *
 * 用来制作头像
 */
public class ProduceAvatarFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    public static final String TAG =ProduceAvatarFragment.class.getSimpleName();
    public static final int REQUEST_PICK_PHOTO = 1;
    public static final int REQUEST_TAKE_PHOTO = 2;
    public static final int REQUEST_CROP_PHOTO = 3;
    private Listener mListener;

    public static ProduceAvatarFragment newInstance() {
        final ProduceAvatarFragment fragment = new ProduceAvatarFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.choosePhoto).setOnClickListener(this);
        view.findViewById(R.id.takePhoto).setOnClickListener(this);
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (Listener) parent;
        } else {
            mListener = (Listener) context;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takePhoto:
                takePhoto();
                break;
            case R.id.choosePhoto:
                pickPhoto();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }

    // 选择相册
    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_PICK_PHOTO);
    }

    // 图片路径
    private File outputFile;
    // 图片Uri
    private Uri imgUri;
    // 拍照
    private void takePhoto() {
        outputFile = new File(getActivity().getExternalFilesDir(null),System.currentTimeMillis() + ".jpg");
        imgUri = Uri.fromFile(outputFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
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

    private void handleCropPhoto(Intent data) {
        Bitmap bitmap = null;
        if (null == data.getData()) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
        } else {
            bitmap = ImageUtil.getBitmapFromUri(getActivity(), Uri.parse(data.getData().toString()));
        }
        File file_upload = SDCardUtils.createPrivatePhotoFile(getActivity(), System.currentTimeMillis() + ".png");
        ImageUtil.compressToFile(bitmap,file_upload);
        if (mListener != null) {
            mListener.onItemClicked(file_upload);
        }
        dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
//                从相册选择
                case REQUEST_PICK_PHOTO:
                    if (null != data) {
                        Uri uri = data.getData();
                        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        String path = null;
                        if (cursor != null && cursor.moveToFirst()) {
                            path = "file://" + cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                        }
                        startPhotoZoom(Uri.parse(path), REQUEST_CROP_PHOTO);
                    }
                    break;
                case REQUEST_TAKE_PHOTO:

                   /* try {
                        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                        bitmapOptions.inSampleSize = 8;
                        *//**
                         * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转
                         *//*
                        int degree = readPictureDegree(outputFile.getAbsolutePath());
                        Bitmap cameraBitmap = BitmapFactory.decodeFile(outputFile.getAbsolutePath(), bitmapOptions);
                        Bitmap bitmap = cameraBitmap;
                        *//**
                         * 把图片旋转为正的方向
                         *//*
                        bitmap = rotaingImageView(degree, bitmap);
                    } catch (Exception e) {

                    }*/
                    // 裁剪拍照的图片
                    startPhotoZoom(imgUri, REQUEST_CROP_PHOTO);
                    break;
//                    剪裁图片
                case REQUEST_CROP_PHOTO:
                    handleCropPhoto(data);
                    break;


            }

        }
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
        return Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
    public interface Listener {
        void onItemClicked(File file);
    }


}
