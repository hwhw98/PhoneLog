package com.heewon.phonelog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {
    final String help = "\"02_\" 입력 후 번호 목록을 가져오면 02로 시작하는 번호들을 가져옵니다.\n" +
            "\n"+
            "\"02\" 입력 후 번호 목록을 가져오면 02가 포함된 번호들을 가져옵니다.\n"+
            "\n"+
            "키보드에 오른쪽 아래 다음을 누르면 키보드는 들어갑니다.\n"+
            "\n"+
            "\n"+
            "목록에 있는 번호들은 통화기록에서 삭제가 될 번호들의 목록입니다.\n"+
            "\n"+
            "\n"+
            "번호 목록 오른쪽 네모박스를 클릭하면 해당 번호는 선택됩니다.\n"+
            "\n"+
            "선택 번호들 목록에서 제거를 클릭하면 목록에 선택된 번호들이 삭제할 번호 목록에서 제외됩니다.\n"+
            "\n"+
            "\n"+
            "아래 번호들 통화 기록에서 모두 삭제 클릭하면 핸드폰 자체의 기본 통화 어플리케이션의 통화기록에서 삭제할 번호 목록에 들어있는 모든 번호들이 삭제됩니다.\n"+
            "\n"+
            "\n"+
            "오른쪽 위에 물음표 버튼은 해당 도움말을 불러옵니다.";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        TextView textView = (TextView) findViewById(R.id.textView);

        textView.setText(help);

        ImageButton backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}