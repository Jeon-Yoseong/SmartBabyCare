package com.ssu.sangjunianjuni.smartbabycare.babyboard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ssu.sangjunianjuni.smartbabycare.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by yoseong on 2017-07-07.
 */

public class BoardListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    Context context;
    List<BabyBoardItem> data;

    String USER_ID;
    String BoardUSER_ID;
    String BoardDate;

    public BoardListAdapter(Context context, List<BabyBoardItem> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // recycler view에 반복될 아이템 레이아웃 연결
        LayoutInflater layoutInflater =(LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.babyboard_item, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BabyBoardItem babyBoardItem = data.get(position);
        CardViewHolder cardViewHolder = (CardViewHolder) holder;

        cardViewHolder.author.setText(babyBoardItem.getWriter());
        cardViewHolder.title.setText(babyBoardItem.getTitle());
        cardViewHolder.date.setText(babyBoardItem.getDate());
//        cardViewHolder.board_Imageview.setImageURI(Uri.parse(babyBoardItem.getPHOTO_URI()));

        // glide 라이브러리를 사용해서 이미지 로딩 속도를 현저하게 줄여 RecyvlerView의 스크롤 속도 느려짐 해결
        Glide.with(context).load(babyBoardItem.getPHOTO_URI()).into(cardViewHolder.board_Imageview);
//        cardViewHolder.board_Imageview.setImageBitmap(getProfilePhoto(babyBoardItem));
/*
        final Bitmap[] bitmap = new Bitmap[1];

        String finalServerPhotoPath = babyBoardItem.getPHOTO_URI();
        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(finalServerPhotoPath);

                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream inputStream = conn.getInputStream();
                    bitmap[0] = BitmapFactory.decodeStream(inputStream);
                } catch (IOException e){

                }
            }
        };
        mThread.start();

        try {
            mThread.join();

            cardViewHolder.board_Imageview.setImageBitmap(bitmap[0]);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
*/

/*        if(babyBoardItem.getWriter().equals(babyBoardItemPhoto.getUSER_ID())) {

        }
*/
        USER_ID = babyBoardItem.getUSER_ID();
        BoardUSER_ID = babyBoardItem.getWriter();
        BoardDate = babyBoardItem.getDate();

        Log.i("Recycler", BoardUSER_ID);
        Log.i("Recycler", BoardDate);

//        cardViewHolder.cardView.setOnClickListener(this);

        cardViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent page_to_content = new Intent(context, BabyBoardContents.class);
                page_to_content.putExtra("USER_ID", babyBoardItem.getUSER_ID());
                page_to_content.putExtra("BoardUSER_ID", babyBoardItem.getWriter());
                page_to_content.putExtra("BoardDate", babyBoardItem.getDate());

                page_to_content.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(page_to_content);
                // 페이지를 종료 시키기 위해 선언
                BabyBoardPage babyBoardPageActivity = (BabyBoardPage) BabyBoardPage.BabyBoardPageActivity;
                babyBoardPageActivity.finish();
            }
        });
    }
/*
    public Bitmap getProfilePhoto(BabyBoardItem babyBoardItem) {
        final Bitmap[] bitmap = new Bitmap[1];

        String finalServerPhotoPath = babyBoardItem.getPHOTO_URI();
        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(finalServerPhotoPath);

                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream inputStream = conn.getInputStream();
                    bitmap[0] = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                } catch (IOException e){

                }
            }
        };
        mThread.start();

        try {
            mThread.join();

            return bitmap[0];
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
*/
    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onClick(View v) {
        Intent page_to_content = new Intent(context, BabyBoardContents.class);
        page_to_content.putExtra("USER_ID", USER_ID);
        page_to_content.putExtra("BoardUSER_ID", BoardUSER_ID);
        page_to_content.putExtra("BoardDate", BoardDate);

        page_to_content.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(page_to_content);
        // 페이지를 종료 시키기 위해 선언
        BabyBoardPage babyBoardPageActivity = (BabyBoardPage) BabyBoardPage.BabyBoardPageActivity;
        babyBoardPageActivity.finish();
    }

    /**
     * Item layout 불러오기
     */
    public class CardViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView date;
        ImageView board_Imageview;
        TextView author;
        CardView cardView;

        public CardViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.board_tv_title);
            date =  (TextView) v.findViewById(R.id.board_tv_date);
            board_Imageview = (ImageView) v.findViewById(R.id.board_imageView);
            author = (TextView) v.findViewById(R.id.board_tv_writer);
            cardView = (CardView) v.findViewById(R.id.board_card_view);

        }
    }
}
