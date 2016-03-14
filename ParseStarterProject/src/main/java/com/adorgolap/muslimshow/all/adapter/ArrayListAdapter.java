package com.adorgolap.muslimshow.all.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.adorgolap.muslimshow.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ifta on 11/3/15.
 */
public class ArrayListAdapter extends ArrayAdapter<ListDataHolder> {
    Context context = null;
    LayoutInflater inflater;
    public ArrayListAdapter(Context context, List<ListDataHolder> objects) {
        super(context, 0, objects);
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        ListDataHolder dataHolder = getItem(position);
        holder.ivImage.setImageBitmap(BitmapFactory.decodeByteArray(dataHolder.image, 0, dataHolder.image.length));
        holder.tvText.setText(dataHolder.title);
        return v;
    }
}
