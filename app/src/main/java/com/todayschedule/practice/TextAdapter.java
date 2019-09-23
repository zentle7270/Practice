package com.todayschedule.practice;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TextAdapter extends RecyclerView.Adapter<TextAdapter.ViewHolder>
{
    private ArrayList<String> mData = null;

    private Context mContext;
    private Cursor mCursor;

    public interface OnItemClickListener
    {
        void onItemClick(View v, int pos);
    }

    public interface OnItemLongClickListener
    {
        void onItemLongClick(View v, int pos);
    }

    private OnItemClickListener mListener = null;
    private OnItemLongClickListener mLongListener = null;


    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.mListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener)
    {
        this.mLongListener = listener;
    }

    //
    public TextAdapter(ArrayList<String> list)
    {
        mData = list;
    }

    // ViewHolder (화면에 표시될 아이템뷰 저장)
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textView1;

        ViewHolder(View itemView)
        {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //Intent intent = new Intent(v.getContext(),addschedule.class);
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION)
                    {
                        mListener.onItemClick(v, pos);
                    }
                    //v.getContext().startActivity(intent);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION)
                    {
                        mLongListener.onItemLongClick(v, pos);
                    }
                    return true;
                }
            });


            textView1 = itemView.findViewById(R.id.text1);
        }
    }


    // 아이템 뷰를 위한 뷰홀더 객체를 생성하고 리턴
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.recyclerview_item, parent, false);
        TextAdapter.ViewHolder vh = new TextAdapter.ViewHolder(view);

        return vh;
    }


    // position에 해당되는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        String text = mData.get(position);
        holder.textView1.setText(text);
    }


    @Override
    public int getItemCount()
    {
        return mData.size();
    }

    // 어댑터에 보관되어있는 커서를 새로운 것을 바꿔 UI 갱신
    public void swapCursor(Cursor newCursor)
    {

        // 이전 커서를 닫고
        if (mCursor != null)
            mCursor.close();

        // 새 커서로 업데이트
        mCursor = newCursor;

        // 리사이클러뷰 업데이트
        if (newCursor != null)
        {
            this.notifyDataSetChanged();
        }
    }

}
