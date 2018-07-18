package com.example.ledwisdom1.scene;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.utils.NavigatorController;

/**
 * 场景和情景的页面
 */
public class SceneActivity extends AppCompatActivity {


    private NavigatorController navigatorController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);
        navigatorController = new NavigatorController(this, R.id.fl_container);
        if (savedInstanceState == null) {
            navigatorController.navigateToAddGroup();
        }

    }

}
