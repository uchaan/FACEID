package com.example.loginadmin;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ThirdActivity extends AppCompatActivity {
    private String data;
    private String raw_lecture;
    private String name;
    private String[] lectures;
    public HashMap<String, String> LectureMap = new HashMap<>();
    public HashMap<String, String> TimeMap = new HashMap<>();
    private Intent intent2;
    String selectedLecture;
//    private ArrayList<Lecture> LectureList = new ArrayList<>();
    RecyclerView recyclerView;

    String SERVER = "http://192.249.19.252:1780/students/lecture/";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_third);
        getSupportActionBar().setIcon(R.drawable.tt2);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initMap();
        Intent intent = getIntent();
        data = intent.getExtras().getString("json");
        name = intent.getExtras().getString("name");

        TextView  textView = findViewById(R.id.title);
        textView.setText(name+" 교수님 강의 목록");

        jsonParsing();

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(new MyAdapter(lectures));

    }

    private void jsonParsing() {
        try {
            // json 을 JSONArray 로 형변환
            JSONArray jsonArray = new JSONArray(data);
            raw_lecture = jsonArray.getJSONObject(0).getString("lecture");
            lectures = raw_lecture.split(",");
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("진행하는 수업이 없습니다");
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {

        private String[] myLectureList = null;

        MyAdapter(String[] lectures)
        {
            myLectureList = lectures;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //전개자(Inflater)를 통해 얻은 참조 객체를 통해 뷰홀더 객체 생성
            View view = inflater.inflate(R.layout.recyclerview_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position)
        {
            //ViewHolder가 관리하는 View에 position에 해당하는 데이터 바인딩
            String temp_lecture = myLectureList[position];
            viewHolder.TopText.setText(temp_lecture + "\n" + LectureMap.get(temp_lecture));
        }

        @Override
        public int getItemCount()
        {
            //Adapter가 관리하는 전체 데이터 개수 반환
            return myLectureList.length;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView TopText;

        ViewHolder(View itemView)
        {
            super(itemView);
            TopText = itemView.findViewById(R.id.topText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        selectedLecture = lectures[pos];
                        SERVER = "http://192.249.19.252:1780/students/lecture/";
                        SERVER = SERVER + selectedLecture;

                        HttpGetRequest request = new HttpGetRequest();
                        request.execute();
                    }
                }
            });
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
                intent2 = new Intent(getApplicationContext(), FourthActivity.class);
                intent2.putExtra("lecture", selectedLecture);
                intent2.putExtra("json", result);
                Log.d("TTTT", "----------------------------" + SERVER);
                startActivity(intent2);
            }
        }
    }

    public void initMap(){
        LectureMap.put("CS496", "Mad Camp ");
        TimeMap.put("CS496", "Everyday | 20:30 ~ 22:00 ");
        LectureMap.put("CS320", "Programming Language");
        TimeMap.put("CS320", " Mon/Wed | 14:30 ~ 16:00 ");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("key", String.valueOf(intent2));
    }

}


