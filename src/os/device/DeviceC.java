package os.device;

public class DeviceC extends Device {
    /*public DeviceC(int time) {
        super(time);
        this.deviceName="deviceC";
    }*/
    public DeviceC(int count) {
        super(count);
        this.deviceName="deviceC";
    }
    public DeviceC(int count,int time){
        super(count,time);
    }
//    public static void addDevice(int num){
//        Random r=new Random();
//        for(int i=0;i<num;i++){
//            OS.lsDevice.add(new deviceC(1000+r.nextInt(1000)));
//        }
//    }
}
