package com.javaex.mysite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.javaex.vo.GuestbookVo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    //필드
    private Button btnWrite;
    private EditText edtName;
    private EditText edtPassword;
    private EditText edtContent;
    //생성자
    //게터세터
    //일반

    //액티비티가 시작될때
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnWrite = (Button)findViewById(R.id.btnWrite);
        edtName = (EditText)findViewById(R.id.edtName);
        edtPassword = (EditText)findViewById(R.id.edtPassword);
        edtContent = (EditText)findViewById(R.id.edtContent);


        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //getSupportActionBar().setTitle("방명록 쓰기"); ==xml에서 속성을 찾아서 처리

        //저장버튼을 클릭할때
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("javaStudy", "저장버튼 클릭");

                //방명록데이터를 vo로 만든다.
                String name = edtName.getText().toString();
                String password = edtPassword.getText().toString();
                String content = edtContent.getText().toString();

                GuestbookVo guestbookVo = new GuestbookVo(name, password, content);
                Log.d("javaStudy", "vo= " + guestbookVo.toString());


                WriteAsyncTask writeAsyncTask = new WriteAsyncTask();
                writeAsyncTask.execute(guestbookVo);


                //서버에 전송하고
                Log.d("javaStudy", "서버전송.............Ok!!!");



            }
        });


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("javaStudy", "Home 버튼 클릭");
        Log.d("javaStudy", "item.getItemId()-->"+item.getItemId());
        Log.d("javaStudy", "android.R.id.home()-->"+android.R.id.home);

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);


        }


    }


    //이너클래스
    public class WriteAsyncTask extends AsyncTask<GuestbookVo, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(GuestbookVo... guestbookVos) {

            Log.d("javaStudy", "doInBackground()");
            Log.d("javaStudy", "Vo = " + guestbookVos[0].toString());

            //vo-->json
            Gson gson = new Gson();
            String json = gson.toJson(guestbookVos[0]);
            Log.d("javaStudy", "json -->" + json);

            //데이터 전송


            try {
                //접속정보
                URL url = new URL("http://192.168.0.108:8088/mysite5/api/guestbook/write2");  //url 생성 -> 없는주소 치면 resCode는 에러코드랑 숫자 똑같이 뜸.

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();  //url 연결
                conn.setConnectTimeout(10000); // 10초 동안 기다린 후 응답이 없으면 종료
                conn.setRequestMethod("POST"); // 요청방식 POST
                conn.setRequestProperty("Content-Type", "application/json"); //요청시 데이터 형식 json
                conn.setRequestProperty("Accept", "application/json"); //응답시 데이터 형식 json
                conn.setDoOutput(true); //OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
                conn.setDoInput(true); //InputStream으로 서버로 부터 응답을 받겠다는 옵션.

                //outputStream(json ---> body)
                OutputStream os = conn.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os);
                BufferedWriter bw = new BufferedWriter(osw);

                bw.write(json);
                bw.flush();

                int resCode = conn.getResponseCode(); // 응답코드 200이 정상
                Log.d("javaStudy", "resCode = " + resCode);

                if(resCode == HttpURLConnection.HTTP_OK) {  //http_ok 는 숫자로 200이고 그거와같다면.


                    /*
                    리스트 액티비티로 전환

                    Intent intent = new Intent(MainActivity.this, ListActivity.class);
                    startActivity(intent);
                    */

                    //자신 액티비티를 종료시킨다.
                    finish();

                }


            } catch (IOException e) {
                e.printStackTrace();
            }


            //응답을 받는다.(데이터를 받는다(문자열)) -> guestbooklist 받아야함 -> json형태로 온다 -> java객체로 변환(List<GuestbookVo> guestbookVo)
            return null;






        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }



    }



}