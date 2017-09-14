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

public class HeartBeatAdapter extends BaseAdapter {

    private ArrayList<HeartbeatItem> HeartbeatItemLIst = new ArrayList<HeartbeatItem>();
    @Override
    public int getCount() {
        return HeartbeatItemLIst.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.heartbeat_item, parent, false);
        }
        TextView time=(TextView)convertView.findViewById(R.id.heartbeattime);
        TextView rate=(TextView)convertView.findViewById(R.id.heartbeatrate);
        HeartbeatItem heartbeatitem=HeartbeatItemLIst.get(position);
        time.setText("일시: "+heartbeatitem.getTime());
        rate.setText(heartbeatitem.getHeartbeat()+"bpm");
        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    public void addItem(String time, String heartbeat) {
        HeartbeatItem item = new HeartbeatItem();
        item.setTime(time);
        item.setHeartbeat(heartbeat);
        HeartbeatItemLIst.add(item);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


}
