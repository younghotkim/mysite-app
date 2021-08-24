package com.javaex.mysite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.javaex.vo.GuestbookVo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private ListView lvGuestbookList;
    private Button btnGoWriteForm;

    //액티비티가 처음 실행될 때
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //ListView를 객체화 한다
        lvGuestbookList = (ListView) findViewById(R.id.lvGuestbookList);

        //데이터를 가져온다
        //List<GuestbookVo> guestbookVoList = getListFromServer();

        //데이터 가져오기 (화면이 그리기)
        //ListAsyncTask listAsyncTask = new ListAsyncTask();
        //listAsyncTask.execute();


        //글쓰기관련 이벤트

        ///////////////////////////

        btnGoWriteForm = (Button)findViewById(R.id.btnGoWriteForm);

        btnGoWriteForm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
            Log.d("javaStudy","방명록 글쓰기 버튼");

                Intent intent = new Intent(ListActivity.this, MainActivity.class);
                startActivity(intent);



            }
        });


    }
    //////////////////////////////////////////
    //액티비티가 숨겨졌다가 다시 보일때(Resume)////


    @Override
    protected void onResume() {
        super.onResume();
        /////////////////////////////////////////
        //////리스트 Resume관련 작업///////////////
        ////////////////////////////////////////

        ListAsyncTask listAsyncTask = new ListAsyncTask();
        listAsyncTask.execute();

        lvGuestbookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Log.d("javaStudy", "index=" + i);

                TextView txtContent = (TextView)view.findViewById(R.id.txtContent);

                Log.d("javaStudy", "Content=" + txtContent.getText().toString());

                //화면에 출력되지 않은 데이터
                GuestbookVo guestbookVo = (GuestbookVo) adapterView.getItemAtPosition(i);

                Log.d("javaStudy", "vo=" + guestbookVo.toString());

                /////////////////List > Read/////////////////

                //클릭한 아이템의 pk값
                int no = guestbookVo.getNo();

                Intent intent = new Intent(ListActivity.this, ReadActivity.class);

                //글읽기 액티비티로 이동(글번호 전달해야함)

                intent.putExtra("no",no);
                intent.putExtra("name",guestbookVo.getName());

                startActivity(intent);

            }

        });

    }

    public class ListAsyncTask extends AsyncTask<Void, Integer, List<GuestbookVo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<GuestbookVo> doInBackground(Void... voids) {

            List<GuestbookVo> guestbookList = null;

            try {


                URL url = new URL("http://192.168.0.108:8088/mysite5/api/guestbook/list");  //url 생성

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();  //url 연결
                conn.setConnectTimeout(10000); // 10초 동안 기다린 후 응답이 없으면 종료
                conn.setRequestMethod("POST"); // 요청방식 POST
                conn.setRequestProperty("Content-Type", "application/json"); //요청시 데이터 형식 json
                conn.setRequestProperty("Accept", "application/json"); //응답시 데이터 형식 json
                conn.setDoOutput(true); //OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
                conn.setDoInput(true); //InputStream으로 서버로 부터 응답을 받겠다는 옵션.

                int resCode = conn.getResponseCode(); // 응답코드 200이 정상

                if (resCode == 200) { //정상이면

                    Log.d("javaStudy", "" + resCode);



                    //Stream 을 통해 통신한다
                    //데이타 형식은 json으로 한다.

                    InputStream is = conn.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is,"UTF-8");
                    BufferedReader br = new BufferedReader(isr);

                    String jsonData = "";

                    while (true) {

                        String line = br.readLine();

                        if(line == null) {

                            break;

                        }

                        jsonData = jsonData + line;

                    }

                    Log.d("javaStudy","" + jsonData);

                    Gson gson = new Gson();
                    guestbookList = gson.fromJson(jsonData, new TypeToken<List<GuestbookVo>>(){}.getType());

                    //Log.d("javaStudy","size---->" + guestbookList.size());
                    //Log.d("javaStudy","size---->" + guestbookList.get(0).getName());


                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return guestbookList;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List<GuestbookVo> guestbookList) {

            Log.d("javaStudy","onPostExecute() size---->" + guestbookList.size());
            Log.d("javaStudy","onPostExecute() size---->" + guestbookList.get(0).getName());

            //어댑터생성
            GuestbookListAdapter guestbookListAdapter =
                    new GuestbookListAdapter(getApplicationContext(),R.id.lvGuestbookList,guestbookList);

            //리스트뷰에 어댑터를 세팅한다다
            lvGuestbookList.setAdapter(guestbookListAdapter);



            super.onPostExecute(guestbookList);



        }
    };












    /*
    public List<GuestbookVo> getListFromServer() {

        List<GuestbookVo> guestbookVoList = new ArrayList<GuestbookVo>();

        for(int i=0; i<=50; i++) {

            GuestbookVo guestbookVo = new GuestbookVo();

            guestbookVo.setNo(i);
            guestbookVo.setName("호날두");
            guestbookVo.setRegDate("2021-08-19" + i);
            guestbookVo.setContent(i+ "번째 본문입니다.");

            guestbookVoList.add(guestbookVo);
        }

        return guestbookVoList;

    }

     */


}