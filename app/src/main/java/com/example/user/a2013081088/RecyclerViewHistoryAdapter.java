package com.example.user.a2013081088;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class RecyclerViewHistoryAdapter extends RecyclerView.Adapter<RecyclerViewHolder>  { //히스토리 리싸이클러뷰 어댑터 클래스(기본적인 구조는 목록과 비슷함)
    private ArrayList<Item> mItems;
    private Context mContext;

    public RecyclerViewHistoryAdapter(ArrayList itemList) {
        mItems = itemList;
    }
    //View 생성, ViewHolder 호출
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {  //뷰홀더 생성
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);    //목록과 마찬가지로 아이템 레이아웃 사용
        mContext = parent.getContext();
        RecyclerViewHolder holder = new RecyclerViewHolder(v);
        return holder;
    }
    //재활용되는 View 가 호출, Adapter 가 해당 position 에 해당하는 데이터를 결합
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        // 해당 position 에 해당하는 데이터 결합

        if(mItems.get(position).finish_type == 1) {     //완료유형이 1번이라면 첫째줄에 성공 문구 출력 후 아이템 레이아웃을 초록색으로 변경
            holder.mPriority.setText("Success!!");
            holder.mLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rounded_green));    //자바코드에서 drawable 안의 xml 사용하는 문장
            holder.mLayout.setPadding(10, 10, 10, 10);  //백그라운드 변경 시 패딩이 사라지는 현상이 발견 되어 패딩값 따로 부여함
        }
        else if(mItems.get(position).finish_type == 2) {    //완료유형이 2번이면 첫째줄에 실패 문구 출력 후 아이템 레이아웃을 빨간색으로 변경
            holder.mPriority.setText("Fail!!");
            holder.mLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rounded_red));
            holder.mLayout.setPadding(10, 10, 10, 10);
        }
        holder.mWish.setText(mItems.get(position).wish_content);    //위시 텍스트뷰에 출력
        holder.mDate.setText(mItems.get(position).finish_date_str); //날짜 텍스트뷰에 출력
    }
    // 데이터 갯수 반환
    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
