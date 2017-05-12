package com.example.smaug.lockview;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    LockView mLockView;
    SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLockView = (LockView) findViewById(R.id.lock_view);
        mLockView.setOnLockChangeListeners(new LockView.OnLockChangeListener() {
            @Override
            public void setOnLockSuccessed(StringBuilder passward) {
                SharedPreferences pref = getSharedPreferences("keys", MODE_PRIVATE);
                String key = pref.getString("passward", null);
                if (key == null) {
                    Toast.makeText(MainActivity.this, "设置初始密码", Toast.LENGTH_SHORT).show();
                    mEditor = getSharedPreferences("keys", MODE_PRIVATE).edit();
                    mEditor.putString("passward", passward.toString());
                    mEditor.apply();
                } else if (key.equals(passward.toString())) {
                    Toast.makeText(MainActivity.this, "密码正确", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void setOnLockError() {
                Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
