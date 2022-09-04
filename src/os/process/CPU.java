package os.process;


import os.OS;
import os.device.DeviceManager;
import os.device.DeviceOccupy;
import os.device.DeviceRequest;
import os.memory.Memory;
import os.memory.Program;
import os.memory.SubArea;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;
import static os.OS.*;

/**
 * 处理想要调用CPU模块的进程
 * 处理进程状态转换
 */

public class CPU implements Runnable {
    static ReentrantLock lock = new ReentrantLock();//互斥锁
    //寄存器组
    private int IR; //指令寄存器
    private int AX; //0
    private int BX; //1
    private int CX; //2
    private int DX; //3
    private int PC; //程序计数器

    private int nextIR; //下一条指令寄存器
    private int OP;
    private int DR;
    private int SR;
    private String result = "NOP";

    private Memory memory;
    public DeviceManager deviceManager;
    public DeviceOccupy deviceOccupy;

    public CPU() {
        this.memory = OS.memory;//对接内存
        deviceManager = new DeviceManager(this);//CPU包含了设备
    }

    @Override
    public void run() {
        while (OS.launched) {
            //Thread.sleep(Clock.TIMESLICE_UNIT);//模拟实际情况，开启OS用了一个时间片
            //拆分指令，获取操作OP，和OP操作对应的数据DATA
            //找到当前就绪PCB对应的Program

            //开机用了1000毫秒
            try {
                sleep(1000);//模拟实际情况，开启OS用了1000毫秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //就绪队列>1 有程序文件需要处理
            if (OS.memory.getWaitPCB().size() > 0) {//就绪队列>1 有程序文件需要处理


                //if
                //处理已经WAIT->RUN的程序
                //不初始状态的hangout程序即run=0
                //处理经过CPU的diapatch的program

                if (OS.memory.getRunningPCB().getPID() != 0) {//获取已经WAIT->RUN的program，不包括程序0（run=0）
                    //获取已经WAIT->RUN的program，当前运行的进程肯定在programLinkedList里
                    Program p = new Program().findProgram(OS.memory.getRunningPCB().getPID());


                    //指令 0000 进程结束
                    if (p.getOrders()[p.getPcb().getCounter()] == 0) {//0000终止 不是看1500终止的 实际上是看执行到的代码是不是到末尾了
                        System.out.println("CPU："+"进程："+OS.memory.getRunningPCB().getPID()+" 运行结束，被撤销");
                        //进程移除
                        OS.memory.free(OS.memory.getRunningPCB().getPID(), OS.memory);//内存释放
                        OS.memory.waitingProgramHandling();//处理之前未成功分配内存的进程
                        OS.programLinkedList.remove(p);//从进程队列中移出
                        OS.memory.setRunningPCB(null);
                    }
                    //其他指令 OP=2,3,4,5..
                    else {
                        int OP = p.getOrders()[p.getPcb().getCounter()] / 100;
                        int DATA = p.getOrders()[p.getPcb().getCounter()] % 100;
                        //System.out.printf("进程%d的counter现在为：%d",p.getPid(),p.getPcb().getCounter());
                        p.getPcb().setCounter(p.getPcb().getCounter() + 1);//程序计数器+1
                        //System.out.printf("进程%d的counter加一为：%d",p.getPid(),p.getPcb().getCounter());
//            System.out.println(OP+" "+DATA);
                        if (1 <= OP && OP <= 15) {
                            System.out.println("进程" + p.getPid() + "：executing" + "：" + OP + DATA);
                        }

                        //witch case执行指令
                        switch (OP) {
                            //CPU
                            case 2:
                                try {
                                    System.out.println("进程" + p.getPid() + "：占用cpu：" + DATA + "秒");
                                    sleep(DATA * 1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                break;
                            //Device A
                            case 3:
                                System.out.println("进程" + p.getPid() + "：申请使用：DeviceA：" + DATA + "秒");
                                try {
                                    cpu.deviceManager.addWaitForDevice(new DeviceRequest(p.getPcb(), DATA, "DeviceA"));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                break;
                            //Device B
                            case 4:
                                System.out.println("进程" + p.getPid() + "：申请使用：DeviceB：" + DATA + "秒");
                                try {
                                    cpu.deviceManager.addWaitForDevice(new DeviceRequest(p.getPcb(), DATA, "DeviceB"));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                break;
                            //Device C
                            case 5:
                                System.out.println("进程" + p.getPid() + "：申请使用：DeviceC：" + DATA + "秒");
                                try {
                                    cpu.deviceManager.addWaitForDevice(new DeviceRequest(p.getPcb(), DATA, "DeviceC"));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                break;
                            //跳过
                            case 0:
                                break;
                            case 1:
                                break;
                            default:
                                System.out.println("无效指令：" + OP + DATA);
                                break;
                        }
                    }
                }//有程序正在RUNNING的括号


                //处理程序0，即初始状态的hangout程序即run=0
                //处理run！=0，重新调度

                cpu.lock.lock();//上锁
                PCB pcb = memory.getRunningPCB();


                //检查是否存在设备中断，存在则先处理设备中断
                if (hasInterrupt) {
                    hasInterrupt = false;
                    deviceManager.deviceDone(deviceOccupy);
                }
                //等待用户从键盘发出文件操作中断
                try {
                    System.out.println("CPU" + "：请确认是否要进行文件系统操作：（默认为否（无需操作），输入f进入）");//TODO 没有加进程的标
                    System.out.println("CPU" + "：等待5秒，请输入 'f' ：");//TODO 没有加进程的标
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //检查是否存在文件操作请求中断
                if (hasFileOperationRequest) {
                    hasFileOperationRequest = false;
                    fileSystem.menu(osManager);
                } else {
                    System.out.println("CPU" + "：无文件请求");//TODO 没有加进程的标
                }


                cpu.toReady();//RUN->WAIT，即对已处理过的程序运行->就绪  2.不会执行刚读入的程序文件(处于WAIT状态)
                cpu.dispatch();//重新调度
                cpu.lock.unlock();//解锁

            }//就绪队列>1 需要处理的程序文件>1


            //就绪队列=0 没有需要处理的程序文件
            else {
                System.out.println("当前暂无程序需要运行");
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }//就绪队列=0 没有需要处理的程序文件
        }//launched括号
    }//run的括号


/*    @Override
    public void run() {
        while (OS.launched) {
            try {
                Thread.sleep(Clock.TIMESLICE_UNIT);//模拟实际情况，开启OS用了一个时间片
            } catch (InterruptedException e) {
                return;//TODO 中断处理
            }
            lock.lock();
            try {

                fetchInstruction();//取指
                identifyInstruction();//译码
                execute();//执行
                //  System.out.println("就绪队列队头进程："+memory.getWaitPCB().peek().getPID());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }*/

    /**
     * 初始化CPU
     */
    public void init() {//TODO 或许可以删掉
        IR = 0;
        AX = 0;
        BX = 0;
        CX = 0;
        DX = 0;
        PC = 0;
        deviceManager.init();//初始化设备
    }

    /**
     * 取指
     */
    public void fetchInstruction() {
        if (memory.getRunningPCB() == memory.getHangOutPCB()) {
            IR = 0;//NOP不执行
        } else {
            byte[] userArea = memory.getUserArea();
            IR = userArea[PC];
            PC++;
        }
        //    System.out.println("取指完成，开始运行指令"+IR);
    }

    /**
     * 译码
     */
    public void identifyInstruction() {
        //移位
        OP = (IR >> 4) & 0x0f;
        DR = (IR >> 2) & 0x03;
        SR = IR & 0x03;

        if (OP == 5) {
            byte[] userArea = memory.getUserArea();
            nextIR = userArea[PC];
            PC++;
        }
        //     System.out.println("译码完成");
    }

    /**
     * 执行和写回
     */
//    public void execute() {
//        result ="NOP";
//        if(IR !=0)
//        {
//            result ="";
//            switch (OP) {
//                case 1:switch (DR){  //ADD
//                    case 0:AX++;result +="INC AX, AX="+AX;break;
//                    case 1:BX++;result +="INC BX, BX="+BX;break;
//                    case 2:CX++;result +="INC CX, CX=" +CX;break;
//                    case 3:DX++;result +="INC DX, DX=" +DX;break;
//                }
//                    break;
//                case 2:switch (DR){ //DEC
//                    case 0:AX--;result +="DEC AX, AX="+AX;break;
//                    case 1:BX--;result +="DEC BX, BX="+BX;break;
//                    case 2:CX--;result +="DEC CX, CX="+ CX;break;
//                    case 3:DX--;result +="DEC DX, DX="+ DX;break;
//                }
//                    break;
//                case 3:              //!??
//                    String deviceName=null;
//                    switch (DR){
//                        case 0:deviceName="A";break;
//                        case 1:deviceName="B";break;
//                        case 2:deviceName="C";break;
//                    }
//
//                    result +="! Device: "+DR+", Time:"+SR;
//                    DeviceRequest deviceRequest=new DeviceRequest();
//                    deviceRequest.setDeviceName(deviceName);
//                    deviceRequest.setWorkTime(SR*5000);
//                    deviceRequest.setPcb(memory.getRunningPCB());
//                    deviceManager.requestDevice(deviceRequest);
//                    //阻塞进程
//                    block();
//                    dispatch();
//
//                    break;
//                case 4:result += "END";
//                    destroy();    //END
//                    dispatch();
//
//                    break;
//                case 5:switch (DR){ //MOV
//                    case 0:AX = nextIR;result +="MOV AX,"+nextIR+", AX="+AX;break;
//                    case 1:BX = nextIR;result +="MOV BX,"+nextIR+", BX="+BX;break;
//                    case 2:CX = nextIR;result +="MOV CX,"+nextIR+", CX="+ CX;break;
//                    case 3:DX = nextIR;result +="MOV DX,"+nextIR+", DX="+ DX;break;
//                }
//                    break;
//            }
//        }
    //System.out.println("指令"+IR+"运行完毕");

//    }

    /**
     * 进程调度,将进程 就绪->运行态
     */
    public void dispatch() {
        PCB pcb1 = memory.getRunningPCB();//当前正在运行的进程
        PCB pcb2 = memory.getWaitPCB().poll();//进程从WAIT队头拿出

        //没有需要处理的程序文件
        if (pcb2 == null) {
            pcb2 = memory.getRunningPCB();
        }


        //处理 有需要处理的程序文件 && 第一个要运行进程是闲逛进程
        if (memory.getWaitPCB().size() > 0 && pcb2 == memory.getHangOutPCB()) {
            memory.getWaitPCB().offer(pcb2);//进程移入WAIT队尾
            pcb2 = memory.getWaitPCB().poll();//进程从WAIT队头拿出
        }

        //WIAT->RUN
        memory.setRunningPCB(pcb2);
        pcb2.setStatus(PCB.STATUS_RUN);
        System.out.println("CPU：要运行" + "进程：" + pcb2.getPID());
        System.out.println("CPU：正在进行进程上下文的切换");
//        //保存现场
//        saveContext(pcb1);
//        //恢复现场
//        recoveryContext(pcb2);
        System.out.println("进程" + pcb2.getPID() + "：WAIT->RUN");
    }


    /**
     * 进程撤销
     */
    public void destroy() {
        PCB pcb = memory.getRunningPCB();
        System.out.println("进程" + pcb.getPID() + "运行结束,撤销进程");
        /*回收进程所占内存*/
        SubArea subArea = null;
        List<SubArea> subAreas = memory.getSubAreas();
        for (SubArea s : subAreas) {
            if (s.getTaskNo() == pcb.getPID()) {
                subArea = s;
                break;
            }
        }
        subArea.setStatus(SubArea.STATUS_FREE);
        int index = subAreas.indexOf(subArea);
        //如果不是第一个，判断上一个分区是否为空闲
        if (index > 0) {
            SubArea preSubArea = subAreas.get(index - 1);
            if (preSubArea.getStatus() == SubArea.STATUS_FREE) {
                preSubArea.setSize(preSubArea.getSize() + subArea.getSize());
                subAreas.remove(subArea);
                subArea = preSubArea;
            }
        }
        //如果不是最后一个，判断下一个分区是否空闲
        if (index < subAreas.size() - 1) {
            SubArea nextSubArea = subAreas.get(index + 1);
            if (nextSubArea.getStatus() == SubArea.STATUS_FREE) {
                nextSubArea.setSize(nextSubArea.getSize() + subArea.getSize());
                nextSubArea.setStartAdd(subArea.getStartAdd());
                subAreas.remove(subArea);
            }
        }


    }

    /**
     * 运行->就绪
     * 1.处理已经run过的程序
     * 2.处理程序0，即初始的hangOutPCB 无实际意义，为os刚开始工作服务，便于进入os的CPU
     */
    public void toReady() {
        PCB pcb = memory.getRunningPCB();
        if (pcb != null) {//已经run过的程序
            System.out.println("进程" + pcb.getPID() + "：RUN->WAIT");
            memory.getWaitPCB().offer(pcb);//运行放入就绪队列
            pcb.setStatus(PCB.STATUS_WAIT);
        }
    }

    /**
     * 将运行进程转换为阻塞态
     */
    public void block() {
        PCB pcb = memory.getRunningPCB();
        //修改进程状态
        pcb.setStatus(PCB.STATUS_BLOCK);
        //将进程链入对应的阻塞队列，然后转向进程调度
        memory.getBlockPCB().add(pcb);
    }

    /**
     * 进程唤醒，设备相关
     */
    public void awake(PCB pcb) {
        lock.lock();
        // System.out.println("唤醒进程"+pcb.getPID());
        //将进程从阻塞队列中调入到就绪队列
        pcb.setStatus(PCB.STATUS_WAIT);
        pcb.setEvent(PCB.EVENT_NOTING);
        memory.getBlockPCB().remove(pcb);
        memory.getWaitPCB().add(pcb);
        lock.unlock();
    }

    /**
     * 保存上下文
     *
     * @param pcb
     */
    private void saveContext(PCB pcb) {
        //   System.out.println("保留现场");
        pcb.setCounter(PC);
        pcb.setAX(this.AX);
        pcb.setBX(this.BX);
        pcb.setCX(this.CX);
        pcb.setDX(this.DX);
    }

    /**
     * 恢复现场
     */
    private void recoveryContext(PCB pcb) {
        //      System.out.println("恢复现场");
        pcb.setStatus(PCB.STATUS_RUN);
        this.AX = pcb.getAX();
        this.BX = pcb.getBX();
        this.DX = pcb.getDX();
        this.CX = pcb.getCX();
        this.PC = pcb.getCounter();
    }

    public String getResult() {
        String temp;
        lock.lock();
        temp = result;
        lock.unlock();
        return temp;
    }

    public DeviceManager getDeviceManager() {
        return deviceManager;
    }
}

