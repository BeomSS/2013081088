package com.example.user.a2013081088;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {   //뷰홀더 클래스(리싸이클러뷰에 넣을 뷰들을 가져옴)
    public TextView mPriority;     //우선순위, 성공/실패 문구 출력 할 텍스트뷰
    public TextView mWish;         //위시를 출력 할 텍스트뷰
    public TextView mTimeLimit;    //시간제한을 출력 할 텍스트뷰
    public TextView mDate;         //날짜를 출력 할 텍스트뷰
    public LinearLayout mLayout;   //성공 여부에 따라 색을 다르게 보일 레이아웃

    public RecyclerViewHolder(View itemView) { //생성자
        super(itemView);
        mPriority = (TextView) itemView.findViewById(R.id.viewPriority);
        mWish = (TextView) itemView.findViewById(R.id.viewWish);
        mTimeLimit = (TextView) itemView.findViewById(R.id.viewTimeLimit);
        mDate = (TextView) itemView.findViewById(R.id.viewDate);
        mLayout = (LinearLayout) itemView.findViewById(R.id.rclLayout);
    }
}
