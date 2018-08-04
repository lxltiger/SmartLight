package com.example.ledwisdom1.mesh;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ledwisdom1.CallBack;
import com.example.ledwisdom1.Config;
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.adapter.CommonPagerAdapter;
import com.example.ledwisdom1.adapter.InsetDecoration;
import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.api.Resource;
import com.example.ledwisdom1.api.Status;
import com.example.ledwisdom1.databinding.ActivityMeshBinding;
import com.example.ledwisdom1.databinding.LayoutEditBinding;
import com.example.ledwisdom1.databinding.MeshLayoutAddBinding;
import com.example.ledwisdom1.databinding.MeshLayoutDetailBinding;
import com.example.ledwisdom1.databinding.MeshLayoutListBinding;
import com.example.ledwisdom1.fragment.ProduceAvatarFragment;
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.user.Profile;
import com.example.ledwisdom1.utils.DialogManager;
import com.example.ledwisdom1.utils.ImageUtil;
import com.example.ledwisdom1.utils.RequestCreator;
import com.example.ledwisdom1.utils.SDCardUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ledwisdom1.example.com.zxinglib.camera.QRCodeUtil;

/**
 * 蓝牙网络控制UI
 * 包括mesh列表
 * 添加Mesh 需要加参数是否默认  返回结果需要更详细 更新profile
 * 修改mesh
 */
public class MeshActivity extends AppCompatActivity implements CallBack, View.OnClickListener, ProduceAvatarFragment.Listener {
    private static final String TAG = MeshActivity.class.getSimpleName();
    public static final String ACTION_ADD_MESH = "action_add_mesh";
    public static final String ACTION_MESH_DETAIL = "action_mesh_detail";
    public static final String ACTION_MESH_LIST = "action_mesh_list";
    public static final int TYPE_EDIT_NAME = 0;
    public static final int TYPE_EDIT_ACCOUNT = 1;
    public static final int TYPE_EDIT_PSW = 2;

    private DialogManager dialogManager;
    private ReportMesh  reportMesh = new ReportMesh();
    private MeshViewModel viewModel;
    private ActivityMeshBinding binding;
    private MeshLayoutAddBinding meshLayoutAddBinding;
    private MeshLayoutDetailBinding meshLayoutDetailBinding;
    private LayoutEditBinding editBinding;

