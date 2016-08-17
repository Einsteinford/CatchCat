package comeinsteinford.github.catchcat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Vector;

public class Playground extends SurfaceView implements View.OnTouchListener {


    private static int WIDTH;           //定义圆球半径，由设备屏幕大小决定
    private static final int ROW = 9;      //表示行数,也就是y坐标最大值
    private static final int COL = 9;      //表示列数,也就是x坐标最大值
    private static final int BLOCKS = 15;   //默认添加的路障数量
    private static float interval = 0.75f; //设置页边距为0.75倍圆球直径


    private Dot matrix[][];     //声明由点组成的二元数组
    private Dot cat;            //声明猫

    public Playground(Context context,AttributeSet attrs) {        //类构造方法
        super(context,attrs);
        getHolder().addCallback(callback);
        matrix = new Dot[ROW][COL];     //创建矩阵点
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                matrix[i][j] = new Dot(j, i);    //遍历每一个矩阵点，并初始化创建他们
            }
        }
        setOnTouchListener(this);       //设定自己为触摸监听对象
        initGame();     //调用游戏初始化方法
    }

    private Dot getDot(int x, int y) {
        return matrix[y][x];
    }   //想要获取的坐标与实际xy轴相反

    private boolean isAtEdge(Dot d) {       //判断Dot对象是否在最外圈
        if (d.getX() * d.getY() == 0 || d.getX() + 1 == COL || d.getY() + 1 == ROW) {
            //判断点Dot 坐标是否在最外圈，即x或者y坐标为0、x或者y坐标为8
            return true;
        }
        return false;
    }

    private Dot getNeighbour(Dot one, int dir) {
        switch (dir) {
            case 1:
                return getDot(one.getX() - 1, one.getY());
            case 2:
                if (one.getY() % 2 == 0) {
                    return getDot(one.getX() - 1, one.getY() - 1);
                } else {
                    return getDot(one.getX(), one.getY() - 1);
                }
            case 3:
                if (one.getY() % 2 == 0) {
                    return getDot(one.getX(), one.getY() - 1);
                } else {
                    return getDot(one.getX() + 1, one.getY() - 1);
                }
            case 4:
                return getDot(one.getX() + 1, one.getY());
            case 5:
                if (one.getY() % 2 == 0) {
                    return getDot(one.getX(), one.getY() + 1);
                } else {
                    return getDot(one.getX() + 1, one.getY() + 1);
                }
            case 6:
                if (one.getY() % 2 == 0) {
                    return getDot(one.getX() - 1, one.getY() + 1);
                } else {
                    return getDot(one.getX(), one.getY() + 1);
                }
            default:
                break;
        }
        return null;
    }

    private int getDistance(Dot one, int dir) {
        int distance = 0;
        if (isAtEdge(one)) {
            return 1;
        }
        Dot origin = one, next;
        while (true) {
            next = getNeighbour(origin, dir);
            if (next.getStatus() == Dot.STATUS_ON) {
                return distance * -1;
            }
            if (isAtEdge(next)) {
                distance++;
                return distance;
            }
            distance++;
            origin = next;
        }
    }

    private void MoveTo(Dot one) {
        one.setStatus(Dot.STATUS_IN);
        getDot(cat.getX(), cat.getY()).setStatus(Dot.STATUS_OFF);
        cat.setXY(one.getX(), one.getY());
    }

    private void move() {
        if (isAtEdge(cat)) {
            lose();
            return;
        }
        Vector<Dot> available = new Vector<>();
        Vector<Dot> positive = new Vector<>();
        HashMap<Dot, Integer> al = new HashMap<Dot,Integer>();
        for (int i = 1; i < 7; i++) {
            Dot n = getNeighbour(cat, i);
            if (n.getStatus() == Dot.STATUS_OFF) {
                available.add(n);
                al.put(n, i);
                if (getDistance(n, i) > 0) {
                    positive.add(n);
                }
            }
        }
        if (available.size() == 0) {
            win();
        } else if (available.size() == 1) {
            MoveTo(available.get(0));
        } else {
            Dot best = null;
            if (positive.size() != 0) {     //存在可以直接到达屏幕边缘的走向
                System.out.println("向前进");
                int min = 999;
                for (int i = 0; i < positive.size(); i++) {
                    int a = getDistance(positive.get(i), al.get(positive.get(i)));
                    if (a < min) {
                        min = a;
                        best = positive.get(i);
                    }
                }
                MoveTo(best);
            } else {    //所有方向都存在路障
                System.out.println("躲路障");
                int max = 0;
                for (int i = 0; i < available.size(); i++) {
                    int k = getDistance(available.get(i), al.get(available.get(i)));
                    if (k <= max) {
                        max = k;
                        best = available.get(i);
                    }
                }
                MoveTo(best);
            }
        }
    }

    private void lose() {       //提示输了
        Toast.makeText(getContext(), "Lose", Toast.LENGTH_SHORT).show();
    }

    private void win() {        //提示获胜
        Toast.makeText(getContext(), "You Win", Toast.LENGTH_SHORT).show();
    }

    public void redraw() {      //重新绘制圆圈并上色
        Canvas c = getHolder().lockCanvas();        //锁定画图
        c.drawColor(Color.DKGRAY);      //设置画布背景颜色
        Paint paint = new Paint();      //创建画笔
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);  //打开抗锯齿
        for (int i = 0; i < ROW; i++) {       //绘制时，将单双行错开
            float offset = 0;     //单数行无偏移量
            if (i % 2 != 0) {      //如果是双数行
                offset = 0.5f;    //则偏移量设置为0.5倍球的直径
            }
            for (int j = 0; j < COL; j++) {
                Dot one = getDot(j, i);  //获取matrix[i][j]的点参数
                switch (one.getStatus()) {
                    //判断此点的状态，并设置上相应的颜色，需要等画圆的时候一起上色
                    case Dot.STATUS_OFF:
                        paint.setColor(0xFFb7c6d5);
                        break;
                    case Dot.STATUS_IN:
                        paint.setColor(0xFFf34d4d);
                        break;
                    case Dot.STATUS_ON:
                        paint.setColor(0xFFfb853d);
                        break;
                    default:
                        break;
                }

                c.drawOval(new RectF((one.getX() + offset + interval) * WIDTH, (one.getY() + interval) * WIDTH,
                        (one.getX() + 1 + offset + interval) * WIDTH, (one.getY() + 1 + interval) * WIDTH), paint);
                //绘制每一个圆圈,带色彩的，此处就是实际坐标效果了
            }
        }

        getHolder().unlockCanvasAndPost(c);     //结束锁定画图，并提交改变。
    }

    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            redraw();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            WIDTH = i1 / (COL + 2);   //根据实际设备屏幕宽度，重新修改圆圈大小
            redraw();       //刷新页面
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        }
    };

    public void initGame() {        //初始化游戏点阵
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                matrix[i][j].setStatus(Dot.STATUS_OFF);     //每一个点都默认设置为空闲
            }
        }
        cat = new Dot((int) (COL / 2), (int) (ROW / 2));     //设置猫的位置
        getDot((int) (COL / 2), (int) (ROW / 2)).setStatus(Dot.STATUS_IN);   //猫的位置状态改为IN
        for (int i = 0; i < BLOCKS; ) {               //带有判断的循环,进行路障初始化
            int x = (int) ((Math.random() * 1000) % COL);   //创建随机整型x坐标
            int y = (int) ((Math.random() * 1000) % ROW);   //创建随机整型y坐标
            if (getDot(x, y).getStatus() == Dot.STATUS_OFF) {     //如果此随机坐标状态为OFF空闲
                getDot(x, y).setStatus(Dot.STATUS_ON);           //那就在此创建一个路障
                i++;        //并且i+1
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            //表示手指触摸后移开才响应
            float x, y;
            y = (motionEvent.getY() / WIDTH - interval);     //取得点击的y坐标，精确到float
            if ((int) y % 2 == 0) {     //如果是单数数行(y从0开始取值，单数行y为双数)
                x = (motionEvent.getX() / WIDTH - interval);    //x仅减去页边距
            } else {                    //如果是单数数行
                x = (motionEvent.getX() / WIDTH - interval - 0.5f); //x减去页边距，并减去offset量
            }
            if ((int) x + 1 > COL || (int) y + 1 > ROW || x < 0 || y < 0) {     //如果点击范围超出游戏主窗口
//                initGame();     //则重置游戏
            } else if (getDot((int) x, (int) y).getStatus() == Dot.STATUS_OFF) {        //点击的坐标点如果是空闲的
                getDot((int) x, (int) y).setStatus(Dot.STATUS_ON);      //则将此坐标点状态变更为路障
                move();     //cat进行move动作
            }
//            Toast.makeText(getContext(), x + ":" + y + "  " + "\n" + motionEvent.getX() + " " + motionEvent.getY(),
//                    Toast.LENGTH_SHORT).show();
            redraw();       //图像刷新
        }
        return true;
    }


}
