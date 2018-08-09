package com.example.user.a2013081088;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {   //메인 클래스

    FragmentViewList fragmentViewList;      //목록 보기 프래그먼트
    FragmentAddList fragmentAddEditList;    //목록 추가 프래그먼트
    FragmentHistory fragmentHistory;        //히스토리 목록 보기 프래그먼트
    wListDBHelper wLHelper;                 //DB 헬퍼 클래스
    SQLiteDatabase sqlDB;                   //SQLite 사용

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener         //바텀네비게이션 아이템 클릭 이벤트
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_showList:          //첫번째 목록 보기
                    fragmentViewList = new FragmentViewList();
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentViewList).commit();
                    setTitle(R.string.app_titleList);
                    return true;            //false 를 리턴시, 네비게이션 아이템 클릭해도 아이템 색이 변하지 않음
                case R.id.navigation_addList:           //두번째 목록 추가
                    fragmentAddEditList = new FragmentAddList();
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentAddEditList).commit();
                    setTitle(R.string.app_titleAdd);
                    return true;
                case R.id.navigation_showHistory:       //세번째 히스토리 목록
                    fragmentHistory = new FragmentHistory();
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentHistory).commit();
                    setTitle(R.string.app_titleHistory);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("위시리스트 - 목록");

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        wLHelper = new wListDBHelper(MainActivity.this);   //DB 헬퍼 생성
        sqlDB = wLHelper.getWritableDatabase();
        wLHelper.createTable_start(sqlDB);                          //맨 처음 시작 시, 사용할 테이블이 존재하지 않을경우 테이블 생성.
    }                                                               //테이블이 존재한다면 스킵.

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {                 //초기화 메뉴 생성
        getMenuInflater().inflate(R.menu.reset, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {           //초기화 메뉴 클릭 시 DB 초기화 후 토스트 출력
        int id = item.getItemId();
        if(id == R.id.reset){
            wLHelper = new wListDBHelper(MainActivity.this);
            sqlDB = wLHelper.getWritableDatabase();
            wLHelper.onUpgrade(sqlDB, 1, 2);    //DB 초기화
            sqlDB.close();

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            AlertDialog dialog = builder.setMessage("DB가 모두 초기화 되었습니다.")
                    .setNegativeButton("확인", null)
                    .create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
