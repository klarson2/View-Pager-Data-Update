package com.krislarson.viewpagerdataupdate;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private String[] children;

    private CharSequence[] infoTitlesArray;

    SparseArray<DataUpdateListener> mListenerMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListenerMap = new SparseArray<>();

        infoTitlesArray = new String[] {
              "Info Title 1",
              "Info Title 2",
              "Info Title 3",
              "Info Title 4",
              "Info Title 5",
        };

        children = new String[infoTitlesArray.length];

        String time = new Date().toString();
        for (int i = 0; i < children.length; i++) {
            children[i] = getResources().getString(R.string.large_text, time);
        }

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new CollectionPagerAdapter(getSupportFragmentManager()));

        new DataAsyncTask().execute();
    }

    public void addDataUpdateListener(DataUpdateListener listener) {
        mListenerMap.put(listener.getPage(), listener);
    }

    public void removeDataUpdateListener(DataUpdateListener listener) {
        mListenerMap.remove(listener.getPage());
    }

    private void notifyUpdateListener(int page, String data) {
        DataUpdateListener listener = mListenerMap.get(page);
        if (listener != null) {
            listener.onDataUpdated(data);
        }
    }

    String getData(int page) {
        return children[page];
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

    public static class ObjectFragment extends Fragment implements DataUpdateListener {

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
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mPage = getArguments().getInt("page");
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

            ((MainActivity) getActivity()).addDataUpdateListener(this);

            return rootView;
        }

        @Override
        public void onDestroyView() {
            ((MainActivity) getActivity()).removeDataUpdateListener(this);
            super.onDestroyView();
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            int scrollY = scrollView.getScrollY();
            outState.putInt("scrollY", scrollY);
            super.onSaveInstanceState(outState);
        }

        @Override
        public int getPage() {
            return mPage;
        }

        @Override
        public void onDataUpdated(String data) {
            textView.setText(data);
            Log.d("ObjectFragment", "onDataUpdated(), page " + mPage);
        }
    }

    class DataAsyncTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {
                while (!isCancelled()) {
                    Thread.sleep(1000);
                    String time = new Date().toString();
                    publishProgress(time);
                }
            } catch (InterruptedException e) {
                Log.e("DataAsyncTask", "doInBackground", e);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            for (int i = 0; i < children.length; i++) {
                children[i] = getResources().getString(R.string.large_text, values[0]);
                notifyUpdateListener(i, children[i]);
            }
        }
    }

    interface DataUpdateListener {

        int getPage();

        void onDataUpdated(String data);
    }
}
