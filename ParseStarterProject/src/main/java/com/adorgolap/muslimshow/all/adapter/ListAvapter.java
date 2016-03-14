package com.adorgolap.muslimshow.all.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.adorgolap.muslimshow.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ifta on 9/17/15.
 */
public class ListAvapter extends BaseAdapter {
    ArrayList<ListDataHolder> items;
    Context context = null;
    LayoutInflater inflater;

    public ListAvapter(Context context, ArrayList<ListDataHolder> listitems) {
        items = listitems;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public ListAvapter(Context context) {
        items = new ArrayList<ListDataHolder>();
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }
    public void add(ListDataHolder item)
    {
        if(!items.contains(item))
            items.add(item);
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ListItemHolder holder;
        if(convertView == null)
        {
            v = inflater.inflate(R.layout.start_list_item, null);
            holder = new ListItemHolder();
            holder.ivImage = (CircleImageView) v.findViewById(R.id.ivImageItem);
            holder.tvText = (TextView) v.findViewById(R.id.tvListItem);
            v.setTag(holder);
        }
        else {
            holder = (ListItemHolder)v.getTag();
        }
        ListDataHolder dataHolder = items.get(position);
        holder.ivImage.setImageBitmap(BitmapFactory.decodeByteArray(dataHolder.image,0,dataHolder.image.length));
        holder.tvText.setText(dataHolder.title);
        return v;

    }

    public ArrayList<ListDataHolder> getItems() {
        return items;
    }

    public void setItems(ArrayList<ListDataHolder> items) {
        this.items = items;
    }
//    @Override
//     public View getView(int position, View convertView, ViewGroup parent) {
//
//        View v = inflater.inflate(R.layout.start_list_item, null);
//        ListItemHolder singleView = new ListItemHolder();
//        singleView.ivImage = (CircleImageView) v.findViewById(R.id.ivImageItem);
//        singleView.tvText = (TextView) v.findViewById(R.id.tvListItem);
//        ListDataHolder model = items.get(position);
//        singleView.ivImage.setImageBitmap(BitmapFactory.decodeByteArray(model.image,0,model.image.length));
//        singleView.tvText.setText(model.title);
//        return v;
//
//    }

}
