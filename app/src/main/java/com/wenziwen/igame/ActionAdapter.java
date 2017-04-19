package com.wenziwen.igame;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wenziwen.igame.bean.ActionBean;

import java.util.ArrayList;

/**
 * Created by wen on 2016/11/10.
 */
public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.Holder> {

    private Context context;
    private ArrayList<ActionBean> dataList = new ArrayList<>();

    public ActionAdapter(Context context) {
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.action_item_view, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        ActionBean actionBean = dataList.get(position);
        StringBuilder builder = new StringBuilder();
        builder.append("序号: ")
                .append(position)
                .append("\n")
                .append("区域:")
                .append(actionBean.getTargetRectangle().toString())
                .append("\n")
                .append("点击:");
//                .append(actionBean.getClickPoint().toString());
        holder.textView.setText(builder.toString());

        Glide.with(context)
                .load(actionBean.getPhotoPath())
                .override(200, 200)
                .into(holder.ivCover);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    public ArrayList<ActionBean> getDataList() {
        return dataList;
    }

    public void add(ActionBean actionBean) {
        dataList.add(actionBean);
        notifyItemInserted(dataList.size() - 1);
    }

    public void addAll(ArrayList<ActionBean> dataList) {
        if (dataList != null) {
            this.dataList.addAll(dataList);
        }
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView ivCover;
        public Holder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_desc);
            ivCover = (ImageView) itemView.findViewById(R.id.iv_cover);
        }
    }
}
