package com.example.ledwisdom1.user;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.api.ApiResponse;
import com.example.ledwisdom1.api.Resource;
import com.example.ledwisdom1.model.CommonRequest;
import com.example.ledwisdom1.model.RequestResult;
import com.example.ledwisdom1.utils.RequestCreator;

import okhttp3.RequestBody;

import static com.example.ledwisdom1.user.UserActivity.EMPTY_ACCOUNT;
import static com.example.ledwisdom1.user.UserActivity.EMPTY_AUTH;
import static com.example.ledwisdom1.user.UserActivity.EMPTY_CONTACT;
import static com.example.ledwisdom1.user.UserActivity.EMPTY_CONTENT;
import static com.example.ledwisdom1.user.UserActivity.INVALID_ACCOUNT;
import static com.example.ledwisdom1.user.UserActivity.INVALID_ORIGIN_PSW;
import static com.example.ledwisdom1.user.UserActivity.INVALID_PSW;
import static com.example.ledwisdom1.user.UserActivity.NAVIGATE_TO_FORGET_PSW;
import static com.example.ledwisdom1.user.UserActivity.NAVIGATE_TO_LOGIN;
import static com.example.ledwisdom1.user.UserActivity.NAVIGATE_TO_REGISTER;
import static com.example.ledwisdom1.user.UserActivity.SAME_PSW;

/**
 * 用户数据模型 为UI提供数据
 */
public class UserViewModel extends AndroidViewModel {
    private static final String TAG = UserViewModel.class.getSimpleName();
    /**
     * 用户名 手机号
     */
    public ObservableField<String> account = new ObservableField<>("");
    public ObservableField<String> password = new ObservableField<>("");
    public ObservableField<String> password_origin = new ObservableField<>("");
    public ObservableField<String> authcode = new ObservableField<>("");
    public ObservableField<String> passwordConfirm = new ObservableField<>("");
    //
    public ObservableField<String> content = new ObservableField<>("");
    public ObservableField<String> contact = new ObservableField<>("");
    //标题需要动态修改
    public ObservableField<String> title = new ObservableField<>("");
    //    Http 请求状态
    public ObservableBoolean loading = new ObservableBoolean();
    //    输入错误类型  账号 密码 验证码等
    public ObservableInt errType = new ObservableInt();
    //    错误类型与提示的键值对
    public SparseArray<String> errMsg = new SparseArray<>();
    /**
     * 用来发起网路请求 为此UserViewModel提供数据
     */
    private UserRepository userRepository;

    /**
     * 用来标识是注册操作还是忘记密码操作
     */
    public boolean isRegister = true;
    /**
     * Activity Fragment 通讯监听
     */
    public MutableLiveData<Integer> actionObserver = new MutableLiveData<>();

    //登陆请求
    private MutableLiveData<RequestBody> loginRequest = new MutableLiveData<>();
    //登陆结果
    public LiveData<Resource<Boolean>> loginResponseObserver;

    //验证码请求
    public MutableLiveData<RequestBody> authCodeRequest = new MutableLiveData<>();
    //验证码请求结果
    public LiveData<ApiResponse<RequestResult>> authCodeResponseObserver;

    //    验证码确认请求
    public MutableLiveData<RequestBody> authCodeVertifyRequest = new MutableLiveData<>();
    //    验证码确认结果
    public LiveData<ApiResponse<RequestResult>> authCodeVertifyResponseObserver;

    //    注册重置密码请求
    public MutableLiveData<RequestBody> registerRequest = new MutableLiveData<>();
    //    注册重置密码确认结果
    public LiveData<ApiResponse<RequestResult>> registerResponseObserver;

    // TODO: 2018/7/13 0013 直接使用
    public final LiveData<Profile> profileLiveData;


    //    修改密码请求
    private final MutableLiveData<RequestBody> modifyRequest = new MutableLiveData<>();
    //    修改密码确认结果
    public final LiveData<ApiResponse<RequestResult>> modifyResponseObserver;

    private final MutableLiveData<RequestBody> feedBackRequest = new MutableLiveData<>();
    public final LiveData<ApiResponse<RequestResult>> feedBackResponseObserver;


