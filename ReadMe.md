
1.关于模块 
  app-应用的业务逻辑，架构模式MVVM
  BluetoothLightLib-蓝牙网络依赖库
  zxinglib-二维码扫描生成依赖库
  espTouch 连接网关和WIFI的工具，不需要网关的系列号

2.通信方式
  短连接，使用Retrofit、OKHTTP库
  长连接，使用MQTT库  
  
  使用Retrofit，如果请求参数含null，内部处理掩盖了异常，什么请求都不会发生，此时需要检查参数是否为空
  如果返回的数据是LiveData类型，没有观察者，也不会发生HTTP请求

Room 数据库  如果字段发生改变 需要提高版本，存入数据的字段不能使用boolean等非标准类型，否则编译不过；相关Dao接口的方法必须有注解，否则应该声明为抽象方法
ConstraintLayout 保持最新版使用旧版本会使新特性失效，导致图片不按预期显示

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

7.RecyclerView中的item可视化

    tools:layoutManager="GridLayoutManager"
    tools:listitem="@layout/item_scene"
    tools:spanCount="2"

8.CardView在5.0前后版本设置app:cardElevation会导致边距不一致，添加app:cardUseCompatPadding="true"

        app:cardCornerRadius="8dp"  圆角
        app:cardElevation="6dp"   阴影
        app:cardUseCompatPadding="true"  阴影兼容
        app:cardPreventCornerOverlap="false" 圆角兼容 否则有padding 露出cardBackground
        
9正方形图片 

    <ImageView
      android:id="@+id/scene_icon"
      android:layout_width="0dp"
      android:layout_height="0dp"
      android:onClick="@{()->handler.onItemClick(scene)}"
      android:scaleType="centerCrop"
      app:imageUrl="@{scene.icon}"
      app:type="@{ImageTransformationType.ROUND}"
      app:layout_constraintDimensionRatio="w,1:1"
      app:layout_constraintLeft_toLeftOf="parent"
      app:layout_constraintRight_toRightOf="parent"
      app:layout_constraintTop_toTopOf="parent"
      tools:background="@mipmap/ic_launcher"
      />
   10 带阴影和圆角的正方形图片（L以下无圆角）
      
    <android.support.v7.widget.CardView
            android:id="@+id/card"
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:foreground="?android:attr/selectableItemBackground"
            android:onClick="@{()->handler.onItemClick(scene)}"
            app:cardCornerRadius="8dp"
            app:cardElevation="6dp"
            app:cardUseCompatPadding="true"
            app:cardPreventCornerOverlap="false"
            app:layout_constraintDimensionRatio="w,1:1"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent"
            app:layout_constraintTop_toTopOf="parent">

            <ImageView
                android:id="@+id/icon"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:scaleType="centerCrop"
                app:imageUrl="@{scene.icon}"
                tools:background="@mipmap/ic_launcher" />

            <ImageView
                android:id="@+id/edit"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:background="@drawable/btn_setting"
                android:onClick="@{()->handler.onEditClick(scene)}"
                android:layout_gravity="end|top"
                android:layout_margin="10dp"
                />
        </android.support.v7.widget.CardView>
        
        
 11. switch的方法引用          
 android:onCheckedChanged="@{(v,checked)->listener.onCheckedChanged(v,checked,clock)}"

12.在xml中使用资源id给控件背景：
app:backgroundResource="@{myData.iconId}"
 //set a drawable from a resource ID as an ImageView src
android:src="@{ContextCompat.getDrawable(context, character.icon)}"

13设置toolbar的导航按钮 一般是返回键
toolbar.setNavigationIcon(R.drawable.ic_back_36dp);
填充目录按钮 及设置点击事件
toolbar.inflateMenu(R.menu.fragment_device);
toolbar.setOnMenuItemClickListener(this::onMenuItemClick);

14获取anr文件 
adb pull /data/anr/traces.txt trace.txt


15 为了兼容5.0之前，使用vector drawable需要在app中设置
AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
使用app:srcCompat代替src
app:srcCompat="@{user.likes &lt; 4 ? R.drawable.ic_person_black_96dp : R.drawable.ic_whatshot_black_96dp }"/>
imageView.setImageDrawable(...);代替imageView.setImageResource(...);


