package com.example.ledwisdom1.mesh;


import android.support.v4.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * 添加mesh页面
 */
@Deprecated
public class AddMeshFragment extends Fragment {/*

    public static final String TAG = AddMeshFragment.class.getSimpleName();

    private AutoClearValue<HomeLayoutAddmeshBinding> binding;
    private DialogManager dialogManager;
    private MeshViewModel meshViewModel;
    private ReportMesh reportMesh;
    public static AddMeshFragment newInstance() {
        Bundle args = new Bundle();
        AddMeshFragment fragment = new AddMeshFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public AddMeshFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        HomeLayoutAddmeshBinding meshBinding = DataBindingUtil.inflate(inflater, R.layout.mesh_layout_add, container, false);
        binding = new AutoClearValue<>(this, meshBinding);
        return meshBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dialogManager = new DialogManager(getActivity());
        meshViewModel = ViewModelProviders.of(getActivity()).get(MeshViewModel.class);
        reportMesh = new ReportMesh();
        binding.get().setHandler(this);
        binding.get().setViewModel(meshViewModel);

        meshViewModel.addMeshObserver.observe(this, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> requestResultApiResponse) {

            }
        });

    }


    public void handleClick(View v) {
        switch (v.getId()) {
            case R.id.avatar:
                doChoosePhoto();
                break;
            case R.id.user_name:
                meshViewModel.type.set(MeshActivity.TYPE_EDIT_NAME);
                dialogManager.showDialog(MeshDialog.TAG, MeshDialog.newInstance());
                break;
            case R.id.account:
                meshViewModel.type.set(MeshActivity.TYPE_EDIT_ACCOUNT);
                dialogManager.showDialog(MeshDialog.TAG, MeshDialog.newInstance());
                break;
            case R.id.password:
                meshViewModel.type.set(MeshActivity.TYPE_EDIT_PSW);
                dialogManager.showDialog(MeshDialog.TAG, MeshDialog.newInstance());
                break;
            case R.id.confirm:
                if (null == reportMesh.homeIcon) {
                    Toast.makeText(getActivity(), "还没有选择头像", Toast.LENGTH_SHORT).show();
                    return;
                }

                meshViewModel.meshObserver.setValue(reportMesh);
                break;
        }
    }

    *//*
     * 从相册中选取图片并裁剪
     *//*
    private void doChoosePhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) return;

        switch (requestCode) {
           *//* case Constant.CAMERA:
                Bitmap bitmap = data.getExtras().getParcelable("data");
                Uri uri= ImageUtil.saveBitmap(bitmap, mAvatarTempFile);
                startImageZoom(uri);
                break;*//*
            case 0:
                if (data != null) {
                    Bitmap bitmap2 = data.getParcelableExtra("data");
                    File file_upload = SDCardUtils.createPrivatePhotoFile(getActivity(), System.currentTimeMillis() + ".png");
                    ImageUtil.compressToFile(bitmap2, file_upload);
                    reportMesh.homeIcon=file_upload;
//                    Map<String, File> fileParam = new ArrayMap<>();
//                    fileParam.put("Head_url", file_upload);
//                    updateAvatar(fileParam, file_upload);
                }
                break;
            *//*case Constant.CROP:
                Bitmap bitamp_final=data.getExtras().getParcelable("data");
                File file_upload = SDCardUtils.createPrivatePhotoFile(mContext, System.currentTimeMillis()+".png");
                ImageUtil.compressToFile(bitamp_final, file_upload);
                Map<String, File> fileParam = new ArrayMap<>();
                fileParam.put("Head_url", file_upload);
                updateAvatar(fileParam, file_upload);
                break;*//*

        }
    }
*/}
