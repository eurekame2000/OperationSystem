package os.timer;

import os.OS;
import os.device.Device;

import static java.lang.Thread.sleep;

public class Timer{
    public static String flag="isInterrupted";
    //构造函数
    public Timer(){

    }

    public static void handle(){
        while(OS.lsDevice.size()>0){
            Device d=OS.lsDevice.pop();
            System.out.printf("正在处理：%s中断！所需时间：%d毫秒。\n",d.getDeviceName(),d.getItrTime());
            try {
                sleep(d.getItrTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("%s中断处理完毕！\n",d.getDeviceName());
        }
    }
}

