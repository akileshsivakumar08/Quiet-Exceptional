package com.example.allowrepeatcallers;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quietexceptional.R;

import java.util.ArrayList;

public class CTL_Adapter extends ArrayAdapter<String> {
    private Context mcontext;
    private int mResource;

    public CTL_Adapter(@NonNull Context context, int resource, @NonNull ArrayList<String> objects){
        super(context,resource, objects);
        this.mcontext=context;
        this.mResource=resource;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater=LayoutInflater.from(mcontext);
        convertView= layoutInflater.inflate(mResource,parent,false);
        if(getItem(position)!=null) {
            TextView customtextinrow = convertView.findViewById(R.id.customtextinrow);
            customtextinrow.setText(getItem(position));
        }
        return convertView;
    }

}
