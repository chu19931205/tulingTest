package com.example.ck.tulingtest;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by CY-ChuKuang on 2018/3/21.
 */

public class MainActivity extends AppCompatActivity {
    private final static String APIKEY = "b61e888ab443479687649f5b0dee053e";
    private TextView mTextView;
    private EditText mEditText;
    private Button mButton;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String str = msg.obj.toString();
                    mTextView.setText(str);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String msg = mEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(msg)) {
                    mEditText.setText("");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String result = post(msg);
                            Message msg = Message.obtain();
                            msg.what = 0;
                            msg.obj = result;
                            mHandler.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextView.setFocusableInTouchMode(true);
                mTextView.requestFocus();
            }
        });
        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
            }
        });
    }

    private void initView() {
        mTextView = findViewById(R.id.textView);
        mEditText = findViewById(R.id.editText);
        mButton = findViewById(R.id.button);
    }

    private String post(String string) {
        String result = "";
        try {
            String info = URLEncoder.encode(string, "utf-8");
            String getURL = "http://www.tuling123.com/openapi/api?key=" + APIKEY + "&info=" + info;
            URL getUrl = new URL(getURL);
            HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
            connection.connect();

            // 取得输入流，并使用Reader读取
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            // 断开连接
            connection.disconnect();
            JSONObject json = new JSONObject(sb.toString());
            switch (json.getInt("code")) {
                case 100000:
                    result = json.getString("text");
                    break;
                case 200000:
                    result = json.getString("text") + "\n" + json.getString("url");
                    break;
//                case 302000:
//
//                    break;
//                case 308000:
//
//                    break;
//                case 313000:
//
//                    break;
//                case 314000:
//
//                    break;
                default:
                    result = sb.toString();
                    break;
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

}
