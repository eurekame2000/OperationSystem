package os.device;


import os.OS;
import os.process.CPU;
import os.process.Clock;

import java.util.LinkedList;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

/**
 * 设备管理
 */
public class DeviceManager{
    private CPU cpu;
    private DeviceA a;
    private DeviceB b;
    private DeviceC c;
    //使用中的设备
    private DelayQueue<DeviceOccupy> usingDevices;
    //等待使用设备的进程队列
    private LinkedList<DeviceRequest> waitForDevice;

    public DeviceManager(CPU cpu){
        a=new DeviceA(2);//A设备2个
        b=new DeviceB(3);//B设备3个
        c=new DeviceC(3);//C设备3个
        usingDevices =new DelayQueue<>();
        waitForDevice=new LinkedList<>();
        this.cpu=cpu;//对接cpu
    }
    public void  init(){
        a.setCount(2);
        b.setCount(3);
        c.setCount(3);
        usingDevices.removeAll(usingDevices);
        //waitForDevice.removeAll(waitForDevice);
        //释放设备线程
        new Thread(()-> {
            while(OS.launched){
                try {
                    OS.cpu.deviceOccupy = usingDevices.take();
                    System.out.println("设备："+OS.cpu.deviceOccupy.getDeviceName()+"设备使用完毕，发出设备中断");
                    OS.hasInterrupt=true;
                    while(OS.hasInterrupt){
                        sleep(1000);
                    };
                    System.out.println("设备："+OS.cpu.deviceOccupy.getDeviceName()+"设备中断处理完毕");
                    OS.cpu.deviceOccupy=null;
//                    deviceDone(deviceOccupy);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //处理设备申请请求线程
        new Thread(()-> {
            while(OS.launched){
                if(waitForDevice.size()>0){
                    DeviceRequest deviceRequest=waitForDevice.remove();
                    DeviceOccupy deviceOccupy=new DeviceOccupy(deviceRequest.getPcb(),deviceRequest.getWorkTime(), TimeUnit.MILLISECONDS);
                    deviceOccupy.setDeviceName(deviceRequest.getDeviceName());
                    switch (deviceRequest.getDeviceName()){
                        case "DeviceA":
                            //如果有设备空闲就使用设备
                            if (a.getCount()>0){
                                //可用设备减1
                                System.out.println("设备："+"设备"+deviceRequest.getDeviceName()+"可用，分配给进程："+deviceRequest.getPcb().getPID());
                                a.decreaseCount();
                                usingDevices.put(deviceOccupy);
                            }
                            //否则将设备请求重新放到请求队列中
                            else {
                                System.out.println("设备："+"无可用"+deviceRequest.getDeviceName()+"设备，等待分配");
                                waitForDevice.add(deviceRequest);
                            }
                            break;
                        case "DeviceB":
                            //如果有B设备空闲就使用设备
                            if (b.getCount()>0){
                                //可用设备减1
                                System.out.println("设备："+"设备"+deviceRequest.getDeviceName()+"可用，分配给进程："+deviceRequest.getPcb().getPID());
                                b.decreaseCount();
                                usingDevices.put(deviceOccupy);
                            }
                            //否则将设备请求重新放到请求队列中
                            else {
                                System.out.println("设备："+"无可用"+deviceRequest.getDeviceName()+"设备，等待分配");
                                waitForDevice.add(deviceRequest);
                            }
                            break;
                        case "DeviceC":
                            //如果有C设备空闲就使用设备
                            if (c.getCount()>0){
                                //可用设备减1
                                System.out.println("设备："+"设备"+deviceRequest.getDeviceName()+"可用，分配给进程："+deviceRequest.getPcb().getPID());
                                c.decreaseCount();
                                usingDevices.put(deviceOccupy);
                            }
                            //否则将设备请求重新放到请求队列中
                            else {
                                System.out.println("设备："+"无可用"+deviceRequest.getDeviceName()+"设备，等待分配");
                                waitForDevice.add(deviceRequest);
                            }
                            break;
                    }
                    try {
                        sleep(Clock.TIMESLICE_UNIT);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    /**
     * 请求使用设备
     * @param
     */
    public void requestDevice(DeviceRequest deviceRequest){
        System.out.println("设备："+"进程"+deviceRequest.getPcb().getPID()+"请求使用设备"+deviceRequest.getDeviceName());
        waitForDevice.add(deviceRequest);
    }

    /**
     * 设备使用结束，释放资源，请求中断
     */
    public void deviceDone(DeviceOccupy deviceOccupy){
        //释放资源
        switch (deviceOccupy.getDeviceName()){
            case "DeviceA":a.increaseCount();break;
            case "DeviceB":b.increaseCount();break;
            case "DeviceC":c.increaseCount();break;
        }
//如果当前进程没有执行到结束，尚未被销毁
        if(OS.memory.getBlockPCB().contains(deviceOccupy.getObj())){
            //将进程从阻塞队列中移到就绪队列
            cpu.awake(deviceOccupy.getObj());
        }

    }

    public DelayQueue<DeviceOccupy> getUsingDevices() {
        return usingDevices;
    }

    public LinkedList<DeviceRequest> getWaitForDevice() {
        return waitForDevice;
    }

    public void addUsingDevices(DeviceOccupy D) {
        usingDevices.add(D);
    }

    public void addWaitForDevice(DeviceRequest D) throws InterruptedException {
        waitForDevice.offer(D);
    }
}
