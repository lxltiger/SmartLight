package com.example.ledwisdom1;

import com.google.gson.Gson;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    //android资源文件的复制
    @Test
    public void addition_isCorrect() throws IOException {
        File dir = new File("C:\\Users\\Administrator\\Desktop\\smartLight\\android\\");
        File[] files = dir.listFiles();
        for (File file : files) {
            System.out.println(file.getName());
            File des = new File(file.getAbsolutePath() + "_bak");
            if (des.mkdir()) {
                System.out.println("success");
                listFile(des, file);
            }

        }
    }


    private void listFile(File des, File file) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files) {
                listFile(des, file1);
            }
        } else {
            System.out.println(file.getAbsolutePath());
//            命名不能大写
            Files.copy(file.toPath(), new File(des, file.getName().toLowerCase()).toPath());
        }
    }

    //由于资源文件的更新，每次复制不同像素的图片十分繁琐，这里比较最新的资源文件的，找到增加的部分 复制过去
    private final String src = "C:\\Users\\Administrator\\Desktop\\smartLight\\";
    private final String des = "F:\\lxl\\android\\SmartLight\\app\\src\\main\\res\\";

    @Test
    public void copyOnDifference() throws IOException {
        File src_file = new File(src);
        if (src_file.exists() && src_file.isDirectory()) {
            File[] files = src_file.listFiles();
            //依次处理各分辨率图片
            for (File file : files) {
                if (!file.isDirectory()) {
                    return;
                }
                String dir_name = file.getName();
//                目标文件夹 如drawable-hdpi
                File des_file = new File(des, dir_name);
//                判断目标文件夹具体分辨率文件夹是否存在
                if (des_file.exists() && des_file.isDirectory()) {
                    List<String> src_list = new ArrayList<>(Arrays.asList(file.list()));
                    List<String> des_list = Arrays.asList(des_file.list());
//                    名字相同的都移除
                    src_list.removeAll(des_list);
                    for (String fileName : src_list) {
                        Path from = new File(file, fileName).toPath();
                        System.out.println(from.toString());
                        Path to = new File(des_file, fileName).toPath();
                        System.out.println(to);
//                        Files.copy(from, to);
                    }
                }
            }
        }
    }

    @Test
    public void testJson() {
        List<String> deviceIds = new ArrayList<>();
        deviceIds.add("hgei");
        deviceIds.add("hgei");
        String s = new Gson().toJson(deviceIds);
        System.out.println(s);
    }

    @Test
    public void testWeek() {
        Calendar instance = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("EEE", Locale.CHINA);
        instance.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        for (int i = 0; i < 7; i++) {
            String format = dateFormat.format(instance.getTimeInMillis());
            System.out.println(format);
            instance.add(Calendar.DAY_OF_WEEK, 1);
        }
    }

    @Test
    public void testBits() {
        String[] days = {"2", "3", "5"};
        int origin = 0x00;
        for (String day : days) {
            int bit = Integer.parseInt(day);
            origin |= (0x01 << bit);
        }

        System.out.println(Integer.toBinaryString(origin));
    }

    @Test
    public void testTimeSet() {
        byte[] params = {7, (byte) 0xdf, 6, 27, 11, 3, 5, -1, -1, -1};
        int offset = 0;
        int year = ((params[offset++] & 0xFF) << 8)+ (params[offset++] & 0xff);

        int test=2015;
        byte one = (byte) (test >> 8 & 0xff);
        byte two= (byte) (test&0xff);
//        assert (byte) 0xdf == two;
        System.out.println(Integer.toHexString(two));


        System.out.println(one);
//        System.out.println(b);

    }

}