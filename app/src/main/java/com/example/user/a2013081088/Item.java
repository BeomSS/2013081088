package com.example.user.a2013081088;

public class Item { //ArrayList 에 넣을 값들을 받아와 정리해 주는(?) 클래스(이렇게 이해하는게 맞는지 모르겠음...)

    String priority, wish_content, time_limit, start_date_str, start_date, finish_date_str, finish_date;
    //우선순위(출력용), 위시 내용, 시간제한(출력용), 등록날짜(출력용), 등록날짜, 완료날짜(출력용), 완료날짜
    Integer int_priority, int_time_limit, finish_type;  //우선순위, 시간제한, 완료유형

    public String getPriority() {
        return priority;
    }

    public String getWish() {
        return wish_content;
    }

    public String getTimeLimit() {
        return time_limit;
    }

    public String getStart_date_str() {
        return start_date_str;
    }

    public Integer getIntPriority() {
        return int_priority;
    }     //우선순위 반환(사용함)

    public Integer getIntTimeLimit() {
        return int_time_limit;
    }  //시간제한 반환(사용함)

    public String getStart_date() {
        return start_date;
    }          //등록날짜 반환(사용함)

    public String getfinish_date_str() {
        return finish_date_str;
    }

    public Integer getFinish_type() {
        return finish_type;
    }

    public Item(Integer priority, String wish_content, Integer time_limit, String start_date) { //우선순위, 위시 내용, 시간제한, 등록날짜 입력 들어올 시 생성자(위시리스트 목록 보여줄때)
        this.priority = "우선순위 : " + priority;                //출력용
        this.wish_content = wish_content;                        //출력
        this.time_limit = "TimeLimit : " + time_limit + "일";    //출력용
        this.start_date_str = "등록일 : " + start_date;          //출력용
        this.int_priority = priority;
        this.int_time_limit = time_limit;
        this.start_date = start_date;
    }

    public Item(String wish_content, String finish_date, Integer finish_type) {  //위시 내용, 완료날짜, 완료유형 입력 들어올 시 생성자(히스토리 목록 보여줄때)
        this.wish_content = wish_content;                  //출력
        this.finish_date_str = "완료일 : " + finish_date;  //출력용
        this.finish_date = finish_date;                    //출력용
        this.finish_type = finish_type;
    }
}
