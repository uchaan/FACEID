package com.example.loginadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
//import org.jsoup.Jsoup;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class SecondActivity extends AppCompatActivity {

    private String name;
    private String htmlPageUrl = "http://www.yonhapnews.co.kr/";
    private TextView news;
    private String htmlContentInStringFormat = "◆ ";
    int cnt = 0;

    String SERVER = "http://192.249.19.252:1780/profs/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_second);
        getSupportActionBar().setIcon(R.drawable.tt2);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        news = (TextView)findViewById(R.id.news);
        news.setMovementMethod(new ScrollingMovementMethod());

        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();
        cnt++;

        Button portal = findViewById(R.id.button6);
        portal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://portalsso.kaist.ac.kr/login.ps"));
                startActivity(intent);
            }
        });

        Button klms = findViewById(R.id.button7);
        klms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://klms.kaist.ac.kr/login.php"));
                startActivity(intent);
            }
        });

        init();

        // MainActivity 로부터 사용자 이름 가져옴.
        Intent intent = getIntent();
        name = intent.getExtras().getString("name");

        // 서버 url 에 사용자 이름 추가
        SERVER = SERVER + name;

        // 버튼 클릭시 웹서버로 부터 JSONArray 가져옴.
        Button check = findViewById(R.id.button5);
        check.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                HttpGetRequest request = new HttpGetRequest();
                request.execute();
            }
        });
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){super.onPreExecute();}

        @Override
        protected Void doInBackground(Void... params){
            try{
                Document doc = Jsoup.connect(htmlPageUrl).get();
                Elements titles = doc.select("div.news-con h1.tit-news");
                for(Element e: titles){
                    htmlContentInStringFormat += e.text().trim() + "\n◆ ";
                }

                titles = doc.select("div.news-con h2.tit-news");
                for(Element e:titles){
                    htmlContentInStringFormat += e.text().trim() + "\n◆ ";
                }



            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected  void onPostExecute(Void result){
            news.setText(htmlContentInStringFormat);
        }
    }


    // 웹서버에서 사용자 수강과목 JSONArray 데이터 가져와주는 클래스
    public class HttpGetRequest extends AsyncTask<Void, Void, String> {

        static final String REQUEST_METHOD = "GET";
        static final int READ_TIMEOUT = 15000;
        static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected String doInBackground(Void... params){
            String op;
            String inputLine;

            try {
                // connect to the server
                URL myUrl = new URL(SERVER);
                HttpURLConnection connection =(HttpURLConnection) myUrl.openConnection();
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.connect();

                // get the string from the input stream
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                op = stringBuilder.toString();

            } catch(IOException e) {
                e.printStackTrace();
                op = "error";
            }

            return op;
        }

        // 가져온 데이터 tvData 텍스트 뷰에 뿌림
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if (result!=null) {
                Intent intent = new Intent(getApplicationContext(), ThirdActivity.class);
                intent.putExtra("json", result);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        }
    }

    public void init(){
        ArrayList<String> aze_gag = new ArrayList<String>();
        aze_gag.add("서울이 추운 걸 다섯 글자로 하면?\n서울시립대~");aze_gag.add("미국에서 비가 내리면?\nUSB~");aze_gag.add("우리나라까지 석유가 도착하는데 소요되는 시간은?\n오일~");
        aze_gag.add("오리를 생으로 먹으면?\n회오리~");aze_gag.add("얼음이 죽으면?\n다이빙~");aze_gag.add("가장 비싼 새는?\n백조~");
        TextView aze = findViewById(R.id.aze);
        Random random = new Random();
        aze.setText(aze_gag.get(random.nextInt(5)));
    }



}
