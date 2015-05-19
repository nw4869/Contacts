package com.nightwind.contacts.activity;

import android.app.Dialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.nightwind.contacts.R;
import com.nightwind.contacts.fragment.ContactFragment;
import com.nightwind.contacts.fragment.ContactsFragment;
import com.nightwind.contacts.fragment.SearchResultListFragment;
import com.nightwind.contacts.model.Contacts;
import com.nightwind.contacts.widget.PagerSlidingTabStrip;

import java.io.IOException;
import java.util.Locale;

public class MainToolbarActivity extends AppCompatActivity {

    private GestureDetector mGestureDetector;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_toolbar);

        doSearchQuery(getIntent());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.person_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getSupportFragmentManager().beginTransaction()
//                        .add(R.id.container, ContactEditorFragment.newInstance(null))
//                        .addToBackStack(ContactEditorFragment.class.getSimpleName())
//                        .commit();
//                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//                getSupportActionBar().setHomeButtonEnabled(true);
                Intent intent = new Intent(MainToolbarActivity.this, PersonAddActivity.class);
                startActivity(intent);
            }
        });

        PagerSlidingTabStrip pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.pager);
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
     *  activity声明为singleTop模式
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {  //activity重新置顶
        super.onNewIntent(intent);
        doSearchQuery(intent);
    }

    /**
     *  从intent中获取信息，即要搜索的内容
     * @param intent
     */
    private void doSearchQuery(Intent intent){
        if(intent == null)
            return;

        String queryAction = intent.getAction();
        if( Intent.ACTION_SEARCH.equals( intent.getAction())){  //如果是通过ACTION_SEARCH来调用，即如果通过搜索调用
            String queryString = intent.getStringExtra(SearchManager.QUERY); //获取搜索内容
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, SearchResultListFragment.newInstance(queryString))
                        .addToBackStack(SearchResultListFragment.class.getSimpleName())
                        .commit();
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
        }

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
        // 获取SearchView对象
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if(searchView == null){
            Log.e("SearchView", "Fail to get Search View.");
            return true;
        }

//        // 获取搜索服务管理器
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        // searchable activity的component name，由此系统可通过intent进行唤起
//        ComponentName cn = new ComponentName(this, MainToolbarActivity.class);
//        // 通过搜索管理器，从searchable activity中获取相关搜索信息，就是searchable的xml设置。如果返回null，表示该activity不存在，或者不是searchable
//        SearchableInfo info = searchManager.getSearchableInfo(cn);
//        if(info == null){
//            Log.e("SearchableInfo", "Fail to get search info.");
//        }
//        // 将searchable activity的搜索信息与search view关联
//        searchView.setSearchableInfo(info);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true); // 缺省值就是true，可能不专门进行设置，true的输入框更大
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

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
        } else if (id == android.R.id.home) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportFragmentManager().popBackStack(SearchResultListFragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else if (id == R.id.action_delete_all) {
            Dialog dialog = new AlertDialog.Builder(this).setMessage(R.string.action_delete_all)
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                new Contacts(MainToolbarActivity.this).deleteAllContacts();
                                getCurrentFragment().reloadData();
                            } catch (RemoteException | OperationApplicationException e) {
                                e.printStackTrace();
                            }
                        }
                    }).create();
            dialog.show();

            return true;
        } else if (id == R.id.action_export) {
            try {
                new Contacts(this).exportContacts();
                Toast.makeText(this, R.string.export_success, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, R.string.export_failed, Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private PlaceholderFragment getCurrentFragment() {
        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + viewPager.getCurrentItem());
        return (PlaceholderFragment) page;
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        }
        return super.onKeyUp(keyCode, event);
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
                case 2:
                    fragment = ContactsFragment.newInstance(true);
                    break;
                default:
                    fragment = new PlaceholderFragment();
                    break;
            }
            Bundle args = fragment.getArguments();
            if (args == null) {
                args = new Bundle();
            }
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

        protected void reloadData() {
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
