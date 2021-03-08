package com.licp.oaid_helper;

import android.os.Bundle;
import android.widget.TextView;

import com.licp.library.Oaid;
import com.licp.library.OaidHelper;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OaidHelper.init(this);
        new OaidHelper(new Oaid.AppIdsUpdater() {
            @Override
            public void onIdsAvalid(boolean isSupport, String oaid, String vaid, String aaid) {
                if (isSupport) {
                    TextView tv = findViewById(R.id.tv);
                    tv.setText(String.format(Locale.CHINA, "oaid:%s", oaid));
                }
            }
        }, getClassLoader()).loadOaid(this);
    }
}
