package com.zzti.fsuper.okhttp_demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends ActionBarActivity {

    private Button button;
    private Button button2;
    private Button button3;
    private ImageView imageView;
    private final static int SUCCESS_STATUS = 1;
    private final static int FAIL_STATUS = 0;
    private final static String TAG = MainActivity.class.getSimpleName();
    private String image_path = "https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1470670554&di=854e40a4813dfab51d7e448106607ff9&src=http://b.hiphotos.baidu.com/image/pic/item/7a899e510fb30f2493c8cbedcc95d143ac4b0389.jpg";
    public final static String NET_DOMAIN = "http://api.zhucj.com/v1/";
    final String json_path = NET_DOMAIN + "home/recommend?page=1&per-page=20";
    final String login_path = NET_DOMAIN + "auth/access-token";
    private OkHttpClient client;
    private OKManager manager;//工具类


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS_STATUS:
                    byte[] result = (byte[]) msg.obj;
                    // Bitmap bitmap = BitmapFactory.decodeByteArray(result,0,result.length);
                    Bitmap bitmap = new CropSquareTrans().transform(BitmapFactory.decodeByteArray(result, 0, result.length));
                    imageView.setImageBitmap(bitmap);
                    break;
                case FAIL_STATUS:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) this.findViewById(R.id.button);
        button2 = (Button) this.findViewById(R.id.button2);

        button3 = (Button) this.findViewById(R.id.button3);

        imageView = (ImageView) this.findViewById(R.id.imageView);



        client = new OkHttpClient();
        //使用的是get请求
        final Request request = new Request.Builder().get().url(image_path).build();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Message message = handler.obtainMessage();
                        if (response.isSuccessful()) {
                            message.what = SUCCESS_STATUS;
                            message.obj = response.body().bytes();
                            handler.sendMessage(message);
                        } else {
                            handler.sendEmptyMessage(FAIL_STATUS);
                        }
                    }
                });
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        manager = OKManager.getInstance();
        button2.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                manager.asyncJsonStringByURL(json_path, new OKManager.Func1() {
                    @Override
                    public void onResponse(String result) {
                        Log.i(TAG,result);//获取json字符串
                    }
                });
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        button3.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                HashMap<String,String> map = new HashMap<String, String>();
                map.put("login","15201649365");
                map.put("password","12345");
                manager.sendComplexForm(login_path, map, new OKManager.Func4() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.i(TAG,jsonObject.toString());
                    }
                });
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
