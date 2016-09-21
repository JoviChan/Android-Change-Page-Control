package com.example.chenzhiwei3.mythirdapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ListView mLv, mLv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLv = (ListView) findViewById(R.id.mlv);
        SimpleAdapter adapter = new SimpleAdapter(this, getData(),
                R.layout.lv_item, new String[] { "img", "title", "info" },
                new int[] { R.id.img, R.id.title, R.id.info });
        mLv.setAdapter(adapter);
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        for(int i = 0; i < 5; i++) {
            map.put("img", R.drawable.guide_btn_launch_normal);
            map.put("title", "小宗");
            map.put("info", "电台DJ");
            list.add(map);

            map = new HashMap<String, Object>();
            map.put("img", R.drawable.guide_btn_launch_normal);
            map.put("title", "貂蝉");
            map.put("info", "四大美女");
            list.add(map);

            map = new HashMap<String, Object>();
            map.put("img", R.drawable.guide_btn_launch_normal);
            map.put("title", "奶茶");
            map.put("info", "清纯妹妹");
            list.add(map);

            map = new HashMap<String, Object>();
            map.put("img", R.drawable.guide_btn_launch_normal);
            map.put("title", "大黄");
            map.put("info", "是小狗");
            list.add(map);

            map = new HashMap<String, Object>();
            map.put("img", R.drawable.guide_btn_launch_normal);
            map.put("title", "hello");
            map.put("info", "every thing");
            list.add(map);

            map = new HashMap<String, Object>();
            map.put("img", R.drawable.guide_btn_launch_normal);
            map.put("title", "world");
            map.put("info", "hello world");
            list.add(map);
        }


        return list;
    }
}
