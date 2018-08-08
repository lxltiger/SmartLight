package com.example.ledwisdom1.common;

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
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.home.DeviceFragment;
import com.example.ledwisdom1.home.GroupListFragment;
import com.example.ledwisdom1.home.HomeFragment;
import com.example.ledwisdom1.home.MoreFragment;
import com.example.ledwisdom1.home.entity.Group;
import com.example.ledwisdom1.mesh.AddMeshFragment;
import com.example.ledwisdom1.mesh.DefaultMesh;
import com.example.ledwisdom1.mesh.MeshDetailFragment;
import com.example.ledwisdom1.mesh.MeshListFragment;
import com.example.ledwisdom1.scene.EditFragment;
import com.example.ledwisdom1.scene.GroupFragment2;
import com.example.ledwisdom1.scene.LampListDialogFragment;
import com.example.ledwisdom1.scene.Scene;
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

    public void navigateToAddDevice(int type) {
        fm.beginTransaction()
                .replace(container, AddDeviceFragment.newInstance(type), AddDeviceFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToAddLamp() {
        fm.beginTransaction()
                .replace(container, AddLampFragment.newInstance(), AddLampFragment.TAG)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void navigateToAddHub() {
        fm.beginTransaction()
                .replace(container, AddHubFragment.newInstance(), AddHubFragment.TAG)
                .addToBackStack(null)

                .commitAllowingStateLoss();
    }

    public void navigateToLampSetting(Lamp lamp) {
        fm.beginTransaction()
                .replace(container, LightSettingFragment.newInstance(lamp), LightSettingFragment.TAG)
                .commitAllowingStateLoss();
    }


    public void navigateToGroupControl(int meshAddress, int brightness, int status) {
        fm.beginTransaction()
                .replace(container, GroupSceneControlFragment.newInstance(meshAddress, brightness, status), GroupSceneControlFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToGroup() {
        fm.beginTransaction()
                .replace(container, GroupListFragment.newInstance(), GroupListFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToSceneList() {
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

    public void navigateToGroup(Group group) {
        fm.beginTransaction()
                .replace(container, GroupFragment2.newInstance(group), GroupFragment2.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToEditName() {
        fm.beginTransaction()
                .replace(container, EditFragment.newInstance(), EditFragment.TAG)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

//    场景 、情景灯具列表
    public void navigateToLampList() {
        fm.beginTransaction()
                .replace(container, LampListDialogFragment.newInstance(), LampListDialogFragment.TAG)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void navigateToClockLampList() {
        fm.beginTransaction()
                .replace(container, com.example.ledwisdom1.clock.LampListDialogFragment.newInstance(), LampListDialogFragment.TAG)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void navigateToSelectedLamps() {
        fm.beginTransaction()
                .replace(container, SelectedLampListFragment.newInstance(), SelectedLampListFragment.TAG)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

//    闹钟已选择灯具
    public void navigateToClockSelectedLamps() {
        fm.beginTransaction()
                .replace(container, com.example.ledwisdom1.clock.SelectedLampListFragment.newInstance(), SelectedLampListFragment.TAG)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void navigateToScene(Scene scene) {
        fm.beginTransaction()
                .replace(container, SceneFragment.newInstance(scene), SceneFragment.TAG)
                .addToBackStack(null)
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
                .replace(container, MeshListFragment.newInstance(), MeshListFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToMeshDetail(DefaultMesh mesh) {
        fm.beginTransaction()
                .replace(container, MeshDetailFragment.newInstance(mesh), MeshDetailFragment.TAG)
                .commitAllowingStateLoss();
    }

    public void navigateToAddMesh() {
        fm.beginTransaction()
                .replace(container, AddMeshFragment.newInstance(), AddMeshFragment.TAG)
                .addToBackStack(null)
                .commitAllowingStateLoss();
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
