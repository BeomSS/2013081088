package com.example.user.a2013081088;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {     //리싸이클러뷰 어탭터 클래스
    private ArrayList<Item> mItems;
    private Context mContext;
    private wListDBHelper wLHelper;
    private SQLiteDatabase sqlDB;

    RecyclerViewAdapter(ArrayList itemList) {
        mItems = itemList;
    }

    //View 생성, ViewHolder 호출
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {      //뷰홀더 생성
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);    //아이템 레이아웃 inflate 하여 사용
        mContext = parent.getContext();
        RecyclerViewHolder holder = new RecyclerViewHolder(v);
        return holder;
    }

    //재활용되는 View 가 호출, Adapter 가 해당 position 에 해당하는 데이터를 결합
    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        // 해당 position 에 해당하는 데이터 결합
        holder.mPriority.setText(mItems.get(position).priority);        //우선순위, 위시, 시간제한, 날짜 값 지닌 텍스트를 받아와 텍스트뷰에 출력
        holder.mWish.setText(mItems.get(position).wish_content);
        holder.mTimeLimit.setText(mItems.get(position).time_limit);
        holder.mDate.setText(mItems.get(position).start_date_str);

        if (mItems.get(position).getIntTimeLimit() <= 7)         //시간제한이 7일 이하라면 시간제한 텍스트 빨간색으로 출력
            holder.mTimeLimit.setTextColor(Color.RED);
        else if (mItems.get(position).getIntTimeLimit() < 30)    //30일 미만이라면 주황색(대략)
            holder.mTimeLimit.setTextColor(Color.rgb(200, 200, 00));
        else                                                    //30일 이상이면 초록색
            holder.mTimeLimit.setTextColor(Color.GREEN);

        final String[] dlgSelect = new String[]{"수정하기", "완료하기", "포기하기"};    //아이템 클릭 시 나올 대화상자 아이템의 이름
        wLHelper = new wListDBHelper(mContext);

        // 생성된 List 중 클릭 시 이벤트 발생(대화상자 팝업)
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(mContext);
                dlg.setTitle("작업 선택");
                dlg.setItems(dlgSelect, new DialogInterface.OnClickListener() {     //대화상자의 아이템 클릭 시 이벤트 처리
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sqlDB = wLHelper.getWritableDatabase();
                        switch (which) {
                            case 0:         //첫번째 아이템 클릭 시 수정창(액티비티) 보여줌
                                Intent intent = new Intent(mContext, EditActivity.class);
                                intent.putExtra("wPriority", mItems.get(position).getIntPriority());
                                intent.putExtra("wWish", mItems.get(position).wish_content);
                                intent.putExtra("wTimeLimit", mItems.get(position).getIntTimeLimit());
                                mContext.startActivity(intent);
                                break;
                            case 1:         //두번째 아이템 클릭 시 해당 아이템 내용을 완료리스트 테이블에 삽입 후 해당 아이템 내용 위시리스트 테이블에서 삭제. 완료유형 1번(완료)
                                sqlDB.execSQL("insert into fList(fWish, fFinish, fDate) values('" + mItems.get(position).wish_content + "', 1, strftime('%Y-%m-%d'));");
                                sqlDB.execSQL("delete from wList where wWish = '" + mItems.get(position).wish_content + "' and wPriority = " + mItems.get(position).getIntPriority() + " and wTimeLimit = " + mItems.get(position).getIntTimeLimit() + " and wDate = '" + mItems.get(position).getStart_date() + "';");
                                mItems.remove(position);
                                notifyDataSetChanged();
                                Toast.makeText(mContext, "와! 대단해요!", Toast.LENGTH_SHORT).show();
                                break;
                            case 2:         //세번째 아이템 클릭 시 위와 같지만 완료유형은 2번(실패)
                                sqlDB.execSQL("insert into fList(fWish, fFinish, fDate) values('" + mItems.get(position).wish_content + "', 2, strftime('%Y-%m-%d'));");
                                sqlDB.execSQL("delete from wList where wWish = '" + mItems.get(position).wish_content + "' and wPriority = " + mItems.get(position).getIntPriority() + " and wTimeLimit = " + mItems.get(position).getIntTimeLimit() + " and wDate = '" + mItems.get(position).getStart_date() + "';");
                                mItems.remove(position);
                                Toast.makeText(mContext, "너무 아쉬워요ㅠㅠ", Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                                break;
                        }
                        sqlDB.close();      //이벤트 끝난 후 DB 닫음
                    }
                });
                dlg.setPositiveButton("닫기", null);
                dlg.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }   //아이템 총 개수 반환

}
