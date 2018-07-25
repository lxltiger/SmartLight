package com.example.ledwisdom1.utils;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.ledwisdom1.clock.Clock;
import com.example.ledwisdom1.clock.ClockFragment;
import com.example.ledwisdom1.clock.ClockListFragment;
import com.example.ledwisdom1.device.AddDeviceFragment;
import com.example.ledwisdom1.device.AddHubFragment;
import com.example.ledwisdom1.device.AddLampFragment;
import com.example.ledwisdom1.device.GroupSceneControlFragment;
import com.example.ledwisdom1.device.LightSettingFragment;
import com.example.ledwisdom1.home.DeviceFragment;
import com.example.ledwisdom1.home.GroupListFragment;
import com.example.ledwisdom1.home.HomeActivity;
import com.example.ledwisdom1.home.HomeFragment;
import com.example.ledwisdom1.home.MoreFragment;
import com.example.ledwisdom1.mesh.MeshFragment;
import com.example.ledwisdom1.scene.EditFragment;
import com.example.ledwisdom1.scene.GroupFragment;
import com.example.ledwisdom1.scene.LampListDialogFragment;
import com.example.ledwisdom1.scene.SceneFragment;
import com.example.ledwisdom1.scene.SceneListFragment;
import com.example.ledwisdom1.scene.SelectedLampListFragment;
import com.example.ledwisdom1.user.AboutUSFragment;
import com.example.ledwisdom1.user.FeedBackFragment;
import com.example.ledwisdom1.user.SettingFragment;
import com.example.ledwisdom1.user.UserFragment;

/**
 * 页面跳转控制
 */
public class NavigatorController {

    private FragmentManager fm;
    private final int container;

    public NavigatorController(AppCompatActivity activity, int container) {
        this.fm = activity.getSupportFragmentManager();
        this.container = container;
    }


    public void navigateToLogin() {
        fm.beginTransaction()
                .replace(container, UserFragment.newInstance(), UserFragment.TAG)
//                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void navigateToSetting() {
        fm.beginTransaction()
                .replace(container, SettingFragment.newInstance(), SettingFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToAboutUs() {
        fm.beginTransaction()
                .replace(container, new AboutUSFragment(), "AboutUSFragment")
                .commitAllowingStateLoss();
    }

    public void navigateToFeedBack() {
        fm.beginTransaction()
                .replace(container, new FeedBackFragment(), "FeedBackFragment")
                .commitAllowingStateLoss();
    }

    public void navigateToHome() {
        fm.beginTransaction()
                .replace(container, HomeFragment.newInstance(), HomeFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToDevice() {
        fm.beginTransaction()
                .replace(container, DeviceFragment.newInstance(), DeviceFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToAddDevice() {
        fm.beginTransaction()
                .replace(container, AddDeviceFragment.newInstance(), AddDeviceFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToAddLamp() {
        fm.beginTransaction()
                .replace(container, AddLampFragment.newInstance(), AddLampFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToAddHub() {
        fm.beginTransaction()
                .replace(container, AddHubFragment.newInstance(), AddHubFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToLampSetting(int meshAddress, int brightness, int status) {
        fm.beginTransaction()
                .replace(container, LightSettingFragment.newInstance(meshAddress, brightness, status), LightSettingFragment.TAG)
                .commitAllowingStateLoss();
    }


    public void navigateToGroupSceneControl(int meshAddress, int brightness, int status) {
        fm.beginTransaction()
                .replace(container, GroupSceneControlFragment.newInstance(meshAddress, brightness, status), GroupSceneControlFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToGroup() {
        fm.beginTransaction()
                .replace(container, GroupListFragment.newInstance(), GroupListFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToScene() {
        fm.beginTransaction()
                .replace(container, SceneListFragment.newInstance(), SceneListFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToClockList() {
        fm.beginTransaction()
                .replace(container, ClockListFragment.newInstance(), ClockListFragment.TAG)
                .commitAllowingStateLoss();
    }

    /*闹钟的添加和编辑*/
    public void navigateToClock(Clock clock) {
        fm.beginTransaction()
                .replace(container, ClockFragment.newInstance(clock), ClockFragment.TAG)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void navigateToAddGroup() {
        fm.beginTransaction()
                .replace(container, GroupFragment.newInstance(), GroupFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToEditName() {
        fm.beginTransaction()
                .replace(container, EditFragment.newInstance(), EditFragment.TAG)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void navigateToLampList() {
        fm.beginTransaction()
                .replace(container, LampListDialogFragment.newInstance(), LampListDialogFragment.TAG)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void navigateToSelectedLamps() {
        fm.beginTransaction()
                .replace(container, SelectedLampListFragment.newInstance(), SelectedLampListFragment.TAG)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void navigateToAddScene() {
        fm.beginTransaction()
                .replace(container, SceneFragment.newInstance(), SceneFragment.TAG)
//                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void navigateToMore() {
        fm.beginTransaction()
                .replace(container, MoreFragment.newInstance(), MoreFragment.TAG)
                .commitAllowingStateLoss();
    }

    //添加mesh
  /*  public void navigateToAddMesh() {
        fm.beginTransaction()
                .replace(container, AddMeshFragment.newInstance(), AddMeshFragment.TAG)
                .commitAllowingStateLoss();
    }*/

    //显示mesh列表
    public void navigateToMeshList() {
        fm.beginTransaction()
                .replace(container, MeshFragment.newInstance(), MeshFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToMain(AppCompatActivity activity) {
        activity.startActivity(new Intent(activity, HomeActivity.class));
        activity.finish();
    }

    //    处理返回键
    public boolean navigateToLast() {
        UserFragment fragment = (UserFragment) fm.findFragmentByTag(UserFragment.TAG);
        if (fragment != null) {
            return fragment.handleBackPressed();
        }
        return false;
    }

}
