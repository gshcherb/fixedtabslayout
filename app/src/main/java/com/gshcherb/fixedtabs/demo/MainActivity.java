package com.gshcherb.fixedtabs.demo;

import android.os.Bundle;

import com.gshcherb.fixedtabs.FixedTabsLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.view_pager);
        FixedTabsLayout tabs = findViewById(R.id.tabs);
        tabs.setupTabs(viewPager);

        PagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return Fragment.instantiate(MainActivity.this, DemoFragment.class.getCanonicalName(), null);
            }

            @Override
            public int getCount() {
                return 10;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return String.valueOf(position);
            }
        };
        viewPager.setAdapter(adapter);
    }
}
