package com.nightwind.contacts.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.nightwind.contacts.R;
import com.nightwind.contacts.fragment.ContactFragment;
import com.nightwind.contacts.fragment.ContactsFragment;
import com.nightwind.contacts.widget.PagerSlidingTabStrip;

import java.util.Locale;

public class MainToolbarActivity extends AppCompatActivity {

    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_toolbar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PagerSlidingTabStrip pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
        pagerSlidingTabStrip.setViewPager(viewPager);

        initTabsValue(pagerSlidingTabStrip);


        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            protected final int verticalMinDistance = 50;
            protected final int minVelocity = 0;
            private float horizonMinDistance = 10;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float absDx = Math.abs(e2.getX() - e1.getX());
                float absDy = Math.abs(e2.getY() - e1.getY());

                if (absDy > 1.5*absDx && e2.getY() - e1.getY() > horizonMinDistance && Math.abs(velocityY) > minVelocity) {
                    //下拉
                    ContactFragment fragment = (ContactFragment) getSupportFragmentManager().findFragmentByTag(ContactFragment.class.getSimpleName());
                    if (fragment != null) {
                        fragment.onPullDown(e1, e2, velocityX, velocityY);

                    }
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }

        });
    }


    /**
     * pagerSlidingTabStrip tab配置
     *
     */
    private void initTabsValue(PagerSlidingTabStrip pagerSlidingTabStrip) {
        int background = getResources().getColor(R.color.colorPrimary);
        int indicator = getResources().getColor(R.color.colorPrimaryDark);
        int textNormal = getResources().getColor(R.color.colorTabNormalText);

        // 底部游标颜色
        pagerSlidingTabStrip.setIndicatorColor(indicator);
        // tab的分割线颜色
        pagerSlidingTabStrip.setDividerColor(Color.TRANSPARENT);
        // tab背景
        pagerSlidingTabStrip.setBackgroundColor(background);
        // tab底线高度
        pagerSlidingTabStrip.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                1, getResources().getDisplayMetrics()));
        // 游标高度
        pagerSlidingTabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                6, getResources().getDisplayMetrics()));
//      选中的文字颜色
        pagerSlidingTabStrip.setSelectedTextColor(Color.WHITE);
//      正常文字颜色
        pagerSlidingTabStrip.setTextColor(textNormal);
        //text size
        pagerSlidingTabStrip.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                13, getResources().getDisplayMetrics()));
        // not expand
        pagerSlidingTabStrip.setShouldExpand(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment;
            switch (sectionNumber) {
                case 1:
                    fragment = new ContactsFragment();
                    break;
                default:
                    fragment = new PlaceholderFragment();
                    break;
            }
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_tabbed, container, false);
            return rootView;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        return super.onTouchEvent(event);
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
}
