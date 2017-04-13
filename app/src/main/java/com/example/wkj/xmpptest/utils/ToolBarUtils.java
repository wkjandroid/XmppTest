package com.example.wkj.xmpptest.utils;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wkj.xmpptest.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wkj on 2017/3/21.
 */

public class ToolBarUtils {
    private List<TextView> textViewList = new ArrayList<TextView>();
    private LinearLayout.LayoutParams params;

    public void createToolBar(LinearLayout container, String[] tooBarTitleArr, int[] iconArr) {
        for ( int i = 0; i < tooBarTitleArr.length; i++) {
            TextView tv = (TextView) View.inflate(container.getContext(), R.layout.inflate_toolbar_btn, null);
            tv.setText(tooBarTitleArr[i]);
            tv.setCompoundDrawablesWithIntrinsicBounds(0, iconArr[i], 0, 0);
            int width = 0;
            int height = LinearLayout.LayoutParams.MATCH_PARENT;
            params = new LinearLayout.LayoutParams(width, height);
            params.weight = 1;
            container.addView(tv, params);
            textViewList.add(tv);
            final int finalI=i;
            //设置 点击事件
            tv.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //不同模块之间传值需要用接口回调
                    //3在需要传值的地方，用借口对象调用接口方法
                    mOnToolBarClickListener.onToolBarClick(finalI);
                }
            });
        }
    }

    public void changeColor(int position) {
        for (TextView t : textViewList) {
            t.setSelected(false);
        }
        textViewList.get(position).setSelected(true);
    }
    /*
    * 1创建接口和接口方法
    * */
    public interface OnToolBarClickListener{
        void onToolBarClick(int position);
    }
    /*
    2创建接口变量
    * */
    OnToolBarClickListener mOnToolBarClickListener;
/*
* 4暴漏一个公共方法
* */
    public void setOnToolBarClickListener(OnToolBarClickListener onToolBarClickListener){
        mOnToolBarClickListener=onToolBarClickListener;
    }

}
