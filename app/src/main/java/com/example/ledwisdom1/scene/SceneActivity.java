package com.example.ledwisdom1.scene;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.home.entity.Group;
import com.example.ledwisdom1.utils.NavigatorController;

/**
 * 场景和情景的页面
 */
public class SceneActivity extends AppCompatActivity {

    public static final String ACTION_GROUP = "group";

    private NavigatorController navigatorController;
    private SceneViewModel viewModel;

    public static Intent newIntent(Context context, String action, Group group) {
        Intent intent = new Intent(context,SceneActivity.class);
        intent.putExtra("action", action);
        intent.putExtra("group", group);

        return  intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);
        viewModel = ViewModelProviders.of(this).get(SceneViewModel.class);
        navigatorController = new NavigatorController(this, R.id.fl_container);
        if (savedInstanceState == null) {
            handleNavigate();
        }

    }

    private void handleNavigate() {
        Intent intent = getIntent();
        String action = "";
        if (intent != null) {
            action = intent.getStringExtra("action");
            switch (action) {
                case ACTION_GROUP:
                    Group group = intent.getParcelableExtra("group");
                    viewModel.group.setValue(group);

                    navigatorController.navigateToAddGroup();
                    break;

            }
        }
    }

}
