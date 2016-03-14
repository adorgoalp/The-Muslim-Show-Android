package com.adorgolap.muslimshow.all.main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.adorgolap.muslimshow.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by ifta on 10/6/15.
 */
public class FullImageActivity extends Activity {
    Context context;
    TextView tvTitle, tvDescription,tvAddToFav;
    ImageView ivFullImage;
    ImageButton ibFavorite;
    String thumbId = null,text = null;
    private void init()
    {
        context = this;
        getActionBar().hide();
        thumbId = getIntent().getStringExtra("id");
        text = getIntent().getStringExtra("text");
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvDescription = (TextView) findViewById(R.id.tvTextAboutPicture);
        ivFullImage = (ImageView) findViewById(R.id.ivFullImage);
        ibFavorite = (ImageButton)findViewById(R.id.ibAddtoFavorite);
        tvAddToFav = (TextView)findViewById(R.id.tvAddToFavorite);
        //check if is fav
//        ParseObject f = new ParseObject("Favorite");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Favorite");
        query.fromLocalDatastore();
        query.whereEqualTo("thumbId", thumbId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    ibFavorite.setBackgroundResource(R.drawable.fav);
                    tvAddToFav.setText("In favorite");
                } else {
                    ibFavorite.setBackgroundResource(R.drawable.nfav);
                    tvAddToFav.setText("Add to favorite");
                }
            }
        });
        ibFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.ibAddtoFavorite) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Favorite");
                    query.fromLocalDatastore();
                    query.whereEqualTo("thumbId", thumbId);
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {
                               object.unpinInBackground();
                                ibFavorite.setBackgroundResource(R.drawable.nfav);
                                tvAddToFav.setText("Add to favorite");
                                changeRating(-1);
                            } else {
                                ibFavorite.setBackgroundResource(R.drawable.fav);
                                tvAddToFav.setText("In favorite");
                                ParseObject favorite = new ParseObject("Favorite");
                                favorite.put("thumbId", thumbId);
                                favorite.pinInBackground();
                                changeRating(1);
                            }
                        }
                    });
                }
            }
        });
    }

    private void fetchDataAndShow()
    {
        String title = getIntent().getStringExtra("title");
        if (title == null) {
            tvTitle.setText("");
        } else {
            tvTitle.setText(title);
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean loadHigh = sp.getBoolean("shouldLoadHigh", false);
        String tableName = "Picture";
        if(loadHigh)
        {
            tableName = "High";
        }
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(tableName);
        query.whereEqualTo("thumbId", thumbId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if(objects.size() == 0)
                    {
                        tvDescription.setText("No object");
                        ivFullImage.setImageResource(R.drawable.no_image);
                    }else {
                        ParseObject object = objects.get(0);
                        try {
                            ParseFile parseFile = object.getParseFile("picture");
                            byte[] bytes = parseFile.getData();
                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            ivFullImage.setImageBitmap(bmp);
                        } catch (ParseException e1) {
                            ivFullImage.setImageResource(R.drawable.no_image);
                            e1.printStackTrace();
                        }
//                        String description = object.getString("text");
                        if (text == null) {
                            tvDescription.setText("");
                        } else {
                            findViewById(R.id.llFav).setVisibility(View.VISIBLE);
                            tvDescription.setVisibility(View.VISIBLE);
                            tvDescription.setText(text);
                        }
                    }
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                }else {
                    tvDescription.setVisibility(View.VISIBLE);
                    tvDescription.setText("Error");
                    ivFullImage.setImageResource(R.drawable.no_image);
                }
            }


        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image_description_layout);
        init();
        fetchDataAndShow();
    }

    private void checkPreference() {

    }

    @Override
    protected void onPause() {
        super.onPause();
//        finish();
    }
    private  void  changeRating(final int change)
    {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Thumb");
        query.getInBackground(thumbId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject result, ParseException e) {
                int currentRating = result.getInt("rating");
                result.put("rating",currentRating+change);
                result.saveInBackground();
            }
        });
    }

}
