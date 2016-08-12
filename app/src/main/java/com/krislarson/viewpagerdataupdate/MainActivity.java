package com.krislarson.viewpagerdataupdate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private String[] children;

    private CharSequence[] infoTitlesArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoTitlesArray = new String[] {
              "Info Title 1",
              "Info Title 2",
              "Info Title 3",
              "Info Title 4",
              "Info Title 5",
        };

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new CollectionPagerAdapter(getSupportFragmentManager()));

    }

    String getData(int page) {
        return getResources().getString(R.string.large_text);
    }

    class CollectionPagerAdapter  extends FragmentStatePagerAdapter {

        public CollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return infoTitlesArray.length;
        }

        @Override
        public Fragment getItem(int position) {
            return ObjectFragment.newInstance(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return infoTitlesArray[position];
        }
    }

    public static class ObjectFragment extends Fragment {

        private int mPage;

        private TextView textView;

        private ScrollView scrollView;

        public static ObjectFragment newInstance(int page) {
            ObjectFragment objectFragment = new ObjectFragment();
            Bundle args = new Bundle();
            args.putInt("page", page);
            objectFragment.setArguments(args);
            return objectFragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_collection_object, container, false);
            scrollView = (ScrollView) rootView;
            textView = ((TextView) rootView.findViewById(R.id.text));
            textView.setText(((MainActivity) getActivity()).getData(mPage));
            if (savedInstanceState != null) {
                final int scrollY = savedInstanceState.getInt("scrollY");
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.setScrollY(scrollY);
                    }
                });
            }
            return rootView;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            int scrollY = scrollView.getScrollY();
            outState.putInt("scrollY", scrollY);
            super.onSaveInstanceState(outState);
        }
    }
}
