package com.alenor.filmdb.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.alenor.filmdb.R;
import com.alenor.filmdb.ui.adapter.GuestMenuActivityPagerAdapter;

public class GuestMenuActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent i = new Intent(context, GuestMenuActivity.class);
        context.startActivity(i);
    }

    public static final int PAGE_COUNT = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_menu_activity);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.guest_menu_activity_toolbar);
        setSupportActionBar(toolbar);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.guest_menu_activity_app_bar);

        GuestMenuActivityPagerAdapter guestMenuActivityPagerAdapter = new GuestMenuActivityPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.guest_menu_activity_pager);
        viewPager.setAdapter(guestMenuActivityPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                toolbar.getMenu().clear();
                if (position == 0) {
                    toolbar.inflateMenu(R.menu.guest_menu_activity_toolbar_menu);
                }
                appBarLayout.setExpanded(true, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.guest_menu_activity_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
