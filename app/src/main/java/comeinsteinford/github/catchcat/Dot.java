package comeinsteinford.github.catchcat;

/**
 * Created by KK on 2016-07-14.
 */
public class Dot {      //用于记录每一个点的状态

    int x,y;    //声明每一个点的坐标
    int status; //声明每一个点的状态

    public static final int STATUS_ON = 1;  //有障碍
    public static final int STATUS_OFF = 0; //空闲
    public static final int STATUS_IN = 2;  //有猫

    public Dot(int x, int y) {      //构造方法
        this.x = x;
        this.y = y;
        status = STATUS_OFF;        //初始化状态为空闲
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setXY(int x,int y) {
        this.x = x;
        this.y = y;
    }
}
