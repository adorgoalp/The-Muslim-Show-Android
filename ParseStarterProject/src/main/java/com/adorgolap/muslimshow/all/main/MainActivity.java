/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.adorgolap.muslimshow.all.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.adorgolap.muslimshow.R;
import com.adorgolap.muslimshow.all.adapter.ArrayListAdapter;
import com.adorgolap.muslimshow.all.adapter.ListDataHolder;
import com.adorgolap.muslimshow.all.preference.Preference;
import com.nhaarman.listviewanimations.appearance.ViewAnimator;
import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    String listKey = "listkey";
    Activity activity = null;
    Context context;
    ListView lv;
    ArrayList<ListDataHolder> listItems = new ArrayList<ListDataHolder>();
    Button bLoadMore;
    ArrayListAdapter adapter;
    int limit = 5;
    int skip = 0;
    View footerView = null;
    LayoutInflater inflater;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        handleSavedState(savedInstanceState);
        handleListeners();



//        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    private void handleListeners() {
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    int count = lv.getCount();
                    if (lv.getLastVisiblePosition() == count - 1) {

                        loadData();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String thumbId = adapter.getItem(position).thumbId;
                Intent i = new Intent(MainActivity.this, FullImageActivity.class);
                i.putExtra("id", thumbId);
                i.putExtra("title", adapter.getItem(position).title);
                i.putExtra("text", adapter.getItem(position).text);
                startActivity(i);
            }
        });
    }

    private void initialize() {
        context = this;
        lv = (ListView) findViewById(R.id.listView);
        inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        adapter = new ArrayListAdapter(context, listItems);
    }

    private void handleSavedState(Bundle savedInstanceState) {
        if (savedInstanceState == null || !savedInstanceState.containsKey(listKey)) {
            ScaleInAnimationAdapter animationAdapter = new ScaleInAnimationAdapter(adapter);
            animationAdapter.setAbsListView(lv);

            lv.setAdapter(animationAdapter);
            loadData();
        } else {
            skip = savedInstanceState.getInt("skip");
            limit = savedInstanceState.getInt("limit");
            listItems = savedInstanceState.getParcelableArrayList(listKey);

            SwingBottomInAnimationAdapter animAdapter = new SwingBottomInAnimationAdapter(adapter);
            animAdapter.setAbsListView(lv);
            ViewAnimator viewAnimator = animAdapter.getViewAnimator();
            if(viewAnimator != null) {
                viewAnimator.setAnimationDelayMillis(200);
            }
            lv.setAdapter(animAdapter);

        }

    }

    private void loadData() {
        boolean hasConnection = checkInternetConnection();
        if(hasConnection) {
            lv.removeFooterView(footerView);
            footerView = inflater.inflate(R.layout.loading, null);
            lv.addFooterView(footerView);
            final ArrayList<ListDataHolder> tempList = new ArrayList<ListDataHolder>();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Thumb");
            query.setLimit(limit);
            query.setSkip(skip);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        skip += objects.size();
                        ListDataHolder temp = null;
                        for (ParseObject po : objects) {
                            ParseFile pFile = po.getParseFile("thumb");
                            try {
                                if (pFile == null) {

                                } else {
                                    byte[] bytes = pFile.getData();
                                    String text = po.getString("text");
                                    temp = new ListDataHolder(bytes, po.getString("title"), po.getObjectId(), text);
                                    tempList.add(temp);
                                }
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        }
                        for (ListDataHolder item : tempList) {
                            adapter.add(item);
                        }
                        lv.removeFooterView(footerView);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "No more or error "+e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            lv.removeFooterView(footerView);
            footerView = inflater.inflate(R.layout.no_internet, null);
            footerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(context,"hmm",Toast.LENGTH_SHORT).show();
                    lv.removeFooterView(footerView);
                    loadData();
                }
            });
            lv.addFooterView(footerView);
        }
    }

    private void removeAllFooters() {
        footerView = inflater.inflate(R.layout.no_internet,null);
        lv.removeFooterView(footerView);
        footerView = inflater.inflate(R.layout.loading,null);
        lv.removeFooterView(footerView);
    }

    private boolean checkInternetConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent i = new Intent(this, Preference.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        ArrayList<ListDataHolder> underlying = new ArrayList<ListDataHolder>();
        for(int i = 0 ; i < adapter.getCount() ; i++)
        {
            underlying.add(adapter.getItem(i));
        }
        outState.putParcelableArrayList(listKey, underlying);
        outState.putInt("skip", skip);
        outState.putInt("limit", limit);
        super.onSaveInstanceState(outState);

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
