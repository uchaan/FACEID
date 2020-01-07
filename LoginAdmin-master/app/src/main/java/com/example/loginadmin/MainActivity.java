package com.example.loginadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    String in_student_id;
    String in_password, in_name;
    String right_student_id;
    String right_name;
    String right_password;
    String name;
    String SERVER ;
    String loginSERVER;

    EditText id, pw, Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setIcon(R.drawable.tt2);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);

        SERVER = "http://192.249.19.252:1780/logins/";

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //클릭하면 사용자 입력 정보 in 으로 받아옴

                id = findViewById(R.id.editText2);
                pw = findViewById(R.id.editText3);

                in_student_id = id.getText().toString();
                in_password = pw.getText().toString();
                loginSERVER = SERVER + in_student_id;
                //서버에 id 요청
                HttpGetRequest request = new HttpGetRequest();
                request.execute();

            }
        });

        Button button2 = findViewById(R.id.button2);



    }

    public void OnClickHandler(View view) {

        View dialogView = getLayoutInflater().inflate(R.layout.register_layout, null);

        final EditText NewName = (EditText) dialogView.findViewById(R.id.tv2);
        final EditText NewID = (EditText) dialogView.findViewById(R.id.tv4);
        final EditText NewPW = (EditText) dialogView.findViewById(R.id.tv6);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);



        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                in_name = NewName.getText().toString();
                in_student_id = NewID.getText().toString();
                in_password = NewPW.getText().toString();

                new HttpPostRequest().execute(SERVER);

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                Toast.makeText(getApplicationContext(), "Cancel Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


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
                URL myUrl = new URL(loginSERVER);
                HttpURLConnection connection =(HttpURLConnection) myUrl.openConnection();
                Log.d("t", "------------------------------" + SERVER);
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

        protected void onPostExecute(String result){
            super.onPostExecute(result);
//            TextView textView = (TextView)findViewById(R.id.textView);
//            textView.setText(result);
            try {
                //서버에 해당 id 찾아서 result 받아오고 파싱
                JSONArray jsonArray = new JSONArray(result);
                right_student_id = jsonArray.getJSONObject(0).getString("student_id");
                right_password = jsonArray.getJSONObject(0).getString("password");

                if(right_password.equals(in_password) && in_student_id.equals(right_student_id)){
                    Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                    name = jsonArray.getJSONObject(0).getString("name");
                    Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                    intent.putExtra("name", name);
                    startActivity(intent);

                } else Toast.makeText(getApplicationContext(), "틀림", Toast.LENGTH_SHORT).show();

            } catch (JSONException e){
                //핸들해줘요
            }
        }

    }

    public class HttpPostRequest extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("student_id", in_student_id);
                jsonObject.accumulate("password", in_password);
                jsonObject.accumulate("name", in_name);

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    URL url = new URL(SERVER);
                    //연결을 함
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송


                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();

                    //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();//버퍼를 받아줌

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();//버퍼를 닫아줌
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), "등록 완료 ", Toast.LENGTH_SHORT).show();
        }
    }

}
