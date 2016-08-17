package comeinsteinford.github.catchcat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends Activity {

    public static final int RESULT_CODE = 9681;
    Playground playground;
    Button butRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);      //隐藏标题栏,需要继承自Activity
        setContentView(R.layout.activity_main);     //设定所关联的xml文件


        Intent gauntest = getIntent();      //利用getIntent()方法实例化其它Activity所传过来的Intent
        if (gauntest!=null){    //如果存在一个传入进来的Intent对象
            String str = getIntent().getStringExtra("title");   //把“key”为title的“Value”取出并实例化
            setTitle(str);      //将MainActivity的Title值设置为str
        }
        playground = (Playground) findViewById(R.id.PlaygroundView);
        butRefresh = (Button) findViewById(R.id.butRefresh);

        setResult(RESULT_CODE);
        //设定一个用于返回表示OnCreate完成的唯一识别码，提供给调用本窗口的startActivityForResult方法

        butRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playground.initGame();
                playground.redraw();
            }
        });
    }
}
