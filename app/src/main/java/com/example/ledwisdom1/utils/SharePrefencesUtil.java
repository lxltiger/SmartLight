package com.example.ledwisdom1.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ledwisdom1.app.SmartLightApp;
import com.example.ledwisdom1.mesh.MeshBean;
import com.google.gson.Gson;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class SharePrefencesUtil {
    /**
     * 保存在手机里面的文件名
     */
    private static final String FILE_NAME = "smart_light";
    private static final String USER_PROFILE = "user_profile";
    private static final String CURRENT_MESH = "current_mesh";

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context
     * @param key
     * @param object
     */
    public static void put(Context context, String key, Object object) {

        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }

        SharedPreferencesCompat.apply(editor);
    }

    public static void put(Context context, String key, String value) {
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit()
                .putString(key, value).apply();
    }

    //保存用户信息
    public static void saveUserProfile(Context context, String value) {
        put(context,USER_PROFILE,value);
    }

    /**
     * 后去用户信息 json格式
     * @param context
     * @return
     */
    public static String getUserProfile(Context context) {
        return get(context, USER_PROFILE, "");
    }

    /**
     * 保存当前的蓝牙 mesh
     * @param meshJson
     */
    public static void saveMesh(String meshJson) {
        put(SmartLightApp.INSTANCE(), CURRENT_MESH,meshJson);
    }

    public static void saveMesh(MeshBean mesh) {
        put(SmartLightApp.INSTANCE(), CURRENT_MESH,new Gson().toJson(mesh));
    }

    public static String getCurrentMesh() {
        return get(SmartLightApp.INSTANCE(), CURRENT_MESH, "");
    }






    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @return
     */
    public static Object get(Context context, String key, Object defaultObject) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);

        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    public static String get(Context context, String key, String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);

        return sp.getString(key, defaultValue);
    }



    /**
     * 移除某个key值已经对应的值
     *
     */
    public static void remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     */
    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key是否已经存在
     *
     */
    public static boolean contains(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     */
    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            editor.commit();
        }
    }


//
//    public static TicketInfo getUserInfo(Context mContext){
//        SharedPreferences sp = mContext.getSharedPreferences(
//                FILE_NAME, Context.MODE_PRIVATE);
//        String Account_id=sp.getString("Account_id", "");
//        String Bind_phone =sp.getString("Bind_phone", "");
//        String Nick_name=sp.getString("Nick_name", "");
//        String Sex=sp.getString("Sex", "");
//        String Money =sp.getString("Money", "");
//        TicketInfo  userInfo=new TicketInfo(Account_id, Bind_phone, Nick_name, Sex, Money);
//        return userInfo;
//    }
}
