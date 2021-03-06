package com.todayschedule.practice;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Frag1 extends Fragment
{
    public static final int REQUEST_CODE_INSERT = 1000;
    private View view;
    private String mTime;
    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy/M/d");
    private MemoDBHelper dbHelper;
    private ArrayList<String> list;
    private RecyclerView recyclerView;
    private TextAdapter textAdapter;
    private TextView textview;
    private AlertDialog.Builder builder;
    private SharedPref sharedPref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.frag1, container, false);

        Date date = new Date();
        mTime = mFormat.format(date);

        list = new ArrayList<>();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler0);
        textview=(TextView) view.findViewById(R.id.textview);
        sharedPref=new SharedPref(getActivity());

        if (sharedPref.loadNightModeState())
        {
            builder =  new AlertDialog.Builder(getActivity(), R.style.Dialog);
        }
        else
        {

            builder =  new AlertDialog.Builder(getActivity());
        }
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("????????? ??????");



        // dbHelper ???????????? ??????
        dbHelper = MemoDBHelper.getInstance(getActivity());

        // ?????????????????? LinearLayoutManager ?????? ??????
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // ????????? ?????? ??????
        textAdapter = new TextAdapter(list);

        // DB ?????? list ?????????
        getMemoCursor();

        // recyclerView ????????? ?????? ??????
        recyclerView.setAdapter(textAdapter);

        textAdapter.setOnItemClickListener(new TextAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(View v, int pos)
            {
                Intent intent = new Intent(getActivity(), addschedule.class); // ?????? ?????? ???????????? ??????
                intent.putExtra("SelectedDate", mTime);

                String[] params = {mTime};
                Cursor cursor = (Cursor) dbHelper.getReadableDatabase().query(MemoContract.MemoEntry.TABLE_NAME, null, "date=?", params, null, null, null);
                cursor.moveToPosition(pos);

                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry._ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_TITLE));
                String contents = cursor.getString(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry.COLUMN_NAME_CONTENTS));

                intent.putExtra("id",id);
                intent.putExtra("title",title);
                intent.putExtra("contents",contents);

                startActivityForResult(intent, REQUEST_CODE_INSERT);
            }
        });

        textAdapter.setOnItemLongClickListener(new TextAdapter.OnItemLongClickListener()
        {
            @Override
            public void onItemLongClick(View v, int pos)
            {
                //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                String[] params = {mTime};
                Cursor cursor = (Cursor) dbHelper.getReadableDatabase().query(MemoContract.MemoEntry.TABLE_NAME, null, "date=?", params, null, null, null);
                cursor.moveToPosition(pos);
                final int id = cursor.getInt(cursor.getColumnIndexOrThrow(MemoContract.MemoEntry._ID));

                builder.setTitle("?????? ??????");
                builder.setMessage("????????? ?????????????????????????");
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        int deletedCount = db.delete(MemoContract.MemoEntry.TABLE_NAME,MemoContract.MemoEntry._ID+"="+id,null);

                        if(deletedCount==0)
                        {
                            Toast.makeText(getActivity(), "?????? ??????", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            getMemoCursor();
                            Toast.makeText(getActivity(), "????????? ?????????????????????", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("??????",null);
                builder.show();
            }
        });
        return view;

    }

    // ????????? ???????????? Arraylist??? ???????????? ?????????
    private void getMemoCursor()
    {
        String[] params = {mTime};

        list.clear();

        Cursor cursor = dbHelper.getReadableDatabase().query(MemoContract.MemoEntry.TABLE_NAME, null, "date=?", params, null, null, null);

        while (cursor.moveToNext())
        {
            list.add(cursor.getString(cursor.getColumnIndex(MemoContract.MemoEntry.COLUMN_NAME_TITLE)));
        }

        // ???????????? ????????? ???????????? ????????? ??????
        if(list.size()>0)
        {
            textview.setVisibility(view.GONE);
        }
        else
        {
            textview.setVisibility(view.VISIBLE);
        }

        textAdapter.notifyDataSetChanged();
        ((MainActivity)MainActivity.mContext).show();

    }

    // ?????? ????????? ????????????
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE_INSERT)
        {
            getMemoCursor();
        }
    }
}
