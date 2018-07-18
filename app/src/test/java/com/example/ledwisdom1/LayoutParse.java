package com.example.ledwisdom1;


import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用用Layout布局生成binding
 */
public class LayoutParse {
    private String path = "F:\\lxl\\android\\SmartLight\\app\\src\\main\\res\\layout";

    private void capFirst(String s[]) {
        if (s == null ||s.length==1){
            return;
        }
        for (int i = 0; i < s.length; i++) {
            s[i]=String.valueOf(s[i].charAt(0)).toUpperCase().concat(s[i].substring(1)).replaceAll(".xml","");
        }
    }

    private String capFirst(String s) {
        return String.valueOf(s.charAt(0)).toUpperCase().concat(s.substring(1));
    }

    private String lowFirst(String s) {
        return String.valueOf(s.charAt(0)).toLowerCase().concat(s.substring(1));
    }

    private Pair<String , String,String> retrieveName(String name) {
        String[] parts = name.split("_");
        String className="";
        String variableName;
        String bindingName="binding";
        for (int i = 0; i < parts.length; i++) {
            className = className.concat(capFirst(parts[i]));
            if (i == parts.length-1) {
                bindingName=bindingName.concat(capFirst(parts[i]));            }
        }
        className=className.concat("Binding");
        variableName = lowFirst(className);
        return new Pair<>(className ,variableName,bindingName);
    }


    @Test
    public void parse() {
        File file = new File(path);
        if (!file.exists()||!file.isDirectory()) {
            return;
        }
        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {

                return file.getName().contains("setting")/*||file.getName().contains("fragment"*/;
            }
        });
        if (files == null) {
            return;
        }
//
        List<String> autoClears = new ArrayList<>();
        List<String> binds = new ArrayList<>();
        List<String> views = new ArrayList<>();
        List<String> autoClearVars = new ArrayList<>();
        List<String> viewModels = new ArrayList<>();
        for (File file1 : files) {
//            System.out.println(file1.getName());
            String name = file1.getName().replaceAll(".xml","");
            Pair<String, String,String> result = retrieveName(name);
            String binding = String.format("%s %s =DataBindingUtil.inflate(inflater, R.layout.%s, container, false);", result.first, result.second, name);
            binds.add(binding);
            String view = String.format("viewList.add(%s.getRoot());", result.second);
            views.add(view);

            String autoClear = String.format("private AutoClearValue<%s> %s;",result.first ,result.thrid);
            autoClears.add(autoClear);
            String autoClearVar = String.format("%s = new AutoClearValue<>(this, %s);",result.thrid,result.second);
            autoClearVars.add(autoClearVar);

            String viewmodel = String.format("%s.get().setViewModel(viewModel);\n%s.get().setHandler(this);", result.thrid, result.thrid);
            viewModels.add(viewmodel);
        }
        for (String autoClear : autoClears) {
            System.out.println(autoClear);
        }

        System.out.println(" List<View> viewList = new ArrayList<>();\nCommonPagerAdapter pagerAdapter = new CommonPagerAdapter(viewList);");
        for (String bind : binds) {
            System.out.println(bind);
        }

        for (String view : views) {
            System.out.println(view);
        }

        for (String autoClearVar : autoClearVars) {
            System.out.println(autoClearVar);
        }


        for (String viewModel : viewModels) {
            System.out.println(viewModel);
        }

    }

    class Pair<T,U,V>{
        public  final T first;
        public final U second;
        public final V thrid;

        public Pair(T first, U second,V thrid) {
            this.first = first;
            this.second = second;
            this.thrid = thrid;
        }
    }
}
