package com.example.ledwisdom1.api;

import android.arch.lifecycle.LiveData;

import com.example.ledwisdom1.device.entity.LampList;
import com.example.ledwisdom1.home.entity.Group;
import com.example.ledwisdom1.home.entity.GroupList;
import com.example.ledwisdom1.home.entity.HubList;
import com.example.ledwisdom1.mesh.AddMeshResult;
import com.example.ledwisdom1.mesh.DefaultMesh;
import com.example.ledwisdom1.mesh.MeshList;
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.scene.AddGroupSceneResult;
import com.example.ledwisdom1.scene.GroupDevice;
import com.example.ledwisdom1.scene.SceneList;
import com.example.ledwisdom1.user.Profile;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;

/**
 */
public interface KimAscendService {
    @POST("user/regist")
    LiveData<ApiResponse<RequestResult>> regist(@Body RequestBody request);

    @POST("user/validate")
    LiveData<ApiResponse<RequestResult>> getAuthCode(@Body RequestBody request);

    @POST("user/checkAccount")
    LiveData<ApiResponse<RequestResult>>  checkAccount(@Body RequestBody request);

    @POST("user/SMSvalidate")
    LiveData<ApiResponse<RequestResult>> SMSvalidate(@Body RequestBody request);

    @POST("user/login")
    LiveData<ApiResponse<Profile>> login(@Body RequestBody request);

    //
    @POST("user/logout")
    LiveData<ApiResponse<RequestResult>> logout();

    @POST("user/resetPassword")
    LiveData<ApiResponse<RequestResult>> resetpassword(@Body RequestBody request);

    @POST("user/setPassword")
    LiveData<ApiResponse<RequestResult>> setPassword(@Body RequestBody request);

    @POST("mesh/meshList/")
    LiveData<ApiResponse<MeshList>> meshList(@Body RequestBody request);

    @POST("mesh/getMeshById")
    LiveData<ApiResponse<DefaultMesh>> meshDetail(@Body RequestBody request);

    @POST("mesh/setDefault")
    LiveData<ApiResponse<RequestResult>> setDefaultMesh(@Body RequestBody request);

    @POST("mesh/shareMesh")
    LiveData<ApiResponse<RequestResult>> shareMesh(@Body RequestBody request);

    @POST("mesh/deleteMesh")
    LiveData<ApiResponse<RequestResult>> deleteMesh(@Body RequestBody request);

    @Multipart
    @POST("mesh/reportBleMesh")
    LiveData<ApiResponse<AddMeshResult>> reportBleMesh(@Part MultipartBody.Part file, @QueryMap Map<String, String> map);

    //    LiveData<ApiResponse<RequestResult>> reportBleMesh(@Header("accessToken") String authorization, @Part MultipartBody.Part file, @Part("json") RequestBody requestBody);
    @Multipart
    @POST("mesh/updateMesh")
    LiveData<ApiResponse<RequestResult>> upateMesh(@Part MultipartBody.Part file, @QueryMap Map<String, String> map);

    @POST("belMesh/deleteLampMeshByMeshAndUser")
    LiveData<Response<Profile>> deleteLampMeshByMeshAndUser(@Body RequestBody request);


    @POST("device/deviceList")
    LiveData<ApiResponse<LampList>> deviceList(@Body RequestBody request);

    //上报灯具
    @POST("device/reportDevice")
    LiveData<ApiResponse<RequestResult>> reportDevice(@Body RequestBody request);

    @POST("gateway/reportLampGateway")
    LiveData<ApiResponse<RequestResult>> reportHub(@Body RequestBody request);

    @POST("gateway/gatewayList")
    LiveData<ApiResponse<HubList>> hubList(@Body RequestBody request);

    //参数 id
    @POST("gateway/deleteGateway")
    LiveData<ApiResponse<RequestResult>> deleteHub(@Body RequestBody request);

    @POST("device/updateDevice")
    LiveData<Response<Profile>> updateDevice(@Body RequestBody request);

    @POST("device/deleteDevice")
    LiveData<ApiResponse<RequestResult>> deleteDevice(@Body RequestBody request);

    /*获取场景列表,场景就是一个灯具组*/
    @POST("group/getGroupsByUserIdOrMesh")
    LiveData<ApiResponse<GroupList>> groupList(@Body RequestBody request);

    @Multipart
    @POST("group/isGroup")
    LiveData<ApiResponse<AddGroupSceneResult>> createGroup(@Part MultipartBody.Part file, @QueryMap Map<String, String> map);

    @Multipart
    @POST("group/updateGroupScene")
    LiveData<ApiResponse<AddGroupSceneResult>> updateGroup(@Part MultipartBody.Part file, @QueryMap Map<String, String> map);

    @POST("group/updateGroupScene")
    LiveData<ApiResponse<AddGroupSceneResult>> updateGroup(@QueryMap Map<String, String> map);

    /*删除场景*/
    @POST("group/deleteGroup")
    LiveData<ApiResponse<RequestResult>> deleteGroup(@Body RequestBody request);

    /*添加设备到场景*/
    @POST("group/addDeviceToGroup")
    LiveData<ApiResponse<RequestResult>> addDeviceToGroup(@Body RequestBody request);

    /*获取场景详情 用来修改*/
    @POST("group/getGroupById")
    LiveData<ApiResponse<Group>> getGroupById(@Body RequestBody request);

    /*获取场景添加的设备*/
    @POST("group/getDevicesByGroupId")
    LiveData<ApiResponse<GroupDevice>> getDevicesByGroupId(@Body RequestBody request);

    @POST("scene/getSceneList")
    LiveData<ApiResponse<SceneList>> getSceneList(@Body RequestBody request);

    @Multipart
    @POST("scene/createScene")
    LiveData<ApiResponse<AddGroupSceneResult>> createScene(@Part MultipartBody.Part file, @QueryMap Map<String, String> map);

    @Multipart
    @POST("scene/updateScene")
    LiveData<ApiResponse<AddGroupSceneResult>> updateScene(@Part MultipartBody.Part file, @QueryMap Map<String, String> map);

    @POST("scene/updateScene")
    LiveData<ApiResponse<AddGroupSceneResult>> updateScene(@QueryMap Map<String, String> map);

    @POST("scene/deleteScene")
    LiveData<ApiResponse<RequestResult>> deleteScene(@Body RequestBody request);

    @POST("scene/addDeviceToScene")
    LiveData<ApiResponse<RequestResult>> addDeviceToScene(@Body RequestBody request);

    @POST("scene/getDevicesBySceneId")
    LiveData<ApiResponse<GroupDevice>> getDevicesBySceneId(@Body RequestBody request);

    @POST("clock/getClockList")
    LiveData<Response<Profile>> getClockList(@Body RequestBody request);

    @POST("clock/createClock")
    LiveData<Response<Profile>> createClock(@Body RequestBody request);

    @POST("clock/updateClock")
    LiveData<Response<Profile>> updateClock(@Body RequestBody request);

    @POST("clock/deleteClock")
    LiveData<Response<Profile>> deleteClock(@Body RequestBody request);

    @POST("clock/addDeviceToClock")
    LiveData<Response<Profile>> addDeviceToClock(@Body RequestBody request);


    @POST("clock/getDevicesByClockId")
    LiveData<Response<Profile>> getDevicesByClockId(@Body RequestBody request);

    @POST("feedback/createFeedback")
    LiveData<ApiResponse<RequestResult>> feedBack(@Body RequestBody request);
}
