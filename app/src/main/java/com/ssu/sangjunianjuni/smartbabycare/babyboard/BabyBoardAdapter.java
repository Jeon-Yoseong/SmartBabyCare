package com.ssu.sangjunianjuni.smartbabycare.babyboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ssu.sangjunianjuni.smartbabycare.R;

import java.util.ArrayList;

/**
 * 게시판 리스트뷰 어댑터
 * Created by yoseong on 2017-04-29.
 */

public class BabyBoardAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 Arraylist
    private ArrayList<BabyBoardItem> babyBoardItemLIst = new ArrayList<BabyBoardItem>();

    //생성자
    public BabyBoardAdapter() {}

    // Adapter에 사용되는 데이터의 개수 리턴
    @Override
    public int getCount() {
        return babyBoardItemLIst.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용할 View 리턴
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // BabyBoard_item Layout을 inflate하여 converView 참조 획득
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.baby_board_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로 부터 위젯에 대한 참조 획득

        TextView titleTextView = (TextView) convertView.findViewById(R.id.boardTitle);
        TextView writerTextView = (TextView) convertView.findViewById(R.id.boardWriter);
        TextView dateTextView = (TextView) convertView.findViewById(R.id.boardDate);

        // Data Set(BabyBoardItemList)에서 position에 위치한 데이터 참조 획득
        BabyBoardItem babyBoardItem = babyBoardItemLIst.get(position);

        // 아이템 내 각 위젯에 데이터 반영

        titleTextView.setText(babyBoardItem.getTitle());
        writerTextView.setText(babyBoardItem.getWriter());
        dateTextView.setText(babyBoardItem.getDate());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터 리턴
    @Override
    public Object getItem(int position) {
        return babyBoardItemLIst.get(position);
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템의 ID를 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 아이템 데이터 추가를 위한 함스
    public void addItem( String title, String writer, String date) {
        BabyBoardItem item = new BabyBoardItem();

        item.setTitle(title);
        item.setWriter(writer);
        item.setDate(date);

        babyBoardItemLIst.add(item);
    }

}
