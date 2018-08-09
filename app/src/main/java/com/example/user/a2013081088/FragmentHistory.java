package com.example.user.a2013081088;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class FragmentHistory extends Fragment { //히스토리 프래그먼트 클래스

    RecyclerView mRecyclerView;         //리싸이클러뷰 사용
    LinearLayoutManager mLayoutManager;
    RecyclerViewHistoryAdapter mAdapter;
    wListDBHelper wLHelper;
    SQLiteDatabase sqlDB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);  //프래그먼트 레이아웃 인플레이트

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rclViewHistory);
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        wLHelper = new wListDBHelper(getContext());

        // ArrayList 에 Item 객체(데이터) 넣기
        ArrayList<Item> items = new ArrayList();

        sqlDB = wLHelper.getWritableDatabase();
        Cursor cursor;
        cursor = sqlDB.rawQuery("Select fWish, fDate, fFinish from fList order by fDate", null);
        //위시, 날짜, 완료형식을 DB 에서 가져옴

        while(cursor.moveToNext()){     //DB 에서 받아온 값들을 item 에 넣음
            items.add(new Item(cursor.getString(0), cursor.getString(1), cursor.getInt(2)));
        }

        cursor.close(); //커서 사용 후 닫음
        sqlDB.close();  //DB 사용 후 닫음
        // LinearLayout으로 설정
        mRecyclerView.setLayoutManager(mLayoutManager);
        // Animation Defualt 설정
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // Adapter 생성
        mAdapter = new RecyclerViewHistoryAdapter(items);
        mRecyclerView.setAdapter(mAdapter);

        return view;    //뷰 반환
    }
}
