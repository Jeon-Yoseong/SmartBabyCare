package com.ssu.sangjunianjuni.smartbabycare.Analysis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ssu.sangjunianjuni.smartbabycare.R;

import java.util.ArrayList;

/**
 * Created by kang on 2017-06-28.
 */

public class PoopAdapter extends BaseAdapter {

    private ArrayList<PoopItem> PoopItemLIst = new ArrayList<PoopItem>();

    @Override
    public int getCount() {
        return PoopItemLIst.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.poop_item, parent, false);
        }
        TextView time=(TextView)convertView.findViewById(R.id.poopTime);
        PoopItem poopitem=PoopItemLIst.get(position);
        time.setText(poopitem.getTime()+" 번");
        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return PoopItemLIst.get(position);
    }

    public void addItem(String time) {
        PoopItem item = new PoopItem();
        item.setTime(time);
        PoopItemLIst.add(item);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


}
