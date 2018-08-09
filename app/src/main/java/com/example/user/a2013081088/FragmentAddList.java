package com.example.user.a2013081088;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FragmentAddList extends Fragment {     //추가 프래그먼트(수정 액티비티와 비슷한 구조)

    wListDBHelper wLHelper;
    SQLiteDatabase sqlDB;
    EditText edtWish, edtTimeLimit; //위시, 시간제한을 적을 에디트텍스트
    Spinner spinnerRank;            //우선순위를 설정 할 스피너
    CalendarView calendar;          //시간제한을 보조적으로 설정 할 수 있는 캘린더뷰(캘린더 뷰에서 날짜 선택 시 현재 날짜와 비교하여 차이만큼 시간제한 자동으로 출력 해 줌)
    Button btnAdd;                  //저장 버튼
    Integer priority_num;           //adaptSpinner 메소드에서 사용 할 변수

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_edt_list, container, false); //프래그먼트 뷰 인플레이트

        wLHelper = new wListDBHelper(getContext());
        edtWish = (EditText)view.findViewById(R.id.edtWish);
        edtTimeLimit = (EditText) view.findViewById(R.id.edtTimeLimit);
        spinnerRank = (Spinner) view.findViewById(R.id.spinnerRank);
        calendar = (CalendarView) view.findViewById(R.id.calendar);
        btnAdd = (Button) view.findViewById(R.id.btnAdd);

        adaptSpinner(); //스피너와 어댑터를 연결해 주는 메소드

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {  //캘린더뷰 날짜 바꿀 시 이벤트
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                try {           //스트링 타입을 데이트 타입으로 변환 중 예외가 발생할 수 있음
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");   //날짜 포맷
                    String pick;        //선택한 날짜(포맷으로 거르기 전)
                    if (year < 9) {
                        pick = year + "-0" + (month + 1) + "-" + dayOfMonth;    //ex) 2018-06-19
                    } else {
                        pick = year + "-" + (month + 1) + "-" + dayOfMonth;     //ex) 2018-10-19
                    }
                    String now = format.format(new Date()); //현재 날짜 스트링으로 변형(이 단계를 안할 시 날짜(년,월,일)제외한 시간(시,분,초) 때문에 정확한 차이가 나오지 않음(내일 선택해도 차이가 0일로 나옴)
                    Date nowDate = format.parse(now);       //현재날짜 스트링 다시 데이트형으로 변환
                    Date pickDate = format.parse(pick);     //선택한 날짜 데이트형으로 변환

                    long calDate = pickDate.getTime() - nowDate.getTime();
                    long calDateDays = calDate / (24 * 60 * 60 * 1000);      //계산한 시간(밀리초 단위)를 (시간*분*초*밀리초)로 나눔 -> 일 단위

                    if (calDateDays <= 0) { //계산된 날짜가 0미만(과거)일때 대화상자 출력
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        AlertDialog dialog = builder.setMessage("최소 1일 이후의 날짜를 선택하세요")
                                .setNegativeButton("확인", null)
                                .create();
                        dialog.show();
                        return;
                    }
                    edtTimeLimit.setText("" + calDateDays); //계산된 날짜 에디트텍스트에 출력
                } catch (ParseException e) {
                    Log.d("date", e.getMessage());  //예외처리
                }
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {  //저장버튼 클릭 시 이벤트 처리
            @Override
            public void onClick(View v) {
                String wish = edtWish.getText().toString(); //위시를 에디트 텍스트에서 가져옴
                Integer rank = Integer.parseInt(spinnerRank.getSelectedItem().toString());  //우선순위를 스피너에서 가져옴
                String strTimeLimit = edtTimeLimit.getText().toString();    //시간제한을 에디트텍스트에서 가져옴
                Integer timeLimit;  //시간제한

                if(wish.equals("") || rank.equals("")){     //위시나 우선순위가 비어있다면(둘다 Not Null) 대화상자 출력
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    AlertDialog dialog = builder.setMessage("위시와 우선순위를 정확히 입력 해 주세요!")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }
                if(strTimeLimit.equals("") || strTimeLimit.isEmpty())    //시간제한 에디트텍스트가 비어있다면 null 값 줌
                    timeLimit = null;
                else
                    timeLimit = Integer.parseInt(strTimeLimit);          //비어있지 않다면 에디트텍스트에서 시간제한 가져옴
                if(timeLimit < 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    AlertDialog dialog = builder.setMessage("0일보다 적은 시간제한은 불가능합니다.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }
                sqlDB = wLHelper.getWritableDatabase();
                sqlDB.execSQL("insert into wList(wPriority, wWish, wTimeLimit, wDate) values(" + rank + ",'" + wish + "'," + timeLimit +", strftime('%Y-%m-%d'));");
                //우선순위, 위시, 시간제한, 날짜(DB 에 날짜형태로 텍스트로 입력하는 문장) DB 입력
                sqlDB.close();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());    //완료 확인 대화상자 출력
                AlertDialog dialog = builder.setMessage("추가가 완료되었습니다!")
                        .setNegativeButton("확인", null)
                        .create();
                dialog.show();

                edtTimeLimit.setText("");
                edtWish.setText("");
                adaptSpinner();
            }
        });
        return view;
    }
    void adaptSpinner(){    //스피너에 어댑터 연결해 주는 메소드
        sqlDB = wLHelper.getReadableDatabase();
        Cursor cursor;
        cursor = sqlDB.rawQuery("select count(*) from wList", null);    //현재 존재하는 위시리스트 총 개수를 가져옴

        while(cursor.moveToNext()){
            priority_num = (cursor.getInt(0));  //개수를 변수에 넣어줌
        }
        cursor.close();
        sqlDB.close();

        ArrayList<String> num = new ArrayList<>();
        for(int i = priority_num+1; i > 0 ; i--){   //개수만큼부터 1까지 역순으로 어댑터에 연결할 어레이리스트에 넣어줌
            num.add(i+"");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, num);   //어댑터에 어레이리스트 연결
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //스피너 드롭다운 방식
        spinnerRank.setAdapter(adapter);    //스피너에 어댑터 연결
    }
}