    public final MutableLiveData<UserRequest> userRequest = new MutableLiveData<>();
    public final LiveData<ApiResponse<RequestResult>> userResponseObserver;


 //    登出请求
    private final MutableLiveData<RequestBody> logoutRequest = new MutableLiveData<>();
    //    登出确认结果
    public final LiveData<Resource<Boolean>> logoutResponseObserver;

    //    验证手机号请求
    private MutableLiveData<RequestBody> checkAccountRequest = new MutableLiveData<>();
    //    验证手机号结果
    public LiveData<ApiResponse<RequestResult>> checkAccountResponseObserver;

    //用来获取资源文件
    private Application application;
    private String PHONE_NUMBER_REG = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";

    public UserViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        userRepository = new UserRepository(application);
        profileLiveData = userRepository.loadProfile();
        populateErrMsg();
        loginResponseObserver = Transformations.switchMap(loginRequest, input -> userRepository.login(input));
        authCodeResponseObserver = Transformations.switchMap(authCodeRequest, input -> userRepository.getAuthCode(input));
        authCodeVertifyResponseObserver = Transformations.switchMap(authCodeVertifyRequest, input -> userRepository.smsValidate(input));
        checkAccountResponseObserver = Transformations.switchMap(checkAccountRequest,input -> userRepository.checkAccount(input));
//        注册和忘记密码UI和逻辑基本类似
        registerResponseObserver = Transformations.switchMap(registerRequest, input -> {
                    if (NAVIGATE_TO_REGISTER == actionObserver.getValue()) {
                        return userRepository.register(input);
                    } else if (NAVIGATE_TO_FORGET_PSW == actionObserver.getValue()) {
                        return userRepository.resetPsw(input);
                    }
                    return null;
                }
        );

