package com.example.user.a2013081088;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class wListDBHelper extends SQLiteOpenHelper {   //DB 헬퍼 클래스
    public wListDBHelper(Context context){
        super(context, "wishListDB", null, 1);
    }   //생성자

    @Override
    public void onCreate(SQLiteDatabase db) {   //생성
        db.execSQL("create table wList (wNum integer primary key autoincrement , wPriority integer not null, wWish varchar(30) not null, wTimeLimit integer, wDate text not null);");
        db.execSQL("create table fList (fNum integer primary key autoincrement , fWish varchar(30) not null, fFinish integer not null, fDate text not null);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  //초기화
        db.execSQL("drop table if exists wList");
        db.execSQL("drop table if exists fList");
        onCreate(db);   //테이블 삭제 후 생성
    }

    void createTable_start(SQLiteDatabase db){  //실행 시 한번씩 불러내는 메소드. 테이블 존재 여부에 따라 테이블 생성.(없으면 생성)
        db.execSQL("create table if not exists wList (wNum integer primary key autoincrement , wPriority integer not null, wWish varchar(30) not null, wTimeLimit integer, wDate text not null);");
        db.execSQL("create table if not exists fList (fNum integer primary key autoincrement , fWish varchar(30) not null, fFinish integer not null, fDate text not null);");
    }
}
