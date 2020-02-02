package com.sourcey.materiallogindemo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class WordAdapter extends BaseAdapter {

    private ArrayList<String> mWords;

    public WordAdapter(ArrayList<String> words) {
        mWords = words;
    }

    @Override
    public int getCount() {
        return mWords.size();
    }

    @Override
    public Object getItem(int position) {
        return mWords.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 檢查convertView是否有值，有值表示是重複使用的
        if (convertView == null) {
            // 沒有值就要自己建立一個
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.basic_list_item, null);
        }

        // 找到TextView
        TextView title = (TextView) convertView.findViewById(R.id.title);
        // 取出文字
        String text = (String) getItem(position);
        // 將文字內容設定給TextView
        title.setText(text);

        // 一定要將convertView回傳，供ListView呈現使用，並加入重用機制中
        return convertView;
    }

}