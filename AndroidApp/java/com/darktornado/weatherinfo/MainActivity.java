package com.darktornado.weatherinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
        final EditText txt = new EditText(this);
        txt.setHint("지역을 입력하세요...");
        layout.addView(txt);
        Button btn = new Button(this);
        btn.setText("날씨 정보 조회");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = txt.getText().toString();
                String zoneId = getZoneId(input);
                if (zoneId == null) {
                    toast("해당 지역을 찾을 수 없습니다.");
                } else {
                    Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                    intent.putExtra("zoneId", zoneId);
                    startActivity(intent);
                }
            }
        });
        layout.addView(btn);
        setContentView(layout);
    }

    private String readZoneList() {
        try {
            InputStream stream = getAssets().open("zone_id.csv");
            InputStreamReader isr = new InputStreamReader(stream);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder str = new StringBuilder(br.readLine());
            String line = "";
            while ((line = br.readLine()) != null) {
                str.append("\n").append(line);
            }
            isr.close();
            br.close();
            return str.toString();
        } catch (IOException e) {
            toast("있어야 할 파일이 없습니다?");
        }
        return "";
    }

    private String getZoneId(String name) {
        String[] data = readZoneList().split("\n");
        for (String s : data) {  //일치 여부 확인
            String[] datum = s.split(",");
            if (datum[0].equals(name)) return datum[1];
        }
        for (String s : data) {  //포함 여부 확인
            String[] datum = s.split(",");
            if (datum[0].contains(name)) return datum[1];
        }
        return null;
    }

    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}