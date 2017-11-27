package com.example.yaphet.phone_audio_radio1.adapter;

import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yaphet.phone_audio_radio1.R;
import com.example.yaphet.phone_audio_radio1.domin.RadioInfo;
import com.example.yaphet.phone_audio_radio1.util.MyApplication;
import com.example.yaphet.phone_audio_radio1.util.Utils;

import java.util.List;

/**
 * Created by WYF on 2017/11/17.
 */

public class RadioAdapter extends BaseAdapter {
    private List<RadioInfo> radioInfos;
    public RadioAdapter( List<RadioInfo> radioInfoss) {
        this.radioInfos=radioInfoss;
    }

    @Override
    public int getCount() {
        return radioInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        ViewHolder viewHolder;
        if(convertView==null) {
            view = View.inflate(MyApplication.getContext(), R.layout.radio_item_first, null);
            viewHolder=new ViewHolder();
            viewHolder.imageView=view.findViewById(R.id.IV_item_radio);
            viewHolder.textView1=view.findViewById(R.id.tv_radio_name);
            viewHolder.textView2=view.findViewById(R.id.tv_radio_time);
            viewHolder.textView3=view.findViewById(R.id.tv_radio_size);
            view.setTag(viewHolder);
        }else{
            view= convertView;
            viewHolder= (ViewHolder) view.getTag();
        }

        RadioInfo radioInfo = radioInfos.get(position);
        viewHolder.textView1.setText(radioInfo.getDisplayName());

        Utils utils = new Utils();
        viewHolder.textView2.setText(utils.stringForTime(Integer.parseInt(radioInfo.getTime())));


        String radioSize = radioInfos.get(position).getRadioSize();
        long parseLong;
        if(radioSize!=null) {
            parseLong = Long.parseLong(radioSize);
        }else{
            parseLong=0;
        }

        viewHolder.textView3.setText(Formatter.formatFileSize(MyApplication.getContext(),parseLong));
        return view;
    }
    static class ViewHolder{
        ImageView imageView;
        TextView textView1;
        TextView textView2;
        TextView textView3;
    }
}
