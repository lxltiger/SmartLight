package com.example.ledwisdom1.mesh;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.common.NavigatorController;

/**
 * 蓝牙网络页面
 */
public class MeshActivity2 extends AppCompatActivity {
    public static final String ACTION_ADD_MESH = "action_add_mesh";
    public static final String ACTION_MESH_LIST = "action_mesh_list";
    public static final String ACTION_MESH_DETAIL = "action_mesh_detail";

    private NavigatorController navigatorController;

    public static void start(Context context, String action) {
        Intent intent = new Intent(context, MeshActivity2.class);
        intent.putExtra("action", action);
        context.startActivity(intent);
    }

    public static void start(Context context, String action,DefaultMesh mesh) {
        Intent intent = new Intent(context, MeshActivity2.class);
        intent.putExtra("action", action);
        intent.putExtra("mesh", mesh);
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
                case ACTION_ADD_MESH:
                    navigatorController.navigateToAddMesh();
                    break;
                case ACTION_MESH_LIST:
                    navigatorController.navigateToMeshList();
                    break;
                    case ACTION_MESH_DETAIL:
                        DefaultMesh mesh = intent.getParcelableExtra("mesh");
                    navigatorController.navigateToMeshDetail(mesh);
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

    /**
     * 主要因为mesh列表在删除模式下需要特别处理
     */
    @Override
    public void onBackPressed() {
        AddMeshFragment f = (AddMeshFragment) getSupportFragmentManager().findFragmentByTag(AddMeshFragment.TAG);
        if (f != null) {
            super.onBackPressed();
            return;
        }
        MeshListFragment meshListFragment = (MeshListFragment) getSupportFragmentManager().findFragmentByTag(MeshListFragment.TAG);
        if (meshListFragment != null) {
            meshListFragment.handleBackPressed();
            return;
        }
        super.onBackPressed();
    }
}
