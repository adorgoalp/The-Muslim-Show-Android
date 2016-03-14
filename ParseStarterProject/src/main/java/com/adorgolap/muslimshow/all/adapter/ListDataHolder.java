package com.adorgolap.muslimshow.all.adapter;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ifta on 9/17/15.
 */
public class ListDataHolder implements Parcelable{
    public byte[] image;
    public String title;
    public String thumbId;
    public String text;
    public ListDataHolder(byte[] image, String title, String thumbId,String text)
    {
        this.image = image;
        this.title = title;
        this.thumbId = thumbId;
        this.text = text;
    }
    public ListDataHolder()
    {}
    public ListDataHolder(Parcel in)
    {
        image = in.createByteArray();
        title = in.readString();
        thumbId = in.readString();
        text = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(image);
        dest.writeString(title);
        dest.writeString(thumbId);
        dest.writeString(text);
    }
    public static final Parcelable.Creator<ListDataHolder> CREATOR = new Parcelable.Creator<ListDataHolder>() {
        public ListDataHolder createFromParcel(Parcel in) {
            return new ListDataHolder(in);
        }

        public ListDataHolder[] newArray(int size) {
            return new ListDataHolder[size];
        }
    };
}
