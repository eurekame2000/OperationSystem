package os;


import os.device.Device;
import os.filesys.OSManager;
import os.filesys.fileOperationCheck;
import os.filesys.testFileSystem;
import os.memory.Memory;
import os.memory.Program;
import os.process.CPU;
import os.process.Clock;
import os.process.ProcessCreator;
import os.timer.Timer;

import java.util.LinkedList;
import java.util.List;

public class OS {
    public static OS os;
    public static ProcessCreator processCreator;//进程创建器
    public static CPU cpu;//CPU
    public static Memory memory;//内存
    public static volatile boolean launched;//默认启动
    public static Timer timer;//处理各设备中断的计时器
    public static Clock clock;//时钟

//    public Thread destroyProgram;//撤销进程，回收内存空间的进程

    public static volatile List<Program>waitingProgramList=new LinkedList<>();//申请分配内存失败的进程列表
    public static LinkedList<Device> lsDevice=new LinkedList<>();//设备中断列表
    //每一次执行一条指令 不管是什么指令 都重新调度进程
    public static LinkedList<Program> programLinkedList=new LinkedList<>();//程序每执行一条指令，都想要调用cpu模块，去排队

    public static boolean hasInterrupt=false;//有设备中断
    public static boolean hasFileOperationRequest=false;//有文件中断
    //public static DeviceManager deviceManager=new DeviceManager();
    public static testFileSystem fileSystem=new testFileSystem();
    public static OSManager osManager=new OSManager();
    public static fileOperationCheck fileOperationCheck=new fileOperationCheck();

    static {
        try {
            os = new OS();
            memory = new Memory();//自动调用构造器，并初始化memory
            cpu = new CPU();
            clock = new Clock();
            processCreator = new ProcessCreator();//自动调用构造器
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //构造器，开始
//    public void OS() throws Exception {
//        if (!os.launched) {
//            os.launched = true;
//            start();//启动系统
//        } else {
//            close();//关闭系统
//        }
//    }

    /**
     * 启动系统，起点
     */
    public void start() throws Exception {

        init();//TODO 或许可以删掉
        os.launched=true;
        new Thread(cpu).start();//CPU工作，自动
        new Thread(clock).start();//系统时钟工作
        OS.cpu.deviceManager.init();//初始化设备
        OS.fileOperationCheck.init();//判断是否需要文件操作
        //在new Memory();//自动调用构造器，并初始化memory
        //在new processCreator时，会自动调用processCreator();
    }

    /**
     * 初始化系统
     */
    public void init() throws Exception {
//        cpu.init();
//        memory.init();
//        clock.init();
    }

    /**
     * 关闭系统资源
     */
    public void close() {
        launched = false;
    }

/*
    //构造器
    public OS() throws Exception {
        memory = new Memory();
        launched = true;
        lsDevice = new LinkedList<>(Collections.synchronizedList(new LinkedList<>()));
        //创建处理设备中断的时钟
        createClock();
        //在设备中断队列中添加中断
        addDevice();
        interruptedCheck();
        //申请内存空间
        malloc(memory);
        //释放内存空间
        free(memory);
        //打印当前内存空间
//        OS.memory.printSubareas();
        //处理之前未成功分配内存的进程
        waitingProgramHandling();
        //等待并结束操作系统主线程
        join();
//        //打印当前内存空间
//        OS.memory.printSubareas();
        System.out.println("程序结束\n");
    }


    //创建设备中断处理类
    private void createClock() {
        timer = new Timer();
    }

    //生成设备中断（调试使用）
    private static void addDevice() {
        for (int i = 0; i < 1; i++) {
           *//* new DeviceA(-1).addDDvice(1);
            new DeviceB(-1).addDDvice(1);
            new DeviceC(-1).addDevice(1);*//*
            DeviceA a=new DeviceA(1);//A设备1个
            DeviceB b=new DeviceB(1);//B设备1个
            DeviceC c=new DeviceC(1);//C设备1个
            //将设备加入设备中断列表
            OS.lsDevice.add(a);
            OS.lsDevice.add(b);
            OS.lsDevice.add(c);
            //随机设置设备中断时间
            Random r=new Random();
            a.setItrTime(1000+r.nextInt(1000));
            b.setItrTime(453+r.nextInt(1000));
            c.setItrTime(745+r.nextInt(1000));
        }
    }

    //每隔一个时钟周期或者每进行一次进程调度，检查当前是否存在中断
    //存在中断就唤醒中断处理线程处理中断
    public static void interruptedCheck() {
        timer.handle();
    }

    //内存分配测试代码
    private synchronized void malloc(Memory memory) throws Exception {
        programList = Collections.synchronizedList(new LinkedList<>());
        waitingProgramList = Collections.synchronizedList(new LinkedList<>());
        int randomNum = new Random().nextInt(100) + 1;
        System.out.printf("进程总数：%d\n", randomNum);//TODO
        //int randomNum=40;
        for (int i = 0; i < randomNum; i++) {
            ProcessCreator p = new ProcessCreator();
            p.setProgram(createProgram());
            p.setPid(i);
            programList.add(p);
            try {
                memory.malloc(programList.get(i).getProgram(), memory, p.getPid());
            } catch (Exception e) {
                System.out.printf("为大小为：%d 的进程:%d 分配内存失败，暂时放回等待列表！\n", p.getProgram().length, p.getPid());
                waitingProgramList.add(programList.remove(programList.size() - 1));
//                OS.printls(OS.programList);
//                System.out.println();
//                OS.printls(OS.waitingProgramList);
            }
        }
    }

    //测试内存回收功能函数
    public synchronized static void free(Memory memory) {
        for (int i = programList.size(); i > 0; i--) {
            int randomNum = new Random().nextInt(programList.size());
            Program p = programList.remove(randomNum);
            memory.free(p.getPid(), memory);
        }
    }

    //随机生成程序，以测试内存分配功能
    private byte[] createProgram() {
        Random r = new Random();
        String s = "";
        int randomNum = r.nextInt(100);
        for (int i = 0; i < randomNum; i++) {
            s += "1";
        }
        return s.getBytes(StandardCharsets.UTF_8);
    }

    //处理等待分配内存的进程队列的函数，用于测试内存分配功能
    private void waitingProgramHandling() {
        takeFromWaiting = new Thread(new Program.waitingProgramCheck());
        takeFromWaiting.start();
        destroyProgram = new Thread(new Program.destroyProgram());
        destroyProgram.start();
    }

    //等待各线程结束，结束主线程
    private void join() throws InterruptedException {
        new Thread(new Check()).start();
        takeFromWaiting.join();
        destroyProgram.join();
    }

    //打印列表，测试用
    public static void printls(List<Program> l) {
        for (int i = 0; i < l.size(); i++) {
            System.out.println(l.get(i).getPid());
        }
    }

    //检查各线程是否为空，用于自动停止程序
    public class Check implements Runnable {
        @Override
        public void run() {
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (true) {
                if (lsDevice.size() == 0
                        && waitingProgramList.size() == 0 && programList.size() == 0) {
                    launched = false;
                    break;
                } else {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }*/
}
