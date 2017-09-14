package com.ssu.sangjunianjuni.smartbabycare.BabyDiary;

import android.content.Context;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ssu.sangjunianjuni.smartbabycare.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yoseong on 2017-07-06.
 */

public class DiaryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<BabyDiaryItem> data;

    public DiaryListAdapter(Context context, List<BabyDiaryItem> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // recycler view에 반복될 아이템 레이아웃 연결
        LayoutInflater layoutInflater =(LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.babydiary_item, parent, false);
        //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.babydiary_item, null);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BabyDiaryItem babyDiaryItem = data.get(position);
        CardViewHolder cardViewHolder = (CardViewHolder) holder;

        String title = babyDiaryItem.getTitle();
        String content = babyDiaryItem.getContent();

        if(title.length() > 10) {
            title = title.substring(0, 10) + "..."; // 10자 자르고 ... 붙이기
        }

        if(content.length() > 50) {
            content = content.substring(0, 50) + "..."; // 50자 자르고 ... 붙이기
        }

        if(babyDiaryItem.getDiaryPhoto().equals("")) {
            cardViewHolder.rl_image.setVisibility(View.GONE);
        } else {
            
            cardViewHolder.iv_image.setImageURI(Uri.parse(babyDiaryItem.getDiaryPhoto()));
        }
        System.out.println("uri: "+babyDiaryItem.getDiaryPhoto());
        cardViewHolder.diary_ImageView.setImageURI(Uri.parse(babyDiaryItem.getPHOTO_URI()));
        cardViewHolder.author.setText(babyDiaryItem.getAuthor());
        cardViewHolder.title.setText(title);
        cardViewHolder.content.setText(content);
        cardViewHolder.date.setText(babyDiaryItem.getDate());
        cardViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent page_to_content = new Intent(context, BabyDiaryContents.class);
                page_to_content.putExtra("USER_ID", babyDiaryItem.getUSER_ID());
                page_to_content.putExtra("Title", babyDiaryItem.getTitle());
                page_to_content.putExtra("Content", babyDiaryItem.getContent());
                page_to_content.putExtra("Author", babyDiaryItem.getAuthor());
                page_to_content.putExtra("Date", babyDiaryItem.getDate());
                page_to_content.putExtra("DiaryPhoto", babyDiaryItem.getDiaryPhoto());
                page_to_content.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(page_to_content);
                // 페이지를 종료 시키기 위해 선언
                BabyDiaryPage babyDiaryPageActivity = (BabyDiaryPage) BabyDiaryPage.BabyDiaryPageActivity;
                babyDiaryPageActivity.finish();
            }
        });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    /**
     * Item layout 불러오기
     */
    public class CardViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView date;
        TextView content;
        TextView author;
        CardView cardView;
        ImageView iv_image;
        ImageView diary_ImageView;
        RelativeLayout rl_image;

        public CardViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.tv_title);
            date =  (TextView) v.findViewById(R.id.tv_date);
            content =  (TextView) v.findViewById(R.id.tv_content);
            author = (TextView) v.findViewById(R.id.tv_writer);
            cardView = (CardView) v.findViewById(R.id.card_view);
            iv_image = (ImageView) v.findViewById(R.id.iv_image);
            diary_ImageView = (ImageView) v.findViewById(R.id.diary_ImageView);
            rl_image = (RelativeLayout) v.findViewById(R.id.rl_image);
        }
    }
}
