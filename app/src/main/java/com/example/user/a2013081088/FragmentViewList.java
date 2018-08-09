package com.example.user.a2013081088;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class FragmentViewList extends Fragment implements SwipeRefreshLayout.OnRefreshListener {    //목록 프래그먼트 클래스. 아래로 스와이프 할 시 새로고침 위해 리스너 상속

    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    RecyclerViewAdapter mAdapter;
    wListDBHelper wLHelper;
    SQLiteDatabase sqlDB;
    SwipeRefreshLayout mSwipeRefreshLayout;     //아래로 스와이프 시, 새로고침 하기위함

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_list, container, false);    //프래그먼트 레이아웃 인플레이트

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rclView);
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        wLHelper = new wListDBHelper(getContext());
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);

        // ArrayList 에 Item 객체(데이터) 넣기
        ArrayList<Item> items = new ArrayList();
        sqlDB = wLHelper.getWritableDatabase();
        Cursor cursor;
        cursor = sqlDB.rawQuery("Select wPriority, wWish, wTimeLimit, wDate from wList order by wPriority", null);
        //우선순위, 위시, 시간제한, 날짜를 DB 에서 가져옴

        while (cursor.moveToNext()) {     //DB 에서 받아온 값들을 item 에 넣어 줌
            items.add(new Item(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3)));
        }

        cursor.close(); //커서 사용 후 닫음
        sqlDB.close();  //디비 사용 후 닫음
        // LinearLayout으로 설정
        mRecyclerView.setLayoutManager(mLayoutManager);
        // Animation Defualt 설정
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // Decoration 설정
        // mRecyclerView.addItemDecoration(new RecyclerViewDecoration(this, RecyclerViewDecoration.VERTICAL_LIST));
        // Adapter 생성
        mAdapter = new RecyclerViewAdapter(items);
        mRecyclerView.setAdapter(mAdapter);

        /*mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {         //익명내부클래스로도 할 수 있음(implements 안해도 됨)
            @Override
            public void onRefresh() {
                mAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "새로고침 되었습니다!", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });*/
        mSwipeRefreshLayout.setOnRefreshListener(this);     //위에서 아래로 스와이프 시 새로고침. <- 데이터(눈에 안보이는) 새로고침은 되나, 리싸이클러뷰 안의 텍스트(눈에 보이는 데이터)를 어떻게 바꿔야할지 고민..

        return view;    //뷰 반환
    }

    @Override
    public void onRefresh() {       //OnRefreshListener 상속 시, 필수 오버라이드. 새로고침 하는 메소드
        mRecyclerView.postDelayed(new Runnable() {      //0.5초 후 새로고침 완료
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();        //데이터 새로고침(리싸이클러뷰의 텍스트를 고치진 못함)
                mSwipeRefreshLayout.setRefreshing(false);   //이 문장 기술 안할 시, 새로고침 이미지가 끝없이 빙글빙글 돔
            }
        }, 500);    //0.5초의 대기시간
    }
}
