package com.javaex.mysite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
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

public class ReadActivity extends AppCompatActivity {

    //field

    private TextView txtNo;
    private TextView txtName;
    private TextView txtRegDate;
    private TextView txtContent;
    private Button btnGoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        //위젯 객체화
        txtNo = (TextView) findViewById(R.id.txtNo);
        txtName = (TextView) findViewById(R.id.txtName);
        txtRegDate = (TextView) findViewById(R.id.txtRegDate);
        txtContent = (TextView) findViewById(R.id.txtContent);
        btnGoList = (Button) findViewById(R.id.btnGoList);

        //이번 액티비티에서 보내준 값을 꺼내준다.

        Intent intent = getIntent();
        int no = intent.getExtras().getInt("no");
        String name = intent.getExtras().getString("name");

        Log.d("javaStudy", "intent로 받은 no-->" + no);
        Log.d("javaStudy", "intent로 받은 name-->" + name);


        //가져온 정보를 출력한다.

        // AsyncTask로 내부로 정보 전달
        ReadAsyncTask readAsyncTask = new ReadAsyncTask();
        readAsyncTask.execute(no);

        /////////////////////////////////////////
        /////////////버튼클릭/////////////////////
        ///////////////////////////////////////




        btnGoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });



    }



    //Inner Class

    public class ReadAsyncTask extends AsyncTask<Integer, Integer, GuestbookVo> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected GuestbookVo doInBackground(Integer... noArray) {

            //AsyncTask에서 no를 받아온다다

           Log.d("javaStudy", "doInBackground()");
            Log.d("javaStudy", "doInBackground().no-->" + noArray[0]);


            //Gson 메모리에 올리기(요청 응답 모두 사용)

            Gson gson = new Gson();

            //보내는 guestbookVo

            GuestbookVo requestVo = null;

            //받는 guestbookVo

            GuestbookVo responseVo = null;

            /////////////////////////////////////////////////////////
            //////////////////////요청 관리 업무//////////////////////
            //////////////////////////////////////////////////////



            //요청준비

            int no = noArray[0];

            requestVo = new GuestbookVo();
            requestVo.setNo(no);

            //Vo ---> json 변경

            String requestJson = gson.toJson(requestVo);

            Log.d("javaStudy", "requestJson-->" + requestJson);

            //요청 + requestBody에 requestJson 넣어서 요청한다.
            //성공여부 판단

            //성공이면 데이터 읽기
            //json --> java 객체로 변경
            //객체의 값을 화면에 출력력


            //서버(Sprng)로부터 29번 정보를 요청한다.

            try {
                URL url = new URL("http://192.168.0.108:8088/mysite5/api/guestbook/read");  //url 생성

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();  //url 연결
                conn.setConnectTimeout(10000); // 10초 동안 기다린 후 응답이 없으면 종료
                conn.setRequestMethod("POST"); // 요청방식 POST
                conn.setRequestProperty("Content-Type", "application/json"); //요청시 데이터 형식 json
                conn.setRequestProperty("Accept", "application/json"); //응답시 데이터 형식 json
                conn.setDoOutput(true); //OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
                conn.setDoInput(true); //InputStream으로 서버로 부터 응답을 받겠다는 옵션.

                //body에 데이터를 보낸다. json
                //guestbookVo에 no정보를 담는다.
                //gusetbookVo ---> json으로 만든다.
                //데이터를 requestBody로 보내서 요청한다.

                //보내는 스트림 OutputStream (json --> requestBody)

                OutputStream os = conn.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                BufferedWriter bw = new BufferedWriter(osw);


                //쓰기 보내기기
                bw.write(requestJson);
                bw.flush();

                int resCode = conn.getResponseCode(); // 응답코드 200이 정상

                Log.d("javaStudy", "resCode-->" + resCode);

                if(resCode == 200){ //정상이면

                    //받는 스트림 InputStream(reseponseBody(json) --> 자바객체)
                    InputStream is = conn.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                    BufferedReader br = new BufferedReader(isr);

                    //Stream 을 통해 통신한다
                    //데이타 형식은 json으로 한다.
                        String responseJson = "";

                        while(true) {

                            String line = br.readLine();

                            if(line == null) {

                                break;

                            }

                            responseJson = responseJson + line;

                        }

                    Log.d("javaStudy", "responseJson-->" + responseJson);

                        responseVo = gson.fromJson(responseJson, GuestbookVo.class);

                    Log.d("javaStudy", "responseVo-->" + responseVo);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return responseVo;
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(GuestbookVo guestbookVo) {

            super.onPostExecute(guestbookVo);

            Log.d("javaStudy", "========onPostExecute=====");
            Log.d("javaStudy", "" + guestbookVo);


            //화면출력

            txtNo.setText(""+guestbookVo.getNo());
            txtName.setText(guestbookVo.getName());
            txtRegDate.setText(guestbookVo.getRegDate());
            txtContent.setText(guestbookVo.getContent());

        }
    }



}