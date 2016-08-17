package comeinsteinford.github.catchcat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class StarAty extends Activity {
    public static final int REQUEST_CODE = 2981;
    //设置一个请求码，用于onActivityResult中，跟RESULT_CODE构成一对用于相互确认的代码
    Handler mHandler = new Handler();
    //实例化一个Handler对象，此处handler用于在此主线程中插入一个线程，并刷新主线程UI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);      //隐藏标题栏,需要继承自Activity
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);    //调用全屏显示
        setContentView(R.layout.activity_star_aty);


        mHandler.postDelayed(new Runnable() {
            //此处调用Handler的postDelayed()方法,启动一个带有延迟的线程

            @Override
            public void run() {
                Intent intent = new Intent(StarAty.this, MainActivity.class);
                //intent直译表示一种意图
                intent.putExtra("title","围住神经猫");
                //将“key”为title，而“value”为 我是歌手 随着intent传递
                //此处putExtra()的value可传递非常多的种类，其中包括Bundle，此类可用于传递多种数据

                startActivityForResult(intent, REQUEST_CODE);
                //启动一个新Activity，携带一个请求码，并存在返回值
            }
        }, 3000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == MainActivity.RESULT_CODE&& requestCode== REQUEST_CODE){
            finish();
        }
    }
}
