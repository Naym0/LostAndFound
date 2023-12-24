package com.example.lostandfound;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LostItemsAdapter extends RecyclerView.Adapter<LostItemsAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<String> mData = new ArrayList<>();
    private static final String TAG = "GETTING SELECTED DATA!!!!!!";

    public LostItemsAdapter(Context mContext, ArrayList<String> mData){
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_lostfounditem,parent,false);
        return new LostItemsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.degree.setText(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView degree;

        public MyViewHolder(View itemView) {
            super(itemView);
            degree = itemView.findViewById(R.id.itemName);
        }
    }
}
