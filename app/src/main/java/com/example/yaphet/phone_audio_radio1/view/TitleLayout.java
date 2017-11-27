package com.example.yaphet.phone_audio_radio1.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.yaphet.phone_audio_radio1.R;
import com.example.yaphet.phone_audio_radio1.util.MyApplication;

/**
 * Created by WYF on 2017/11/13.
 */

public class TitleLayout extends LinearLayout {
    public TitleLayout(Context context) {
        this(context,null);
    }

    public TitleLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TitleLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View tv_title_search = getChildAt(1);
        View game = getChildAt(2);
        View iv_record = getChildAt(3);
        tv_title_search.setOnClickListener(new myListener());
        game.setOnClickListener(new myListener());
        iv_record.setOnClickListener(new myListener());
    }

    private class myListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_title_search:
                    Toast.makeText(MyApplication.getContext(), "搜索", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.game_dom:
                    Toast.makeText(MyApplication.getContext(), "游戏", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.iv_record:
                    Toast.makeText(MyApplication.getContext(), "记录", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
