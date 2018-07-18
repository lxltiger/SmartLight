
1.关于模块 
  app-应用的业务逻辑，架构模式MVVM
  BluetoothLightLib-蓝牙网络依赖库
  zxinglib-二维码扫描生成依赖库
  espTouch 连接网关和WIFI的工具，不需要网关的系列号

2.通信方式
  短连接，使用Retrofit、OKHTTP库
  长连接，使用MQTT库  
  
  使用Retrofit，如果参数为null，内部处理掩盖了异常，什么请求都不会发生，此时需要检查参数是否为空

3.Hub状态
 未初始化的网关显示红灯
 初始化过程是蓝灯，说明正在链接WIFI，
 完成WIFI绑定是黄灯，hub链接到Wifi之后 按一下上报自己IP MAC等的参数来激活，激活的HUb 其status为1
 注意：Hub 有可能USB接触不良导致初始化失败

4.Hub 对灯的控制需要meshName和meshPassword

5.MQTT调试工具
    服务器地址：tcp://121.40.76.221
    用户名：test 密码 123456
    主题 ：/App2dev/529/;//APP 控制灯
           /dev2sta/529/;//监听灯的状态
           /dev2ser/529/;//监听网关状态
           
6.灯具的初始化，手动设置：拔掉插停1秒，重复三次，然后拔掉插停5秒二次

 问题集：
CardView在5.0前后版本设置app:cardElevation会导致边距不一致，添加app:cardUseCompatPadding="true"

