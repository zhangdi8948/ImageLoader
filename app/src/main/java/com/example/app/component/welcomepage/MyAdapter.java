package com.example.app.component.welcomepage;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app.R;
import com.zd.imageloaderlibrary.ImageLoader;
import com.zd.imageloaderlibrary.cache.DoubleCache;

import java.util.List;

/**
 * Created by pactera on 2017/12/15.
 */

public class MyAdapter extends BaseAdapter {

    private List<String> data;
    private LayoutInflater inflater;
    private ImageLoader loader;
    private static String DISKPATH;

    public MyAdapter(Context context, List<String> list) {

        data = list;
        inflater = LayoutInflater.from(context);
        loader = ImageLoader.getNewIntance();
        DISKPATH = Environment.getExternalStorageDirectory()
                .getPath() + "/Android/data/"+context.getApplicationContext().
                getPackageName()+"/cache";
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item, parent, false);
            holder.iv = (ImageView) convertView.findViewById(R.id.iv);
            holder.tv = (TextView) convertView.findViewById(R.id.tv);
            convertView.setTag(holder);
        } else  {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv.setText(data.get(position));
        try {
            loader.setImageCache(DoubleCache.getInstance(DISKPATH))
                    .BindView(data.get(position),holder.iv);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    class ViewHolder {

        private ImageView iv;
        private TextView tv;
    }
}