    private MeshAdapter myMeshAdapter;
    private MeshAdapter friendMeshAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mesh);

        LayoutInflater inflater = LayoutInflater.from(this);
        ViewGroup container = null;
        meshLayoutAddBinding = DataBindingUtil.inflate(inflater, R.layout.mesh_layout_add, container, false);
        meshLayoutAddBinding.setHandler(this);

        meshLayoutDetailBinding = DataBindingUtil.inflate(inflater, R.layout.mesh_layout_detail, container, false);
        meshLayoutDetailBinding.setHandler(this);

        MeshLayoutListBinding meshLayoutListBinding = DataBindingUtil.inflate(inflater, R.layout.mesh_layout_list, container, false);
        meshLayoutListBinding.setHandler(this);

        editBinding = DataBindingUtil.inflate(inflater, R.layout.layout_edit, container, false);
        editBinding.setHandler(this);

        List<View> viewList = new ArrayList<>();
        viewList.add(meshLayoutAddBinding.getRoot());
        viewList.add(meshLayoutListBinding.getRoot());
        viewList.add(meshLayoutDetailBinding.getRoot());
        viewList.add(editBinding.getRoot());
        CommonPagerAdapter pagerAdapter = new CommonPagerAdapter(viewList);
        binding.viewPager.setAdapter(pagerAdapter);

        viewModel = ViewModelProviders.of(this).get(MeshViewModel.class);
        meshLayoutAddBinding.setViewModel(viewModel);
        meshLayoutDetailBinding.setViewModel(viewModel);
        meshLayoutListBinding.setViewModel(viewModel);

        myMeshAdapter = new MeshAdapter(onMeshListener);
        friendMeshAdapter = new MeshAdapter(onMeshListener);

        meshLayoutListBinding.myMeshes.setLayoutManager(new GridLayoutManager(this, 3));
        meshLayoutListBinding.myMeshes.setAdapter(myMeshAdapter);
        meshLayoutListBinding.myMeshes.addItemDecoration(new InsetDecoration(this));

        meshLayoutListBinding.friendMeshes.setLayoutManager(new GridLayoutManager(this, 3));
        meshLayoutListBinding.friendMeshes.setAdapter(friendMeshAdapter);
        meshLayoutListBinding.myMeshes.addItemDecoration(new InsetDecoration(this));

        subscribeUI(viewModel);
        handleNavigate();

        outputFile = new File(this.getExternalFilesDir(null),
                System.currentTimeMillis() + ".jpg");
        imgUri = Uri.fromFile(outputFile);
    }


    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void handleNavigate() {
        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getStringExtra("action");
            switch (action) {
                case ACTION_ADD_MESH:
                    binding.viewPager.setCurrentItem(0);
                    break;
                case ACTION_MESH_DETAIL:
                    binding.viewPager.setCurrentItem(2);
                    break;
                case ACTION_MESH_LIST:
                    binding.viewPager.setCurrentItem(1);
                    //请求mesh列表
                    viewModel.pagerNo.setValue(1);
                    break;
            }
        }
    }


    private void subscribeUI(MeshViewModel viewModel) {
        dialogManager = new DialogManager(this);


        viewModel.defaultMeshObserver.observe(this, defaultMesh -> {
            defaultMesh.aijiaIcon = Config.IMG_PREFIX.concat(defaultMesh.aijiaIcon);
            meshLayoutDetailBinding.setMesh(defaultMesh);
            reportMesh.homeName=defaultMesh.aijiaName;
            String sharetext = RequestCreator.createShareMeshCode(defaultMesh.id, null, defaultMesh.creater);
            Bitmap bitmap = QRCodeUtil.createQRCode(sharetext, 300, 300);
            if (bitmap != null) {
                meshLayoutDetailBinding.ivTdCode.setImageBitmap(bitmap);
            }
        });
//        mesh添加结果监听  更新profile中mesh
        viewModel.addMeshObserver.observe(this, resource -> {
            //控制进度条的可见性
            binding.setResource(resource);
            if (Status.SUCCESS == resource.status) {
                finish();
            } else if (Status.ERROR == resource.status) {
                showToast(resource.message);
            }
        });

//        将结果分成两部分
        viewModel.meshList.observe(this, resource -> {
            binding.setResource(resource);
            if (Status.SUCCESS == resource.status) {
                List<Mesh> data = resource.data;
                Profile profile = viewModel.profile.getValue();
                if (profile != null) {
                    List<Mesh> mine = new ArrayList<>();
                    List<Mesh> friend = new ArrayList<>();
                    for (Mesh mesh : data) {
                        if (mesh.isMyMesh(profile.userId)) {
                            mine.add(mesh);
                        } else {
                            friend.add(mesh);
                        }
                    }
                    myMeshAdapter.addMeshes(mine);
                    friendMeshAdapter.addMeshes(friend);
                } else {
                    Log.d(TAG, "subscribeUI: profile is null");
                }
            } else if (Status.ERROR == resource.status) {
                showToast(resource.message);
            }

        });

        viewModel.setDefaultMeshObserver.observe(this, resource -> {
            binding.setResource(resource);
            if (Status.SUCCESS == resource.status) {
                myMeshAdapter.changeDefaultMesh(current);
                friendMeshAdapter.changeDefaultMesh(current);
                finish();
            } else if (Status.ERROR == resource.status) {
                showToast(resource.message);
            }

        });

        viewModel.deleteMeshObserver.observe(this, new Observer<Resource<Boolean>>() {
            @Override
            public void onChanged(@Nullable Resource<Boolean> resource) {
                binding.setResource(resource);
                if (Status.SUCCESS == resource.status) {
                    myMeshAdapter.removeMesh(current);
                    friendMeshAdapter.removeMesh(current);
                } else if (Status.ERROR == resource.status) {
                    showToast(resource.message);
                }

            }
        });

        viewModel.modifyMeshObserver.observe(this, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                if (apiResponse.isSuccessful()) {
                    if (apiResponse.body.succeed()) {
                        finish();
                    }
                    showToast(apiResponse.body.resultMsg);
                } else {
                    showToast(apiResponse.errorMsg);
                }
            }
        });
    }

    @Override
    public void handleClick(View v) {
        switch (v.getId()) {
            case R.id.avatar:
//                show();
                ProduceAvatarFragment.newInstance().show(getSupportFragmentManager(), ProduceAvatarFragment.TAG);
                break;
            case R.id.user_name:
                viewModel.type.set(MeshActivity.TYPE_EDIT_NAME);
                dialogManager.showDialog(MeshDialog.TAG, MeshDialog.newInstance());
                break;
            case R.id.account:
                viewModel.type.set(MeshActivity.TYPE_EDIT_ACCOUNT);
                dialogManager.showDialog(MeshDialog.TAG, MeshDialog.newInstance());
                break;
            case R.id.password:
                viewModel.type.set(MeshActivity.TYPE_EDIT_PSW);
                dialogManager.showDialog(MeshDialog.TAG, MeshDialog.newInstance());
                break;
            case R.id.iv_back:
                int currentItem = binding.viewPager.getCurrentItem();
                if (currentItem == 3) {
                    binding.viewPager.setCurrentItem(2);
                } else {
                    finish();
                }
                break;
            case R.id.add_mesh:
                binding.viewPager.setCurrentItem(0);
                break;
            case R.id.modify_mesh:
                ProduceAvatarFragment.newInstance().show(getSupportFragmentManager(), ProduceAvatarFragment.TAG);
                break;
            case R.id.confirm:
                int currentItem2 = binding.viewPager.getCurrentItem();
                if (currentItem2 == 0) {
                    if (null == reportMesh.homeIcon) {
                        Toast.makeText(this, "还没有选择头像", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    viewModel.meshObserver.setValue(reportMesh);

                } else if (currentItem2 == 3) {
                    String name = editBinding.getName();
                    if (TextUtils.isEmpty(name) || name.length() > 10) {
                        editBinding.content.setError("名称在1到10个字符之间");
                        editBinding.content.requestFocus();
                        return;
                    }
                    binding.viewPager.setCurrentItem(2);
//                    DefaultMesh mesh = meshLayoutDetailBinding.getMesh();
//                    mesh.aijiaName=name;
//                    meshLayoutDetailBinding.setMesh(mesh);
                    reportMesh.homeName = name;
                    meshLayoutDetailBinding.modifyName.setValue(name);

                }
                break;
            case R.id.modify_name:
                editBinding.setName(meshLayoutDetailBinding.getMesh().aijiaName);
                binding.viewPager.setCurrentItem(3);
                break;
            case R.id.confirm_update:
                if (null == reportMesh.homeIcon) {
                    Toast.makeText(this, "还没有选择头像", Toast.LENGTH_SHORT).show();
                    return;
                }
//                reportMesh.meshName = meshLayoutDetailBinding.getMesh().id;
                viewModel.modifymeshRequest.setValue(reportMesh);
                break;
            case R.id.clear:
                editBinding.setName("");

                break;
        }
    }

    private Mesh current;
    private boolean deleteMode = false;
    private OnMeshListener onMeshListener = new OnMeshListener() {
        @Override
        public void onItemClick(View view, Mesh meshBean) {
            current = meshBean;
            if (deleteMode) {
                showDialog();
            } else {
                viewModel.setDefaultMeshRequest.setValue(meshBean.getId());
            }
        }

        @Override
        public void onDeleteClick(Mesh meshBean) {
            current = meshBean;
            showDialog();
        }


        @Override
        public boolean onItemLongClick(Mesh light) {
            deleteMode = true;
            myMeshAdapter.showDeleteIcon(true);
            friendMeshAdapter.showDeleteIcon(true);
            return true;
        }
    };

    private void showDialog() {
        AlertDialog.Builder buidler = new AlertDialog.Builder(this).setTitle("确认删除")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.deleteMeshRequest.setValue(current);
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


    Dialog dialog;

    public void show() {
        dialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);
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

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) return;

        if (requestCode == 1) {
            // ??????
            if (null != data) {
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
//                File tempFile=new File(cursor.getString(column_index));
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

            }
            // 裁剪拍照的图片
            startPhotoZoom(imgUri, 4);
        }
        // 裁剪拍照的图片
        else if (requestCode == 3) {
//            setPicToView(data, requestCode);
            setImg(data);
        } else if (requestCode == 4) {
            setImg(data);
        } else if (requestCode == 0) {
            setImg(data);
        }

    }*/

    private void setImg(Intent data) {
        Bitmap bitmap2 = null;
        if (null == data.getData()) {
            Bundle extras = data.getExtras();
            bitmap2 = (Bitmap) extras.get("data");
        } else {
            bitmap2 = ImageUtil.getBitmapFromUri(MeshActivity.this, Uri.parse(data.getData().toString()));
        }
        File file_upload = SDCardUtils.createPrivatePhotoFile(MeshActivity.this, System.currentTimeMillis() + ".png");
        ImageUtil.compressToFile(bitmap2, file_upload);
        meshLayoutAddBinding.avatar.setImageBitmap(bitmap2);
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
    public void onBackPressed() {
        if (deleteMode) {
            deleteMode = false;
            myMeshAdapter.showDeleteIcon(false);
            friendMeshAdapter.showDeleteIcon(false);
        } else {
            super.onBackPressed();
        }

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


    @Override
    public void onItemClicked(File file) {
        int currentItem = binding.viewPager.getCurrentItem();
        if (currentItem == 2) {
            reportMesh.homeIcon = file;
//        DefaultMesh mesh = meshLayoutDetailBinding.getMesh();
//        mesh.aijiaIcon = file.getAbsolutePath();
//        meshLayoutDetailBinding.setMesh(mesh);
            meshLayoutDetailBinding.modifyMesh.setValue(file.getAbsolutePath());
        } else if (currentItem == 0) {
            Glide.with(this).load(file).into(meshLayoutAddBinding.avatar);
            reportMesh.homeIcon = file;
        }
    }
}
