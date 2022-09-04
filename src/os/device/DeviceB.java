package os.device;

public class DeviceB extends Device {
    /*public DeviceB(int time) {
        super(time);
        this.deviceName="deviceB";
    }*/
    public DeviceB(int count) {
        super(count);
        this.deviceName="deviceB";
    }
    public DeviceB(int count,int time){
        super(count,time);
    }
//    public static void addDevice(int num){
//        Random r=new Random();
//        for(int i=0;i<num;i++){
//            OS.lsDevice.add(new deviceB(1000+r.nextInt(1000)));
//        }
//    }
}
