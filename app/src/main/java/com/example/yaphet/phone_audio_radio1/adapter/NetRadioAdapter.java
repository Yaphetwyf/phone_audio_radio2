package com.example.yaphet.phone_audio_radio1.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.format.Formatter;
import com.example.yaphet.phone_audio_radio1.R;
import com.example.yaphet.phone_audio_radio1.domin.RadioInfo;
import com.example.yaphet.phone_audio_radio1.util.MyApplication;
import com.example.yaphet.phone_audio_radio1.util.Utils;

import org.xutils.x;

import java.util.List;

/**
 * Created by WYF on 2017/11/17.
 */

public class NetRadioAdapter extends BaseAdapter {
    private List<RadioInfo> radioInfos;
    public NetRadioAdapter(List<RadioInfo> radioInfos) {
        this.radioInfos = radioInfos;
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
            view = View.inflate(MyApplication.getContext(), R.layout.netradio_item_first, null);
            viewHolder=new ViewHolder();
            viewHolder.imageView= (ImageView) view.findViewById(R.id.net_picture);
            viewHolder.textView1= (TextView) view.findViewById(R.id.movename);
            viewHolder.textView2= (TextView) view.findViewById(R.id.Moveintroduce);
            viewHolder.textView3= (TextView) view.findViewById(R.id.moive_time);
            view.setTag(viewHolder);
        }else{
            view= convertView;
            viewHolder= (ViewHolder) view.getTag();
        }

        RadioInfo radioInfo = radioInfos.get(position);
        x.image().bind(viewHolder.imageView, radioInfo.getArtist());
        viewHolder.textView1.setText(radioInfo.getDisplayName());
        viewHolder.textView2.setText(radioInfo.getRadioSize());
        Utils utils = new Utils();
        String time = radioInfo.getTime();
        int parseInt = Integer.parseInt(time);
        String stringForTime = utils.stringForTimefors(parseInt);
        viewHolder.textView3.setText(stringForTime);
        //viewHolder.textView3.setText(utils.stringForTime(Integer.parseInt(radioInfo.getTime())));
        return view;
    }
    static class ViewHolder{
        ImageView imageView;
        TextView textView1;
        TextView textView2;
        TextView textView3;
    }
}
