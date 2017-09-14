package com.ssu.sangjunianjuni.smartbabycare.babyboard;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ssu.sangjunianjuni.smartbabycare.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 댓글 recyclerview 리스트 어댑터
 * Created by yoseong on 2017-06-02.
 */

public class BabyBoardReplyExpandableListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int HEADER = 0;
    public static final int CHILD = 1;

    private List<Item> data;

    public BabyBoardReplyExpandableListAdapter(List<Item> data) {
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view = null;
        View childView = null;

        switch (type) {
            // 부모부분 즉 '댓글' 나오게 하는 부분
            case HEADER:
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.baby_board_reply_header, parent, false);
                ListHeaderViewHolder header = new ListHeaderViewHolder(view);
                return header;
            // 자식부분, 댓글내용, 댓글작성자, 작성시간 나오는 layout 설정시키는 부분
            case CHILD:
                LayoutInflater inflater1 = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                childView = inflater1.inflate(R.layout.baby_board_reply_child, parent, false);
                ListChildViewHolder childHolder = new ListChildViewHolder(childView);
                return childHolder;
        }
        return null;
    }
    // view를 나오게 해주는 부분
    public void onBindViewHolder(RecyclerView.ViewHolder holder,int position) {
        final Item item = data.get(position);
        switch (item.type) {
            case HEADER:
                final ListHeaderViewHolder itemController = (ListHeaderViewHolder) holder;
                itemController.refferalItem = item;
                itemController.header_title.setText(item.text);
                if (item.invisibleChildren == null) {
                    itemController.btn_expand_toggle.setImageResource(R.drawable.ic_arrow_up);
                } else {
                    itemController.btn_expand_toggle.setImageResource(R.drawable.ic_arrow_down);
                }
                itemController.btn_expand_toggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.invisibleChildren == null) {
                            item.invisibleChildren = new ArrayList<Item>();
                            int count = 0;
                            int pos = data.indexOf(itemController.refferalItem);
                            while (data.size() > pos + 1 && data.get(pos + 1).type == CHILD) {
                                item.invisibleChildren.add(data.remove(pos + 1));
                                count++;
                            }
                            notifyItemRangeRemoved(pos + 1, count);
                            itemController.btn_expand_toggle.setImageResource(R.drawable.ic_arrow_down);
                        } else {
                            int pos = data.indexOf(itemController.refferalItem);
                            int index = pos + 1;
                            for (Item i : item.invisibleChildren) {
                                data.add(index, i);
                                index++;
                            }
                            notifyItemRangeInserted(pos + 1, index - pos - 1);
                            itemController.btn_expand_toggle.setImageResource(R.drawable.ic_arrow_up);
                            item.invisibleChildren = null;
                        }
                    }
                });
                break;
            case CHILD:
                final ListChildViewHolder childController = (ListChildViewHolder) holder;
                childController.childRefferalItem = item;
                childController.replyContents.setText(item.replyContents);
                childController.replyWriter.setText(item.replyWriter);
                childController.replyDate.setText(item.replyDate);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).type;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private static class ListHeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView header_title;
        public ImageView btn_expand_toggle;
        public Item refferalItem;

        public ListHeaderViewHolder(View itemView) {
            super(itemView);
            header_title = (TextView) itemView.findViewById(R.id.header_title);
            btn_expand_toggle = (ImageView) itemView.findViewById(R.id.btn_expand_toggle);
        }
    }

    private static class ListChildViewHolder extends RecyclerView.ViewHolder {
        public TextView replyContents;
        public TextView replyWriter;
        public TextView replyDate;
        public Item childRefferalItem;

        public ListChildViewHolder(View itemView) {
            super(itemView);
            replyContents = (TextView) itemView.findViewById(R.id.replyContents);
            replyWriter = (TextView) itemView.findViewById(R.id.replyWriter);
            replyDate = (TextView) itemView.findViewById(R.id.replyDate);
        }
    }

    public static class Item {
        public int type;
        public String text;
        public String replyContents;
        public String replyWriter;
        public String replyDate;
        public List<Item> invisibleChildren;

        public Item() {
        }

        public Item(int type, String text) {
            this.type = type;
            this.text = text;
        }

        public Item(int type, String replyContents, String replyWriter, String replyDate){
            this.type = type;
            this.replyContents = replyContents;
            this.replyWriter = replyWriter;
            this.replyDate = replyDate;
        }
    }

}
