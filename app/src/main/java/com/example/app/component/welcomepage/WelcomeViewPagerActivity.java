package com.example.app.component.welcomepage;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.app.R;

import java.util.ArrayList;

public class WelcomeViewPagerActivity extends Activity {

    private ListView lsv;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_view_pager);

        lsv = (ListView) findViewById(R.id.lvs);
        ArrayList<String> list = new ArrayList<>();
        list.add("http://img0.imgtn.bdimg.com/it/u=3027221949,3438213709&fm=11&gp=0.jpg");
        list.add("http://b.hiphotos.baidu.com/image/h%3D220/sign=a35c76f9bade9c82b965fe8d5c8080d2/0824ab18972bd40704fe413d72899e510fb30930.jpg");
        list.add("http://c.hiphotos.baidu.com/image/h%3D220/sign=881ae477e050352aae61220a6341fb1a/3b292df5e0fe99250ed882903da85edf8cb1711e.jpg");
        list.add("http://g.hiphotos.baidu.com/image/h%3D220/sign=e1b723d54ba7d933a0a8e3719d4ad194/1c950a7b02087bf4e4d54e28fbd3572c11dfcf32.jpg");
        list.add("http://f.hiphotos.baidu.com/image/h%3D220/sign=131600a9d809b3def4bfe36afcbe6cd3/d1160924ab18972b915fa70befcd7b899f510a8d.jpg");
        list.add("http://g.hiphotos.baidu.com/image/pic/item/314e251f95cad1c8c12683e7763e6709c83d5154.jpg");
        list.add("http://a.hiphotos.baidu.com/image/h%3D220/sign=0cf34b5857df8db1a32e7b663922dddb/1ad5ad6eddc451dad158462bbffd5266d1163247.jpg");
        list.add("http://pic5.photophoto.cn/20071213/0010023914256732_b.jpg");
        list.add("http://imgsrc.baidu.com/imgad/pic/item/0bd162d9f2d3572c333852298013632763d0c369.jpg");
        list.add("http://pic7.nipic.com/20100430/4762876_142052098501_2.jpg");
        list.add("http://imgsrc.baidu.com/imgad/pic/item/023b5bb5c9ea15cef5295efdbc003af33b87b27b.jpg");
        list.add("http://pic.58pic.com/58pic/14/04/20/46j58PICnbQ_1024.jpg");
        list.add("http://pic.58pic.com/58pic/14/03/71/30A58PICQcF_1024.jpg");
        list.add("http://scimg.jb51.net/allimg/151226/13-151226212TU33.jpg");
        list.add("http://imgsrc.baidu.com/imgad/pic/item/6a63f6246b600c332c647f3e104c510fd9f9a123.jpg");
        adapter = new MyAdapter(this,list);
        lsv.setAdapter(adapter);
    }
}
