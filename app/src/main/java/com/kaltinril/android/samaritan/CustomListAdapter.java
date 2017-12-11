package com.kaltinril.android.samaritan;

/**
 * Created by thisisme1 on 12/11/2017.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    ArrayList itemname = new ArrayList<String>();
    ArrayList imgid = new ArrayList<Bitmap>();
    ArrayList itemDesc = new ArrayList<String>();

    public CustomListAdapter(Activity context, ArrayList<String> itemname, ArrayList<Bitmap> imgid, ArrayList<String> itemDescription) {
        super(context, R.layout.mylist, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
        this.itemDesc=itemDescription;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);

        txtTitle.setText(itemname.get(position).toString());
        imageView.setImageBitmap((Bitmap)imgid.get(position));
        extratxt.setText(itemDesc.get(position).toString());
        return rowView;

    };
}