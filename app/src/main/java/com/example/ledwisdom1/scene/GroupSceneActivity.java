package com.example.ledwisdom1.scene;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.ledwisdom1.Config;
import com.example.ledwisdom1.R;
import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.home.entity.Group;
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.sevice.TelinkLightService;
import com.example.ledwisdom1.utils.BindingAdapters;
import com.example.ledwisdom1.utils.NavigatorController;

import java.util.List;

import static com.example.ledwisdom1.utils.ToastUtil.showToast;

/**
 * 场景和情景的页面容器
 */
public class GroupSceneActivity extends AppCompatActivity {
    private static final String TAG = GroupSceneActivity.class.getSimpleName();
    public static final String ACTION_ADD_GROUP = "action_add_group";
    public static final String ACTION_ADD_SCENE = "action_add_scene";
    public static final String ACTION_SCENE_LIST = "action_scene_list";
    public static final String ACTION_EDIT_NAME = "action_edit_name";
    public static final String ACTION_LAMP_LIST = "action_lamp_list";
    public static final String ACTION_SELECTED_LAMP = "action_selected_lamp";

    private NavigatorController navigatorController;
    private GroupSceneViewModel viewModel;
    /**
     * 情景列表发生改变的标志
     */
    private boolean changed=false;

    public static Intent newIntent(Context context, String action, Group group) {
        Intent intent = new Intent(context, GroupSceneActivity.class);
        intent.putExtra("action", action);
        intent.putExtra("group", group);
        return intent;
    }

    public static void start(Context context, String action) {
        Intent intent = new Intent(context, GroupSceneActivity.class);
        intent.putExtra("action", action);
        context.startActivity(intent);
    }

    public static void start(Context context, String action,Scene scene) {
        Intent intent = new Intent(context, GroupSceneActivity.class);
        intent.putExtra("action", action);
        intent.putExtra("scene", scene);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);
        viewModel = ViewModelProviders.of(this).get(GroupSceneViewModel.class);
        navigatorController = new NavigatorController(this, R.id.fl_container);
        if (savedInstanceState == null) {
            handleNavigate(getIntent());
        }

