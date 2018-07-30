package com.example.ledwisdom1.api;

import android.os.Build;
import android.util.Log;

import com.example.ledwisdom1.app.SmartLightApp;
import com.example.ledwisdom1.repository.HomeRepository;

import java.io.IOException;

import javax.net.ssl.SSLContext;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2016/7/25.
 */
public class NetWork {
    private static final String TAG = "NetWork";
    private static KimAscendService sKimAscendService;


    public static KimAscendService kimService() {
        if (sKimAscendService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.1.33/intelligence/")
                    .client(configOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                    .build();
            sKimAscendService = retrofit.create(KimAscendService.class);

        }
        return sKimAscendService;
    }


    private static OkHttpClient configOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (Build.VERSION.SDK_INT < 20) {
            try {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, null, null);
                Tls12SocketFactory socketFactory = new Tls12SocketFactory(sslContext.getSocketFactory());
                builder.sslSocketFactory(socketFactory, socketFactory.getDefaultTrustManager());
            } catch (Exception e) {
                Log.e(TAG, "configOkHttpClient: unable to use tls");

            }

        }
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> Log.d(TAG, "message " + message));
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(loggingInterceptor);
        builder.addInterceptor(new HeaderInterceptor());

        return builder.build();
    }

    private static class HeaderInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            SmartLightApp lightApp = SmartLightApp.INSTANCE();
            HomeRepository repository = HomeRepository.INSTANCE(lightApp);
            String sessionId = repository.getSessionId();

//            Profile userProfile = repository.profileObserver.getValue();
//            Profile userProfile = SmartLightApp.INSTANCE().getUserProfile();
            /*if (userProfile != null && !TextUtils.isEmpty(userProfile.sessionid)) {
                sessionId = userProfile.sessionid;
            }*/
            Log.d(TAG, "session"+sessionId);
            Request request = chain.request();
            request = request.newBuilder()
                    .addHeader("accessToken", sessionId)
                    .build();
            return chain.proceed(request);
        }
    }

}
