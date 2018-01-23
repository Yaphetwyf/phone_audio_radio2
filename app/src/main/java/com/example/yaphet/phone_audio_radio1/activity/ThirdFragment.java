package com.example.yaphet.phone_audio_radio1.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yaphet.phone_audio_radio1.Bean.RadioInfoNet;
import com.example.yaphet.phone_audio_radio1.R;
import com.example.yaphet.phone_audio_radio1.adapter.NetRadioAdapter;
import com.example.yaphet.phone_audio_radio1.domin.RadioInfo;
import com.example.yaphet.phone_audio_radio1.util.Contents;
import com.example.yaphet.phone_audio_radio1.util.MyApplication;
import com.example.yaphet.phone_audio_radio1.util.SavaWordNet;
import com.example.yaphet.phone_audio_radio1.util.Utils;
import com.example.yaphet.phone_audio_radio1.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.R.attr.id;

/**
 * Created by WYF on 2017/11/7.
 */

public class ThirdFragment extends BaseFrament implements XListView.IXListViewListener {
    /**
     * 用xutils注解，实例化
     */
    @ViewInject(R.id.lv_first_rudio)
    private XListView mListView;

    @ViewInject(R.id.PB_press_center)
    private ProgressBar progressBar;

    @ViewInject(R.id.tv_text)
    private TextView textView;
    private String Tag="TAG----";
    private Handler mHandler;
    private int mIndex = 0;
    private int mRefreshIndex = 0;
    List<RadioInfo> radioInfos =new ArrayList<>();

    @Override
    protected View initView() {
       View view=View.inflate(MyApplication.getContext(), R.layout.listmenu_net,null);//总的布局
        Log.e("TAG", "ThirdFragment被初始化了");
        x.view().inject(this,view);
        return view;
    }
    @Override
    protected void initdata() {
        super.initdata();

        //如果有缓存，先解析缓存的数据
        String getSavedata = SavaWordNet.GetSave(getContext(), Contents.URI);
        //解析缓存的数据
        praseJsonWithGson(getSavedata);
        if(radioInfos!=null&&radioInfos.size()>0) {
            netRadioAdapter=new NetRadioAdapter(radioInfos);
            mListView.setAdapter(netRadioAdapter);//设置适配器
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(MyApplication.getContext(), "播放出错，请检查网络", Toast.LENGTH_SHORT).show();
                }
            });
        }
        //用xutils加载数据
        getDataFromNet();//先得到数据


//        setAutoView();
//        setListView();//主要的功能就是消失progressBar和设置适配器
        String name = Thread.currentThread().getName();//主线程
        Log.e("IDDDD",name);
    }
    private void setAutoView() {
        mHandler = new Handler();
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
        mListView.setAutoLoadEnable(true);
        mListView.setXListViewListener(this);
        mListView.setRefreshTime(getTime());
    }
    private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
    }
    private void getDataFromNet() {
        RequestParams requestParams=new RequestParams(Contents.URI);//联网请求在子线程
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {//回调结果在主线程
                String name = Thread.currentThread().getName();
                Log.e(Tag,name+result);//主线程

                //做文本缓存功能，具体功能为当有网络时缓存数据，断网时实现缓存的显示
                SavaWordNet.putSave(getContext(),Contents.URI,result);//保存数据

                //接下来的任务解析json数据
                praseJsonWithGson(result);
                setAutoView();
                setListView();
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //获得联网状态，判断是否有网络

                ConnectivityManager connectivityManager= (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if(networkInfo!=null&&networkInfo.isAvailable()) {
                    textView.setVisibility(View.GONE);
                    Toast.makeText(MyApplication.getContext(), "网络链接超时。请检查网络", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
                Log.e(Tag,ex.getMessage());
            }
            @Override
            public void onCancelled(CancelledException cex) {
                Log.e(Tag,cex.getMessage());
            }
            @Override
            public void onFinished() {
                Log.e(Tag,"onFinished");
                Log.e("tagggg", radioInfos.toString());
                //显示列表
            }
        });
    }
private NetRadioAdapter netRadioAdapter;
    private void setListView() {
        setAdapterandshowView();
    }

    private void setAdapterandshowView() {
        if(radioInfos !=null&& radioInfos.size()>0) {

            //ProgressBar消失
            //显示数据

            netRadioAdapter=new NetRadioAdapter(radioInfos);
            mListView.setAdapter(netRadioAdapter);//设置适配器
            textView.setVisibility(View.GONE);
            //让进度条消失
            progressBar.setVisibility(View.GONE);
            mListView.setOnItemClickListener(new MyListViewAdapter());
        }else{
            //显示没有数据
            textView.setVisibility(View.VISIBLE);
            textView.setText("没有视频文件"+ radioInfos.toString());
            progressBar.setVisibility(View.GONE);
        }
    }

    private void praseJsonWithGson(String jsonString){
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            //第一层解析
            JSONArray jsonArray = jsonObject.optJSONArray("trailers");

            //第二层解析
            for(int i = 0; i <jsonArray.length() ; i++) {
                JSONObject object = jsonArray.optJSONObject(i);
                if(object!=null) {
                    RadioInfo radioInfo =new RadioInfo();
                    String coverImg = object.optString("coverImg");
                    String hightUrl = object.optString("hightUrl");
                    String movieName = object.optString("movieName");
                    int videoLength = object.optInt("videoLength");
                    String videoTitle = object.optString("videoTitle");
                    radioInfo.setDisplayName(movieName);
                    radioInfo.setTime(videoLength+"");//videoLength
                    radioInfo.setAdressAbso(hightUrl);
                    radioInfo.setArtist(coverImg);//coverImg
                    radioInfo.setRadioSize(videoTitle);//videoTitle
                    radioInfos.add(radioInfo);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                radioInfos.clear();
                getDataFromNet();
                netRadioAdapter=new NetRadioAdapter(radioInfos);
                mListView.setAdapter(netRadioAdapter);//设置适配器
                onLoad();
            }
        }, 2500);
    }
    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(getTime());
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getDataFromNet();
                netRadioAdapter.notifyDataSetChanged();
                onLoad();
            }
        }, 2500);
    }

    private class MyListViewAdapter implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            RadioInfo radioInfo = radioInfos.get(position);

            // Toast.makeText(MyApplication.getContext(), radioInfoNet.getAdressAbso(), Toast.LENGTH_SHORT).show();

          /*  //1.调起系统所有的播放器，Intent.ACTION_VIEW(用隐士意图)
            Intent intent=new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse(radioInfoNet.getAdressAbso()),"video*//*");
            MyApplication.getContext().startActivity(intent);*/

            //2.调起系统api所组成的播放器

            //显示意图调用系统API写的播放器,这种情况传递的是单一的地址
           /* Intent intentmyRadio=new Intent(MyApplication.getContext(),SystemRadioActivity.class);
            intentmyRadio.setDataAndType(Uri.parse(radioInfo.getAdressAbso()),"video");
            MyApplication.getContext().startActivity(intentmyRadio);*/
            //传递视频列表
            Intent intent=new Intent(MyApplication.getContext(),SystemRadioActivity.class);
            Bundle bundle=new Bundle();
            bundle.putSerializable("vadio_list",(Serializable)radioInfos);
            intent.putExtras(bundle);//传递视频列表
            intent.putExtra("position",position-1);
            MyApplication.getContext().startActivity(intent);
        }
    }
}