        subscribeUI(viewModel);
        //当前mesh下的灯具
        viewModel.lampListRequest.setValue(1);

    }

    private void subscribeUI(GroupSceneViewModel viewModel) {
//        删除场景
        viewModel.deleteGroupObserver.observe(this, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                if (apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    showToast(apiResponse.body.resultMsg);
                    if (viewModel.groupSceneRequest.isGroup) {
                        finish();
                    } else {
                        sendResult();
                        navigatorController.navigateToScene();
                    }
                } else {
                    showToast("删除失败");
                }
            }
        });

        //情景添加成功后添加灯具
        viewModel.addGroupObserver.observe(this, new Observer<ApiResponse<AddGroupSceneResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<AddGroupSceneResult> apiResponse) {
                if (null != apiResponse && apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    AddGroupSceneResult groupSceneResult = apiResponse.body;
                    if (groupSceneResult.groupId > 0) {
                        viewModel.groupSceneRequest.groupId = groupSceneResult.id;
                        viewModel.groupSceneRequest.groupAddress = groupSceneResult.groupId;
                        viewModel.addDeviceToGroupSceneRequest.setValue(viewModel.groupSceneRequest);
                    } else if (groupSceneResult.sceneId > 0) {
                        viewModel.groupSceneRequest.sceneId = groupSceneResult.id;
                        viewModel.groupSceneRequest.groupAddress = groupSceneResult.sceneId;
                        viewModel.addDeviceToGroupSceneRequest.setValue(viewModel.groupSceneRequest);
                    }
                } else {
                    viewModel.isLoading.set(false);
                    showToast("创建失败");
                }
            }
        });

        viewModel.addDeviceToGroupSceneObserver.observe(this, new Observer<ApiResponse<RequestResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<RequestResult> apiResponse) {
                viewModel.isLoading.set(false);
                if (apiResponse != null && apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    List<Lamp> lamps = viewModel.groupSceneLamps;
                    for (Lamp lamp : lamps) {
                        allocDeviceGroup(viewModel.groupSceneRequest.groupAddress, lamp.getDevice_id(), BindingAdapters.LIGHT_SELECTED == lamp.lampStatus.get());
                    }
                    showToast(apiResponse.body.resultMsg);
                    if (viewModel.groupSceneRequest.isGroup) {
                        finish();
                    } else {
                        sendResult();
                        navigatorController.navigateToScene();
                    }
                } else {
                    showToast("添加灯具失败");

                }
            }
        });
        viewModel.updateGroupObserver.observe(this, new Observer<ApiResponse<AddGroupSceneResult>>() {
            @Override
            public void onChanged(@Nullable ApiResponse<AddGroupSceneResult> apiResponse) {
                viewModel.isLoading.set(false);
                if (apiResponse.isSuccessful() && apiResponse.body.succeed()) {
                    showToast(apiResponse.body.resultMsg);
                    if (viewModel.groupSceneRequest.isGroup) {
                        finish();
                    } else {
                        sendResult();
                        navigatorController.navigateToScene();
                    }
                } else {
                    showToast("更新失败");
                }
            }
        });
    }

    private void handleNavigate(Intent intent) {
        String action = "";
        if (intent != null) {
            action = intent.getStringExtra("action");
            switch (action) {
                case ACTION_ADD_GROUP: {
                    Group group = intent.getParcelableExtra("group");
                    viewModel.groupSceneRequest.isGroup = true;
                    if (group != null) {
                        viewModel.name = group.getName();
                        viewModel.imagePath = Config.IMG_PREFIX.concat(group.getIcon());
                        viewModel.MODE_ADD = false;
                        viewModel.groupSceneId.setValue(group.getId());
                        viewModel.groupSceneRequest.groupId = group.getId();
                    }else{
                        viewModel.MODE_ADD = true;
                    }
                    navigatorController.navigateToAddGroup();
                }
                break;
//                    情景列表
                case ACTION_SCENE_LIST:
                    navigatorController.navigateToScene();
                    break;
                case ACTION_ADD_SCENE: {
                    Scene scene = intent.getParcelableExtra("scene");
                    viewModel.groupSceneRequest.isGroup = false;
                    if (scene != null) {
                        viewModel.name = scene.getName();
                        viewModel.imagePath = Config.IMG_PREFIX.concat(scene.getIcon());
                        viewModel.MODE_ADD = false;
                        viewModel.groupSceneId.setValue(scene.getId());
                        viewModel.groupSceneRequest.sceneId = scene.getId();
                    }else {
                        viewModel.MODE_ADD = true;
                        viewModel.name = "";
                        viewModel.imagePath = "";
                    }
                    navigatorController.navigateToAddScene();
                }
                break;
                case ACTION_EDIT_NAME:
                    navigatorController.navigateToEditName();
                    break;
                case ACTION_LAMP_LIST:
                    navigatorController.navigateToLampList();
                    break;
                case ACTION_SELECTED_LAMP:
                    navigatorController.navigateToSelectedLamps();
                    break;
            }
        }
    }

    /*single top 模式 */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleNavigate(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void sendResult() {
        Log.d(TAG, "onStop: ok");
        setResult(Activity.RESULT_OK);

    }



    /**
     * 添加灯具到场景
     *
     * @param groupAddress
     * @param dstAddress
     * @param add
     */


    private void allocDeviceGroup(int groupAddress, int dstAddress, boolean add) {
        byte opcode = (byte) 0xD7;
        byte[] params = new byte[]{0x01, (byte) (groupAddress & 0xFF),
                (byte) (groupAddress >> 8 & 0xFF)};

        params[0] = (byte) (add ? 0x01 : 0x00);
        TelinkLightService.Instance().sendCommand(opcode, dstAddress, params);
    }
}