        modifyResponseObserver = Transformations.switchMap(modifyRequest, input -> userRepository.modifyPsw(input));
        feedBackResponseObserver = Transformations.switchMap(feedBackRequest, input -> userRepository.feedback(input));
        logoutResponseObserver = Transformations.switchMap(logoutRequest, input -> userRepository.logout());
        userResponseObserver = Transformations.switchMap(userRequest, userRepository::updateUser);
    }

    private void populateErrMsg() {
        errMsg.put(EMPTY_ACCOUNT, getString(R.string.phone_not_null));
        errMsg.put(INVALID_ACCOUNT, getString(R.string.la_invalid_account));
        errMsg.put(INVALID_PSW, getString(R.string.psw_tip));
        errMsg.put(INVALID_ORIGIN_PSW, getString(R.string.psw_tip));
        errMsg.put(EMPTY_AUTH, getString(R.string.ra_verification));
        errMsg.put(SAME_PSW, getString(R.string.ra_forgent_error));
        errMsg.put(EMPTY_CONTENT, getString(R.string.feedback_tip));
        errMsg.put(EMPTY_CONTACT, getString(R.string.contact_tip));
    }

    private String getString(@StringRes int resId) {
        return application.getString(resId);
    }


    //------------------UI操作 start---------------------
    public void login() {
        String account = this.account.get().trim();
        if (!validateAccount(account)) {
            return;
        }

        String password = this.password.get().trim();
        if (!validatePassword(password)) {
            setErrType(INVALID_PSW);
            return;
        }
        loading.set(true);
        RequestBody requestBody = CommonRequest.createLogin(password, 1, account);
        loginRequest.setValue(requestBody);
    }

    public void navigateToRegister() {
        title.set(getString(R.string.ra_register));
        isRegister = true;
        setAction(NAVIGATE_TO_REGISTER);
    }

    public void navigateToForgetPsw() {
        title.set(getString(R.string.forgment_titletext));
        isRegister= false;
        setAction(NAVIGATE_TO_FORGET_PSW);
    }

    public void navigateToLogin() {
        title.set(getString(R.string.la_login));
        setAction(NAVIGATE_TO_LOGIN);
    }

    /**
     * 点击按钮先验证有效性 手机号码、密码长度、验证码
     * 获取验证码过程： 获取验证码 》验证码获取倒计时
     */
    public void getAuthCode() {
        String account = this.account.get().trim();

        if (!validateAccount(account)) {
            return;
        }
        loading.set(true);
        RequestBody requestBody = CommonRequest.createAuthCode(account);
        authCodeRequest.setValue(requestBody);

    }
    /**
     * 点击按钮先验证有效性 手机号码、密码长度、验证码
     * 获取验证码过程：验证手机号》 获取验证码 》验证码获取倒计时
     */
    public void checkAccount() {
        String account = this.account.get().trim();

        if (!validateAccount(account)) {
            return;
        }
        loading.set(true);
        RequestBody requestBody = CommonRequest.createAuthCode(account);
        checkAccountRequest.setValue(requestBody);
    }


    //点击下一步流程-> 请求确定验证码输入是否正确》进入密码输入界面
    public void verifyAuth() {
        String account = this.account.get().trim();
        // 防止用户获取验证码后修改账号 所以再次判断

        if (!validateAccount(account)) {
            return;
        }
        String code = this.authcode.get().trim();
        if (TextUtils.isEmpty(code)) {
            setErrType(EMPTY_AUTH);
            return;
        }
        loading.set(true);

        RequestBody requestBody = CommonRequest.createVertifyAuthCode(account, code);
        authCodeVertifyRequest.setValue(requestBody);
    }

    // 注册 或 密码重置
    public void register() {
        final String psw = password.get().trim();
        if (!validatePassword(psw)) {
            setErrType(INVALID_PSW);
            return;
        }

        if (!psw.equals(passwordConfirm.get())) {
            setErrType(SAME_PSW);
            return;
        }
        loading.set(true);

        RequestBody requestBody = CommonRequest.createLogin(psw, 0, account.get().trim());
        registerRequest.setValue(requestBody);
    }

    //    修改密码
    public void modifyPassword() {
        final String psw_origin = password_origin.get().trim();
        Log.d(TAG, psw_origin);
        if (!validatePassword(psw_origin)) {
            setErrType(INVALID_ORIGIN_PSW);
            return;
        }

        final String psw = password.get().trim();
        if (!validatePassword(psw)) {
            setErrType(INVALID_PSW);
            return;
        }

        if (!psw.equals(passwordConfirm.get())) {
            setErrType(SAME_PSW);
            return;
        }
        Profile userProfile = profileLiveData.getValue();
        String account = userProfile.phone;

        RequestBody modifyPsw = RequestCreator.createModifyPsw(account, psw_origin, psw);
        modifyRequest.setValue(modifyPsw);
    }

    public void feedBack() {
        String suggestion = content.get().trim();
        if (TextUtils.isEmpty(suggestion)) {
            setErrType(EMPTY_CONTENT);
            return;
        }

        final String user_contact = contact.get().trim();
        if (TextUtils.isEmpty(user_contact)) {
            setErrType(EMPTY_CONTACT);
            return;
        }
        RequestBody requestBody = RequestCreator.createFeedBack(suggestion, user_contact);
        feedBackRequest.setValue(requestBody);
    }




    public void logout() {
        logoutRequest.setValue(null);
    }

    public void clear() {
        userRepository.clearLocalData();
    }

//------------------UI操作 end---------------------


    //    页面切换
    public void setAction(int action) {
        actionObserver.setValue(action);
    }

    //    只要设置错误类型就要通知观察者响应
    public void setErrType(int type) {
        if (errType.get() == type) {
//        强制更新 即使type没变
            errType.notifyChange();
        } else {
            this.errType.set(type);
        }

    }

    private boolean validateAccount(String account) {
        if (TextUtils.isEmpty(account)) {
            setErrType(EMPTY_ACCOUNT);
            return false;
        }
        if (!account.matches(PHONE_NUMBER_REG)) {
            setErrType(INVALID_ACCOUNT);
            return false;
        }

        return true;
    }

    private boolean validatePassword(String password) {
        return password.length() >= 8 && password.length() < 16;
    }


}
