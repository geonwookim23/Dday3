package com.example.escbasicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private ImageButton addContact;
    private ImageButton contact;
    private TextView phoneNum;
    private TextView[] dials = new TextView[10];
    private TextView star;
    private TextView sharp;
    private ImageButton message;
    private ImageButton call;
    private ImageButton backspace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        setUpUI();
        phoneNum.setText("");
        if(phoneNum.getText().length() == 0) {
            message.setVisibility(View.GONE);
            backspace.setVisibility(View.GONE);
        }
    }

    private void checkPermission(){
        int resultCall = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int resultSms = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);

        if(resultCall == PackageManager.PERMISSION_DENIED || resultSms == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS}, 1001);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1001){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"권한 허용 됨", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this,"권한 허용이 필요합니다. 설정에서 허용해주세요", Toast.LENGTH_SHORT);
                Log.d("PermissionDenied", "권한이 거부돼 앱을 종료합니다.");
            }
            finish();
        }
    }

    private void  setUpUI()  {
        addContact = findViewById(R.id.main_ibtn_add);
        contact = findViewById(R.id.main_ibtn_contact);
        phoneNum = findViewById(R.id.main_tv_phone);

        for(int i=0; i<dials.length; i++) {
            dials[i] = findViewById(getResourceID("main_tv_" + i,"id", this ));
        }

        star = findViewById(R.id.main_tv_star);
        sharp = findViewById(R.id.main_tv_sharp);
        message = findViewById(R.id.main_ibtn_message);
        call = findViewById(R.id.main_ibtn_call);
        backspace = findViewById(R.id.main_ibtn_back);

        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(MainActivity.this, AddEditActivity.class);
                startActivity(addIntent);
                Toast.makeText(MainActivity.this, "test", Toast.LENGTH_SHORT).show();

            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactIntent = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(contactIntent);
            }
        });


        setOnClickDial(star,"*");
        setOnClickDial(sharp,"#");

        for(int i = 0; i < 10; i++){
            setOnClickDial(dials[i], String.valueOf(i));
        }

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent messageIntent = new Intent(MainActivity.this, MessageActivity.class);
                messageIntent.putExtra("phone_num", phoneNum.getText().toString());
                startActivity(messageIntent);
            }
        });
        call.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //checkPermission();
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel: " + phoneNum.getText()));
                startActivity(callIntent);
            }
        });


        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneNum.getText().length() >= 0){
                    //String formatPhoneNum = PhoneNumberUtils.formatNumber(phoneNum.getText().subSequence(0,phoneNum.getText().length()-1).toString(), Locale.getDefault().getCountry());
                    //phoneNum.setText(formatPhoneNum);

                    String change = changeToDial(phoneNum.getText().subSequence(0,phoneNum.getText().length()-1).toString());
                    phoneNum.setText(change);

                    if(phoneNum.getText().length() == 0) {
                        message.setVisibility(View.GONE);
                        backspace.setVisibility(View.GONE);
                    }
                }

            }
        });
        backspace.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                phoneNum.setText("");


                message.setVisibility(View.GONE);
                backspace.setVisibility(View.GONE);

                return true;
            }
        });

    }

    private void setOnClickDial(View view, final String input){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String formatPhoneNum = PhoneNumberUtils.formatNumber(phoneNum.getText() + input, Locale.getDefault().getCountry());
//                phoneNum.setText(formatPhoneNum); //번호 입력

                String change = changeToDial(phoneNum.getText() + input);
                phoneNum.setText(change);

                message.setVisibility(View.VISIBLE);
                backspace.setVisibility(View.VISIBLE);

            }
        });
    }

    private int getResourceID(final String resName, final String resType, final Context ctx) {
        final int ResourceID = ctx.getResources().getIdentifier(resName, resType, ctx.getApplicationInfo().packageName);
        if(ResourceID == 0){
            //throw new IllegalAccessException("No resource string found with name " + resName);
        }
        else{
            return ResourceID;
        }
        return ResourceID;//
    }

    private String changeToDial(String phoneNum_){
        //전화번호 기준 01033218119
        //4글자 이상일 때 3번째 숫자 다음에
        //8글자이상이면 3번째 다음이랑 7번째 다음에 둘다 하이픈을
        //12글자 이상이면 전부 제거
        //특수문자 있으면 하이픈 전부 제거
        phoneNum_ = phoneNum_.replaceAll("-","");
        int length = phoneNum_.length();

        if(phoneNum_.charAt(length-1) == '*' || phoneNum_.charAt(length-1) == '#'){
            return phoneNum_;
        }
        else if(length>=(12)){
            return phoneNum_;
        }
        else if(length >= 8){
            phoneNum_ = phoneNum_.substring(0,3) + "-" + phoneNum_.substring(3,7) + "-" + phoneNum_.substring(7);
            return phoneNum_;
        }
        else if(length >= 4){
            phoneNum_ = phoneNum_.substring(0,3) + "-" + phoneNum_.substring((3));
            return phoneNum_;
        }

        return phoneNum_;
    }
}