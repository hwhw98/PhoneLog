package com.heewon.phonelog;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.CallLog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editText;

    // 찾은 번호들 담을 리스트
    ArrayList<String> numList = new ArrayList<String>();
    String partNum;

    String sureAns;

    Integer[] startDate;
    Integer[] endDate;
    DatePickerDialog datePickerDialog;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        boolean hasLaunchedBefore = prefs.getBoolean("hasLaunchedBefore", false);

        if (hasLaunchedBefore == false){
            Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
            startActivityForResult(intent, 2);
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("hasLaunchedBefore", true);
        editor.apply();

        // 권한ID를 가져옵니다
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALL_LOG);

        int permission2 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CALL_LOG);

        // 권한이 열려있는지 확인
        if (permission == PackageManager.PERMISSION_DENIED || permission2 == PackageManager.PERMISSION_DENIED) {
            // 마쉬멜로우 이상버전부터 권한을 물어본다
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 권한 체크(READ_PHONE_STATE의 requestCode를 1000으로 세팅
                requestPermissions(
                        new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG},
                        1000);
            }
            //return;
        }

        editText = (EditText) findViewById(R.id.delete_text);

        ListView listView = (ListView) findViewById(R.id.listView);

        // Create adapter and set it to listView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, numList);
        listView.setAdapter(adapter);

        Button sendBtn = (Button) findViewById(R.id.send_num);
        sendBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                partNum = String.valueOf(editText.getText());
                findNums(partNum);
                Log.d("textis:",partNum);
                Log.d("numList", Integer.toString(numList.size()));

                //adapter.addAll(numList);
                adapter.notifyDataSetChanged(); // Notify adapter about data changes
            }
        });

        Button deleteButton = (Button)findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                int count = adapter.getCount() ;
                // 현재 선택된 아이템들의 position 획득.
                SparseBooleanArray checkedItems = listView.getCheckedItemPositions();

                for (int i = count-1; i >= 0; i--) {
                    if (checkedItems.get(i)) {
                        numList.remove(i) ;
                    }
                }
                listView.clearChoices();
                // listview 갱신.
                adapter.notifyDataSetChanged();
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);    //hide keyboard
                    return true;
                }
                return false;
            }
        });

        ImageButton helpBtn = (ImageButton) findViewById(R.id.helpBtn);
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
                startActivityForResult(intent, 2);
            }
        });
    }

    //button 클릭 시 numList 해당 callLog 삭제 후 numList 초기화
    public void deleteNum(View view){
        // fragment_sure_delete 띄우고
        Intent intent = new Intent(getApplicationContext(), SureActivity.class);
        startActivityForResult(intent, 1);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode != RESULT_OK) {
                return;
            }
            sureAns = data.getStringExtra("result");
            Log.d("resultis", sureAns);

            Toast toast;
            if(sureAns==null){
                return;
            } else if (sureAns.equals("yes")){
                String queryString = CallLog.Calls.NUMBER+"=?";
                String[] selection = numList.toArray(new String[numList.size()]);
                getContentResolver().delete(CallLog.Calls.CONTENT_URI, queryString, selection);

                toast = makeToast("해당 번호들의 통화기록들이 모두 삭제됐습니다!");
                toast.show();

            } else if (sureAns.equals("no")) {
                toast = makeToast("다시 선택하여 주세요.");
                toast.show();
            }
        }

        if(requestCode == 2){
            return;
        }

        if(requestCode == 3){
            if (resultCode != RESULT_OK) {
                return;
            }
            String agree = data.getStringExtra("agree");
            if (agree.equals("yes")){
                return;
            } else if(agree.equals("no")){
                Log.d("agreeis", "no");
            }
        }
    }

    public Toast makeToast(String text){
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);

        ViewGroup group = (ViewGroup)toast.getView();
        TextView msgTextView = (TextView)group.getChildAt(0);
        msgTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

        toast.setGravity(Gravity.CENTER,0,0);
        return toast;
    }

    public void findNums(String partNum){
        numList.clear();
        if(partNum==null || partNum.isEmpty()){
            return;
        }
        HashSet<String> hashSet = new HashSet<>();

        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null,
                null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);

        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            // partNum 숫자들 포함하는 번호면 numList에 담는다
            if (stringEquality(partNum, phNumber)){
                if (hashSet.size()>=10) break;
                hashSet.add(phNumber);
            }
        }

        numList.addAll(hashSet);
    }

    // 같은 index에 같은 value가지면('_'value 는 무시) return true
    public static boolean stringEquality(String part, String phNum) {

        if (part.contains("_")){
            for (int i = 0; i < part.length(); i++) {
                if(part.charAt(i) == '_') {
                    continue;
                } else if (part.charAt(i) != phNum.charAt(i)) {
                    return false;
                }
            }
            return true;
        } else {
            return phNum.contains(part);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        // READ_PHONE_STATE의 권한 체크 결과를 불러온다
        super.onRequestPermissionsResult(requestCode, permissions, grandResults);
        if (requestCode == 1000) {
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            // 권한 체크에 동의를 하지 않으면 안드로이드 종료
            if (check_result == true) {

            } else {
                finish();
            }
        }
    }
}