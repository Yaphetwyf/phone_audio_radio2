package com.example.yaphet.phone_audio_radio1.activity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.yaphet.phone_audio_radio1.R;
import com.example.yaphet.phone_audio_radio1.adapter.FourAdapter;
import com.example.yaphet.phone_audio_radio1.domin.FourInfo;
import com.example.yaphet.phone_audio_radio1.util.Contents;
import com.example.yaphet.phone_audio_radio1.util.MyApplication;
import com.example.yaphet.phone_audio_radio1.util.SavaWordNet;
import com.example.yaphet.phone_audio_radio1.view.XListView;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;


/**
 * Created by WYF on 2017/11/7.
 */

public class FourFrament extends BaseFrament {
    /**
     * 用xutils注解，实例化
     */
    @ViewInject(R.id.lv_first_rudio_four)
    private ListView listView;
    @ViewInject(R.id.tv_text_four)
    private TextView textView;
    @ViewInject(R.id.PB_press_center_four)
    private ProgressBar progressBar;

    //获得的数据集合
    List<FourInfo.ListBean> fourInfoLists;
    @Override
    protected View initView() {
       View view=View.inflate(MyApplication.getContext(), R.layout.listmenu_four,null);
        Log.e("TAG", "FourFrament被初始化了");
        x.view().inject(FourFrament.this,view);
        return view;
    }

    @Override
    protected void initdata() {
        super.initdata();
        //取出缓存的数据
        String getSave = SavaWordNet.GetSave(MyApplication.getContext(), Contents.ALL_RES_URL);
        if(!TextUtils.isEmpty(getSave)) {
            praseData(getSave);
        }
        //联网获得数据
        getDataFromNet();
    }

    private void getDataFromNet() {
        progressBar.setVisibility(View.VISIBLE);
        //Xutils请求数据
        RequestParams paras=new RequestParams(Contents.ALL_RES_URL);
        x.http().get(paras, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //请求成功
                SavaWordNet.putSave(MyApplication.getContext(),Contents.ALL_RES_URL,result);//缓存数据
                praseData(result);
                progressBar.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(ex.getMessage().toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 解析Json数据
     * @param result
     */
    private void praseData(String result) {
        Gson gson=new Gson();
        FourInfo fourInfo = gson.fromJson(result, FourInfo.class);
        fourInfoLists = fourInfo.getList();
        //设置适配器
        FourAdapter adapter=new FourAdapter(fourInfoLists);
        listView.setAdapter(adapter);
    }
}
