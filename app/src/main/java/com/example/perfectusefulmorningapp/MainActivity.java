
package com.example.perfectusefulmorningapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/*
    프로젝트를 수정할 때 GitHub와 더불어 추가로 MainActivity 액티비티 클라스에 주석을 달도록 한다.

    2019-04-30 조완식
    기본적인 알람 어플 작성, 알람 목록과 추가, 온 오프를 할 수 있는 MainActivity(layout)과 알람이 울리면 intent로 열리는 AlarmActivity(layout)
    Main : 알람은 하나 등록, 하단의 버튼 누르면 알람 설정, 알람 옆 스위치로 on off
    alarm : 알람이 울리면 실행, 버튼 누르면 취소
    noti : 상단바 (1분전)알람 알리기, 간단한 취소 기능

    추가할 점 : 알람 여러개 등록 가능 -> 궁극적으로 7개의 알람 한 화면에 보이게 디자인 가능해야함
               한 화면에 보이면 디자인 배경 조정

               상단 배너 없애야함, 하단 버튼 말고 개별적으로 버튼으로 시간 조정 할 수 있어야 함
    궁극적으로 추가할 점
                :알람마다 설정 가능해야함 : 날씨 / 미세먼지 / 구글메일 / 메모 Main과 Alarm둘다
                :알람 끌 때 게임 활용(게임 알고리즘만 만들면 취소 버튼 대신에 넣으면 됨)


    2019_5_1 조완식
        뭐뭐수정한지
        추가할점
        궁국적으로 더 해야할점

        2019_5_12 김준성
        fabtn 버튼 삭제
        textView -> button 으로 변경
        button을 눌렀을 때, timetable이 나오지 않고
        옵션 list가 나오도록 함. (preference view)


 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{
//View.OnClickListener : 화면 하단 버튼 눌렀을떄
//RadioGroup:CheckBox 버튼 눌렀을때

    FloatingActionButton fabtn; //알람설정버튼

    TextView setTimeview;
    Switch setSwitch;

    TextView timeView;  //알람시간
    TextView timeView2;
    TextView timeView3;
    TextView timeView4;
    TextView timeView5;
    TextView timeView6;
    TextView timeView7;

    Switch aSwitch;     //스위치
    Switch aSwitch2;
    Switch aSwitch3;
    Switch aSwitch4;
    Switch aSwitch5;
    Switch aSwitch6;
    Switch aSwitch7;

    Button menuButton;
    Button menuButton2;
    Button menuButton3;
    Button menuButton4;
    Button menuButton5;
    Button menuButton6;
    Button menuButton7;

    SharedPreferences prefs;    //알람시간 설정 저장

    AlarmManager alarm;         //알람매니저

    PendingIntent preIntent;    //실제 알람 1분전 의뢰
    Intent aIntent;
    PendingIntent alarmIntent;  //실제 알람 의뢰

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return super.getSharedPreferences(name, mode);
    }

    boolean isClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setContentView(R.layout.activity_setting);

        //뷰들의 객체얻기
        timeView = (Button)findViewById(R.id.button1);
        timeView2 = (Button)findViewById(R.id.button2);
        timeView3 = (Button)findViewById(R.id.button3);
        timeView4 = (Button)findViewById(R.id.button4);
        timeView5 = (Button)findViewById(R.id.button5);
        timeView6 = (Button)findViewById(R.id.button6);
        timeView7 = (Button)findViewById(R.id.button7);

        aSwitch = (Switch)findViewById(R.id.switch1);
        aSwitch2 = (Switch)findViewById(R.id.switch2);
        aSwitch3 = (Switch)findViewById(R.id.switch3);
        aSwitch4 = (Switch)findViewById(R.id.switch4);
        aSwitch5 = (Switch)findViewById(R.id.switch5);
        aSwitch6 = (Switch)findViewById(R.id.switch6);
        aSwitch7 = (Switch)findViewById(R.id.switch7);

        menuButton = (Button)findViewById(R.id.menubutton1);
        menuButton2 = (Button)findViewById(R.id.menubutton2);
        menuButton3 = (Button)findViewById(R.id.menubutton3);
        menuButton4 = (Button)findViewById(R.id.menubutton4);
        menuButton5 = (Button)findViewById(R.id.menubutton5);
        menuButton6 = (Button)findViewById(R.id.menubutton6);
        menuButton7 = (Button)findViewById(R.id.menubutton7);



        //알람매니저 객체
        alarm = (AlarmManager)getSystemService(ALARM_SERVICE);

        //실제 알람 울리기 전에 의뢰(명시적) pending intent로 aintent 업데이트 해달라고 브로드캐스트한테 부탁함
        aIntent = new Intent(this,NotiReceiver.class);
        preIntent = PendingIntent.getBroadcast(this,50,aIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        //실제 알람 의뢰(암시적) pending intent로 bintent로 업데이트 해달라고 브로드캐스트한테 부탁함
        Intent bIntent = new Intent("com.example.perfectusefulmorningapp.ACTION_ALARM");
        alarmIntent = PendingIntent.getActivity(this,100,bIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        //preference객체(예전 알람 저장)
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        int hour = prefs.getInt("hour",-1);
        int minute = prefs.getInt("minute",-1);
        boolean enable = prefs.getBoolean("enable",false);


        //지금 시간으로 시간으로 셋팅
        if(hour > -1 && minute > -1){
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,hour);
            calendar.set(Calendar.MINUTE,minute);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            timeView.setText(sdf.format(calendar.getTime()));
        }

        //스위치도 온으로 셋팅
        if(enable){
            aSwitch.setChecked(true);
        }

        // 리사이클러뷰에 표시할 데이터 리스트 생성.
        ArrayList<String> list = new ArrayList<>();
        for (int i=0; i<100; i++) {
            list.add(String.format("TEXT %d", i)) ;
        }


        //버튼이랑 스위치 리스너
        menuButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainMenuActivity.class);
                startActivityForResult(intent,1001);
            }
        });

        menuButton2.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainMenuActivity.class);
                startActivityForResult(intent,1001);
            }
        });

        menuButton3.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainMenuActivity.class);
                startActivityForResult(intent,1001);
            }
        });

        menuButton4.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainMenuActivity.class);
                startActivityForResult(intent,1001);
            }
        });

        menuButton5.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainMenuActivity.class);
                startActivityForResult(intent,1001);
            }
        });

        menuButton6.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainMenuActivity.class);
                startActivityForResult(intent,1001);
            }
        });

        menuButton7.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainMenuActivity.class);
                startActivityForResult(intent,1001);
            }
        });

        timeView.setOnClickListener(this);
        timeView2.setOnClickListener(this);
        timeView3.setOnClickListener(this);
        timeView4.setOnClickListener(this);
        timeView5.setOnClickListener(this);
        timeView6.setOnClickListener(this);
        timeView7.setOnClickListener(this);

        aSwitch.setOnCheckedChangeListener(this);
        aSwitch2.setOnCheckedChangeListener(this);
        aSwitch3.setOnCheckedChangeListener(this);
        aSwitch4.setOnCheckedChangeListener(this);
        aSwitch5.setOnCheckedChangeListener(this);
        aSwitch6.setOnCheckedChangeListener(this);
        aSwitch7.setOnCheckedChangeListener(this);

    }

    @Override
    public void onClick(View v) {

        //현재시간 C
        final Calendar c=Calendar.getInstance();
        int currentHour=c.get(Calendar.HOUR_OF_DAY);
        int currentMinute=c.get(Calendar.MINUTE);

        if(v == timeView) {
            setTimeview = timeView;
            setSwitch = aSwitch;
        }else if(v == timeView2) {
            setTimeview = timeView2;
            setSwitch = aSwitch2;
        }else if(v == timeView3) {
            setTimeview = timeView3;
            setSwitch = aSwitch3;
        }else if(v == timeView4) {
            setTimeview = timeView4;
            setSwitch = aSwitch4;
        }else if(v == timeView5) {
            setTimeview = timeView5;
            setSwitch = aSwitch5;
        }else if(v == timeView6) {
            setTimeview = timeView6;
            setSwitch = aSwitch6;
        }else if(v == timeView7) {
            setTimeview = timeView7;
            setSwitch = aSwitch7;
        }

        //시간설정 타임피커로 현재시간 얻음
        final TimePickerDialog timeDialog=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            //타임피커로 시간 설정
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                isClick=true;   //유저가 새로운 시간 설정했으면

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                //새로운 시간 저장장
                SharedPreferences.Editor editor=prefs.edit();
                editor.putInt("hour", hourOfDay);
                editor.putInt("minute", minute);
                editor.putBoolean("enable", true);
                editor.commit();

                //새로운 시간 화면에 출력
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                setTimeview.setText(sdf.format(calendar.getTime()));
                setSwitch.setChecked(true);

                //알람매니저로 미리알람/진짜알람 등록 aintent한테 현재 시간 줌
                aIntent.putExtra("time", sdf.format(calendar.getTime()));
                alarm.set(AlarmManager.RTC, calendar.getTimeInMillis()-120000, preIntent);
                alarm.set(AlarmManager.RTC, calendar.getTimeInMillis(), alarmIntent);

                isClick=false;
            }
        }, currentHour, currentMinute, false);
        timeDialog.show();


    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //스위치 정보 저장
        SharedPreferences.Editor editor=prefs.edit();
        editor.putBoolean("enable", isChecked);
        editor.commit();

        //스위치 온되있을때
        if(isChecked){
            if(!isClick){
                //저장된 시간들
                int hour=prefs.getInt("hour",-1);
                int minute=prefs.getInt("minute",-1);
                if(hour > -1 && minute >-1){
                    Calendar calendar=Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);

                    //미리알람/전알람 등록
                    alarm.set(AlarmManager.RTC, calendar.getTimeInMillis() - 60000, preIntent);
                    alarm.set(AlarmManager.RTC, calendar.getTimeInMillis(), alarmIntent);
                }
            }
        }else {//스위치 오프되있을때 알람 전부 캔슬
            alarm.cancel(preIntent);
            alarm.cancel(alarmIntent);
            preIntent.cancel();
            alarmIntent.cancel();
            editor.putBoolean("enable", false);

        }
    }

}
