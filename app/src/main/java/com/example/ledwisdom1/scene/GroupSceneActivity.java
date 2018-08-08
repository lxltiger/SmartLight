package com.example.ledwisdom1.scene;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.home.entity.Group;
import com.example.ledwisdom1.common.NavigatorController;

/**
 * 场景和情景的页面容器
 */
public class GroupSceneActivity extends AppCompatActivity {
    private static final String TAG = GroupSceneActivity.class.getSimpleName();
    public static final String ACTION_GROUP = "action_group";
    public static final String ACTION_SCENE = "action_scene";
    public static final String ACTION_SCENE_LIST = "action_scene_list";
    public static final String ACTION_EDIT_NAME = "action_edit_name";
    public static final String ACTION_LAMP_LIST = "action_lamp_list";
    public static final String ACTION_SELECTED_LAMP = "action_selected_lamp";

    private NavigatorController navigatorController;
    /**
     * 情景列表发生改变的标志
     */
    private boolean changed = false;

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

    public static void start(Context context, String action, Scene scene) {
        Intent intent = new Intent(context, GroupSceneActivity.class);
        intent.putExtra("action", action);
        intent.putExtra("scene", scene);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);
        navigatorController = new NavigatorController(this, R.id.fl_container);
        if (savedInstanceState == null) {
            handleNavigate(getIntent());
        }

    }


    private void handleNavigate(Intent intent) {
        String action = "";
        if (intent != null) {
            action = intent.getStringExtra("action");
            switch (action) {
                case ACTION_GROUP: {
                    Group group = intent.getParcelableExtra("group");
                    navigatorController.navigateToGroup(group);
                }
                break;
//                    情景列表
                case ACTION_SCENE_LIST:
                    navigatorController.navigateToSceneList();
                    break;
                case ACTION_SCENE: {
                    Scene scene = intent.getParcelableExtra("scene");
                    navigatorController.navigateToScene(scene);
                }
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


    /*通知情景发生变化，首页需要请求新的数据*/
    private void sendResult() {
        Log.d(TAG, "onStop: ok");
        setResult(Activity.RESULT_OK);

    }

}
