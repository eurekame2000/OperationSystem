package os.device;

public class DeviceA extends Device {
    //构造函数
   /* public DeviceA(int time) {
        super(time);
        this.deviceName="deviceA";
    }*/
    public DeviceA(int count) {
        super(count);
        this.deviceName="deviceA";
    }
    public DeviceA(int count,int time){
        super(count,time);
    }
    //生成指定个数的设备中断
//    public static void addDevice(int num){
//        for(int i=0;i<num;i++){
//            OS.lsDevice.add(new DeviceA(num));
//        }
//    }
}
