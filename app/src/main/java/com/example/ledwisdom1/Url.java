package com.example.ledwisdom1;

public interface Url {


    String PREFIX = "http://192.168.1.33/lamp/";
    //手机号验证接口 检查是否已经注册
    String CHECK_ACCOUNT = "user/checkAccount";
    // 发送手机验证码 ZXH
    String VALIDATE = "user/getAuthCode";
    //验证手机号和验证码是否正确接口
    String SMS_VALIDATE = "user/SMSvalidate";

    //用户注册接口
    String REGISTER = "user/regist";

    // 登录
    String LOGIN = "user/login";
    String LOGIN_TYPE = "1";//1 android 2 IOS  3 web

    String LOGIN_OUT = "user/loginout";// 退出登录
    //1.10.	设备批量上报接口
    String REPORT_LAMP_DEVICE = "device/reportLampDevice";
    //1.13.	蓝牙网络上报接口
    String REPORT_LAMP_BLEMESH = "belMesh/reportLampBleMesh";

    String LAMP_MESH_LIST = "belMesh/lampMeshList";//1.14.	获取蓝牙网络列表接口
    String DELETE_LAMP_MESH = "belMesh/deleteLampMesh";//1.15.	删除蓝牙网络接口
    String UPDATE_LAMP_BLEMESH = "belMesh/updateLampBleMesh";//1.16.	修改网关的蓝牙网络

    String DELETE_LAMP_DEVICE = "device/deleteLampDevice";//1.11.	设备删除上报接口
    String LAMP_DEVICE_LIST = "device/lampDeviceList";//1.12.	获取灯具列表接口
    String UPDATE_HUB_LAMPS = "device/updateLamps";//更新Hub的灯具

    String REPORT_LAMP_GATEWAY = "gateway/reportLampGateway";//1.8.	网关上报接口
    String GATEWAY_LIST = "gateway/gatewayListAppendDeviceNum";//1.9.	获取网关列表接口

    String DELETE_LAMP_GATEWAY = "gateway/deleteLampGateway";//1.9.	网关删除接口

    /**场景接口*/
    String CREATE_GROUP= "group/createGroup";//新建场景
    String DEL_GROUP = "group/deleteGroup";//删除场景
    String UPDATE_GROUP = "group/updateGroup";//修改场景
    String GET_GROUP_BY_ID = "group/getGroupById";//根据id查询场景
    String GET_GROUPS_BY_USERID_OR_MESHID = "group/getGroupsByUserIdOrMesh";//根据用户id查询场景
    String GET_DEVICES_BY_GROUP_ID = "group/getDevicesByGroupId";//根据场景id查询灯
    String ADD_DEVICE_TO_GROUP = "group/addDeviceToGroup";//场景中加设备
    String DElDEVICE_FROM_GROUP = "group/delDeviceFromGroup";//场景中删除设备

    /**MQTT链接接口*/

}
