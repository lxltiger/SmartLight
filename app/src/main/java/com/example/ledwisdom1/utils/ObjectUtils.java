package com.example.ledwisdom1.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.UUID;

/**
 * Created by Administrator on 2017/6/6.
 * 工具类
 * 数据类型的转化已经判断
 */

public class ObjectUtils {

    public static byte[] StringToByte(String s) {
        if (null == s)
            return null;
        int lenght = s.length();
        byte[] temp = new byte[lenght];
        for (int i = 0; i < lenght; i++) {

            temp[i] = (byte) s.charAt(i);
        }
        return temp;
    }

    public static boolean isNull(Object object) {
        try {
            if (null == object) {
                return true;
            }

            if (object instanceof String) {
                if (object.equals("")) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;

    }

    public static boolean isOneNull(Object... o) {
        Object[] objects = o;
        if (isNull(objects)) {
            return true;
        }

        for (Object object : objects) {
            if (isNull(object)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isOneNotNull(Object... o) {
        Object[] objects = o;
        if (isNull(objects)) {
            return false;
        }

        for (Object object : objects) {
            if (!isNull(object)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isAllNotNull(Object... o) {
        Object[] objects = o;
        if (isNull(objects)) {
            return false;
        }

        for (Object object : objects) {
            if (isNull(object)) {
                return false;
            }
        }

        return true;
    }

    public static int string2Int(String str) {

        if (isNull(str)) {
            return 0;
        }

        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public static long string2Long(String str) {

        if (isNull(str)) {
            return 0L;
        }

        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            return 0L;
        }
    }

    public static float string2Float(String str) {

        if (isNull(str)) {
            return 0f;
        }

        try {
            return Float.parseFloat(str);
        } catch (Exception e) {
            return 0f;
        }
    }

    public static double string2Double(String str) {

        if (isNull(str)) {
            return 0;
        }

        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean string2Boolean(String str) {

        if (isNull(str)) {
            return false;
        }
        try {
            return Boolean.parseBoolean(str);
        } catch (Exception e) {
            return false;
        }

    }

    public static byte string2Byte(String str) {

        if (isNull(str)) {
            return 0;
        }
        try {
            return Byte.parseByte(str);
        } catch (Exception e) {
            return 0;
        }

    }

    public static String object2String(Object object) {
        return object == null ? "" : object.toString();
    }

    /**
     * @param d
     * @param length ��ȷС��λ��
     * @return
     */

    public static double decimal(double d, int length) {
        StringBuffer sb = new StringBuffer();
        sb.append("0.");
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                sb.append("0");
            }
            DecimalFormat decimalFormat = new DecimalFormat(sb.toString());
            return string2Double(decimalFormat.format(d));
        }
        return 0;
    }

    private static final String[] digits = new String[]{"��", "һ", "��", "��", "��", "��", "��", "��", "��", "��"};
    private static final String[] radices = new String[]{"", "ʮ", "��", "ǧ"};
    private static final String[] bigRadices = new String[]{"", "��", "��", "��"};

    public static String digitConvert(int digit) {

        String currencyDigits = String.valueOf(digit);

        String integral = null;
        String outputCharacters = null;

        String d = null;
        int zeroCount = 0, p = 0, quotient = 0, modulus = 0;

        currencyDigits = currencyDigits.replace("/,/g", "");
        currencyDigits = currencyDigits.replace("/^0+/", "");
        String[] parts = currencyDigits.split("\\.");
        if (parts.length > 1) {
            integral = parts[0];

        } else {
            integral = parts[0];
        }

        outputCharacters = "";
        if (Double.parseDouble(integral) > 0) {

            zeroCount = 0;

            for (int i = 0; i < integral.length(); i++) {

                p = integral.length() - i - 1;
                d = integral.substring(i, i + 1);

                quotient = p / 4;
                modulus = p % 4;
                if (d.equals("0")) {
                    zeroCount++;
                } else {
                    if (zeroCount > 0) {
                        outputCharacters += digits[0];
                    }
                    zeroCount = 0;
                    outputCharacters += digits[Integer.parseInt(d)] + radices[modulus];
                }
                if (modulus == 0 && zeroCount < 4) {
                    outputCharacters += bigRadices[quotient];
                }
            }
        }
        if (outputCharacters.length() > 1 && outputCharacters.startsWith("һ") && outputCharacters.indexOf("ʮ") == 1) {
            outputCharacters = outputCharacters.substring(1, outputCharacters.length());
        }

        return outputCharacters;
    }

    public static String formatString(String s) {
        if (isNull(s) || "null".equals(s)) {
            return "";
        }
        return s;
    }

    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * 将实体类转换成json字符串对象            注意此方法需要第三方gson  jar包
     *
     * @param obj 对象
     * @return map
     */
    public static String toJson(Object obj, int method) {
        // TODO Auto-generated method stub
        if (method == 1) {
            //字段是首字母小写，其余单词首字母大写
            Gson gson = new Gson();
            String obj2 = gson.toJson(obj);
            return obj2;
        } else if (method == 2) {

            // FieldNamingPolicy.LOWER_CASE_WITH_DASHES    全部转换为小写，并用空格或者下划线分隔

            //FieldNamingPolicy.UPPER_CAMEL_CASE    所以单词首字母大写
            Gson gson2 = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            String obj2 = gson2.toJson(obj);
            return obj2;
        }
        return "";
    }

    /**
     * 、Bitmap → byte[]
     *
     * @param bm
     * @return
     */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * byte[] → Bitmap
     *
     * @param b
     * @return
     */
    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    /**
     * drawable - bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {

        Bitmap bitmap = Bitmap.createBitmap(

                drawable.getIntrinsicWidth(),

                drawable.getIntrinsicHeight(),

                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

                        : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        //canvas.setBitmap(bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        drawable.draw(canvas);

        return bitmap;
    }

    //将bitmap转化为byte[]类型也就是转化为二进制
    public static byte[] getBitmapByte(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }


    public static String getMacAddress() {
//        String strMacAddr = "";
        StringBuilder buffer = new StringBuilder();

        try {
            NetworkInterface NIC = NetworkInterface.getByName("eth0");
            if (NIC == null) {
                return "";
            }
            //6个字节，48位
            byte[] bytes = NIC.getHardwareAddress();
            if (null == bytes || bytes.length == 0) {
                return "";
            }
            for (byte b : bytes) {
                String str = Integer.toHexString(b & 0xff);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
//            strMacAddr = buffer.deleteCharAt(0).toString();
//            strMacAddr = buffer.toString();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return buffer.toString().toUpperCase();
    }
}
