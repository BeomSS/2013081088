package com.example.user.a2013081088;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditActivity extends AppCompatActivity {   //수정 액티비티(추가 프래그먼트와 비슷한 구조)
    wListDBHelper wLHelper;
    SQLiteDatabase sqlDB;
    EditText edtWish, edtTimeLimit; //위시, 시간제한을 적을 에디트텍스트
    Spinner spinnerRank;            //우선순위를 설정 할 스피너
    CalendarView calendar;          //시간제한을 보조적으로 설정 할 수 있는 캘린더뷰(캘린더 뷰에서 날짜 선택 시 현재 날짜와 비교하여 차이만큼 시간제한 자동으로 출력 해 줌)
    Button btnAdd;                  //저장 버튼
    Integer priority_num;           //adaptSpinner 메소드에서 사용 할 변수

    Integer wPriority, wTimeLimit;  //전 화면에서 받아 올 값을 넣을 변수. 우선순위, 시간제한
    String wWish;                   //위시

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_edt_list);
        setTitle("위시리스트 - 수정");

        Intent intent = getIntent();
        wPriority = intent.getIntExtra("wPriority", 1);     //전 화면에서 값을 받아옴
        wWish = intent.getStringExtra("wWish");
        wTimeLimit = intent.getIntExtra("wTimeLimit", 0);

        wLHelper = new wListDBHelper(EditActivity.this);
        edtWish = (EditText)findViewById(R.id.edtWish);
        edtTimeLimit = (EditText) findViewById(R.id.edtTimeLimit);
        spinnerRank = (Spinner) findViewById(R.id.spinnerRank);
        calendar = (CalendarView) findViewById(R.id.calendar);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        adaptSpinner(); //스피너와 어댑터를 연결해 주는 메소드

        spinnerRank.setSelection(spinnerRank.getCount() - wPriority);       //총 개수 - 기존 순위 (ex : 5개 중 2위 였다면, 5(0), 4(1), 3(2), 2(3), 1(4) 중 5 - 2 = 3. 3번은 2.)
        edtWish.setText(wWish);                                             //받아온 값들을
        edtTimeLimit.setText(wTimeLimit+"");                                //에디트텍스트, 스피너에 보여 줌

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {  //캘린더뷰 날짜 바꿀 시 이벤트
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                try {       //스트링 타입을 데이트 타입으로 변환 중 예외가 발생할 수 있음
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
                    long calDateDays = calDate / (24 * 60 * 60 * 1000);     //계산한 시간(밀리초 단위)를 (시간*분*초*밀리초)로 나눔 -> 일 단위

                    if (calDateDays <= 0) {     //계산된 날짜가 0미만(과거)일때 대화상자 출력
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
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
        btnAdd.setOnClickListener(new View.OnClickListener() {      //저장버튼 클릭 시 이벤트 처리
            @Override
            public void onClick(View v) {
                String wish = edtWish.getText().toString();     //위시를 에디트 텍스트에서 가져옴
                Integer rank = Integer.parseInt(spinnerRank.getSelectedItem().toString());  //우선순위를 스피너에서 가져옴
                String strTimeLimit = edtTimeLimit.getText().toString();    //시간제한을 에디트텍스트에서 가져옴
                Integer timeLimit;      //시간제한

                if(wish.equals("") || rank.equals("")){     //위시나 우선순위가 비어있다면(둘다 Not Null) 대화상자 출력
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                    AlertDialog dialog = builder.setMessage("위시와 우선순위를 정확히 입력 해 주세요!")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                if(strTimeLimit.equals("") || strTimeLimit.isEmpty())   //시간제한 에디트텍스트가 비어있다면 null 값 줌
                    timeLimit = null;
                else
                    timeLimit = Integer.parseInt(strTimeLimit);         //비어있지 않다면 에디트텍스트에서 시간제한 가져옴
                if(timeLimit < 0){      //시간제한이 0일 미만이면 대화상자 출력
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                    AlertDialog dialog = builder.setMessage("0일보다 적은 시간제한은 불가능합니다.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }
                sqlDB = wLHelper.getWritableDatabase();
                sqlDB.execSQL("update wList set wPriority=" + rank + ", wWish = '" + wish + "', wTimeLimit = " + timeLimit + " where wPriority = " + wPriority + " and wWish is '" + wWish + "' and wTimeLimit = " + wTimeLimit + ";");
                //우선순위, 위시, 시간제한 DB 수정
                sqlDB.close();
                AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);   //완료 확인 대화상자 출력
                AlertDialog dialog = builder.setMessage("수정이 완료되었습니다!")
                        .setNegativeButton("확인", null)
                        .create();
                dialog.show();
                finish();
            }
        });

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
        for(int i = priority_num; i > 0 ; i--){     //개수만큼부터 1까지 역순으로 어댑터에 연결할 어레이리스트에 넣어줌
            num.add(i+"");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditActivity.this, android.R.layout.simple_spinner_item, num);  //어댑터에 어레이리스트 연결
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //스피너 드롭다운 방식
        spinnerRank.setAdapter(adapter);    //스피너에 어댑터 연결
    }
}
