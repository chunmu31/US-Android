package com.bcm.us_project.calen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bcm.us_project.R;

import java.util.ArrayList;

// 일정이 있을 시 추가하는 Adapter
public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {

    private ArrayList<EventList> mDataset;
    public EventListAdapter(ArrayList<EventList> searchDataSet) {
        mDataset = searchDataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice, parent, false);

        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.nameText.setText(mDataset.get(position).dateName);
        holder.locationText.setText(mDataset.get(position).location);
        holder.dateText.setText(mDataset.get(position).userDate);
        holder.memoText.setText(mDataset.get(position).dateMemo);

    }

    public EventList getItem(int position) {
        return mDataset.get(position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Click Event
    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View v, int pos);
    }

    // 한번 클릭시 and 길게 클릭시 ClickListener
    private OnItemClickListener mListener = null;
    private OnItemLongClickListener mLongListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mLongListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameText;
        public TextView locationText;
        public TextView dateText;
        public TextView memoText;

        public ViewHolder(View view) {
            super(view);

            nameText = (TextView) view.findViewById(R.id.eventName);
            locationText = (TextView) view.findViewById(R.id.eventLocation);
            dateText = (TextView) view.findViewById(R.id.eventDate);
            memoText = (TextView) view.findViewById(R.id.eventMemo);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition(); // 클릭된 아이템의 위치를 가지고 옴
                    if (pos != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(v, pos);
                    }
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        mLongListener.onItemLongClick(v, pos);
                    }
                    return true;
                }
            });

        }

    }

}
