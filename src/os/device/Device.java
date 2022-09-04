package os.device;

import java.util.concurrent.atomic.AtomicInteger;

public class Device {
    //设备类型
    protected String deviceName;
    //设定使用设备的时间
    protected int itrTime;
    //设备数量
    private  volatile AtomicInteger count;
    //构造器csy
//    public device(int time){
//        this.itrTime=time;
//    }
    public Device(int count){
        this.count=new AtomicInteger(count);
    }
    public Device(int count,int time){
        this.count=new AtomicInteger(count);
        itrTime=time;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getItrTime() {
        return itrTime;
    }

    public void setItrTime(int itrTime) {//TODO 应该可以删除
        this.itrTime = itrTime;
    }

    public int getCount() {
        return count.intValue();
    }

    public void setCount(int count) {
        this.count.set(count);
    }

    public void increaseCount(){
        count.getAndIncrement();
    }

    public int decreaseCount(){
        return count.getAndDecrement();
    }
}
