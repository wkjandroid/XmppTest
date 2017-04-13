package com.example.wkj.xmpptest.activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wkj.xmpptest.R;
import com.example.wkj.xmpptest.fragment.ContactsFragment;
import com.example.wkj.xmpptest.fragment.SessionFragment;
import com.example.wkj.xmpptest.utils.ToolBarUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.main_view_paper)
    ViewPager mainViewPaper;
    @BindView(R.id.main_bottom)
    LinearLayout mainBottom;
    // xutils viewUtils注解方式去找控件
    //httpUtils dbutils,bitmaputils
    private List<Fragment> mFragments = new ArrayList<>();
    private final ToolBarUtils toolBarUtils = new ToolBarUtils();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        initListener();
    }

    private void initListener() {
        mainViewPaper.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                toolBarUtils.changeColor(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });
        toolBarUtils.setOnToolBarClickListener(new ToolBarUtils.OnToolBarClickListener() {
            @Override
            public void onToolBarClick(int position) {
                mainViewPaper.setCurrentItem(position);
            }
        });
    }

    private void initData() {
        //viewPaper-->view -->pagerAdapter
        //viewpaper-->fragment-->fragmentAdapter数量比较少
        //viewPaper -->fragment -->fragmentStateAdapter
        //添加fragment到集合中
        mFragments.add(new SessionFragment());
        mFragments.add(new ContactsFragment());

        mainViewPaper.setAdapter(new MyPaperAdapter(getSupportFragmentManager()));
        String[] toolBarTitleArr={"会话","联系人"};
        int[] iconArr={R.drawable.selector_meassage,R.drawable.selector_selfinfo};

        toolBarUtils.createToolBar(mainBottom,toolBarTitleArr,iconArr);
        //设置默认选中效果
        toolBarUtils.changeColor(0);
    }

    class MyPaperAdapter extends FragmentPagerAdapter {
        public MyPaperAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }


        @Override
        public int getCount() {
            return 2;
        }
    }


}
