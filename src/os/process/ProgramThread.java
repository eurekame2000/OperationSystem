package os.process;

import os.OS;
import os.memory.Memory;
import os.memory.Program;

public class ProgramThread extends Thread {
    private Program p;
    private CPU cpu;
    private Memory memory;

    public ProgramThread(Program p) {
        this.p = p;
        this.cpu = OS.cpu;
        this.memory = OS.memory;
    }

//    @Override
/*
    public void run() {
        //遍历程序的指令数组，根据指令
        for (int i = 0; i < Constant.ORDER_MAX; i++) {
            //拆分指令，获取操作OP，和OP操作对应的数据DATA
            int OP = p.getOrders()[i] / 100;
            int DATA = p.getOrders()[i] % 100;
//            System.out.println(OP+" "+DATA);
            if (1 <= OP && OP <= 15) {
                System.out.println("进程" + p.getPid() + "：executing" + "：" + OP + DATA);
            }

            //witch case执行指令
            switch (OP) {
                //CPU
                case 2:
                    //阻塞->就绪 读到CPU指令 与FIFC无关
                    System.out.println("进程" + p.getPid() + "：现在的状态是" + "：" + p.getPcb().getStatus());
                    OS.memory.getBlockPCB().remove(p.getPcb());
                    System.out.println("进程" + p.getPid() + "：out STATUS_BLOCK");
                    OS.memory.getWaitPCB().add(p.getPcb());
                    System.out.println("进程" + p.getPid() + "：" + "in STATUS_WAIT");
                    p.getPcb().setStatus(PCB.STATUS_WAIT);
                    System.out.println("进程" + p.getPid() + "：现在的状态是" + "：" + p.getPcb().getStatus());

                    //就绪->运行 CPU调度 FIFC 指的是就绪状态和运行状态之间的转换
                    //判断当前是否有实际运行进程，没有的则申请进程调度
                    //没有运行的进程 或 运行的进程处于挂起状态
                    if (memory.getRunningPCB() == null || memory.getRunningPCB() == memory.getHangOutPCB()) {
                        System.out.println("进程" + p.getPid() + "：申请进程调度");
                        //为每一个已分配内存的程序进行调度
                        cpu.lock.lock();
//                        cpu.toReady();
                        cpu.dispatch();
                        cpu.lock.unlock();
                    }

                    //运行->阻塞 释放CPU


                    break;
                //Device A
                case 3:
                    break;
                //Device B
                case 4:
                    break;
                //Device C
                case 5:
                    break;
                //进程结束
                case 15:
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
    }
*/
//    public void run() {
//        //拆分指令，获取操作OP，和OP操作对应的数据DATA
//        int OP = p.getOrders()[p.getPcb().getCounter()] / 100;
//        int DATA = p.getOrders()[p.getPcb().getCounter()] % 100;
//        p.getPcb().setCounter(p.getPcb().getCounter()+1);
////            System.out.println(OP+" "+DATA);
//        if (1 <= OP && OP <= 15) {
//            System.out.println("进程" + p.getPid() + "：executing" + "：" + OP + DATA);
//        }
//
//        //witch case执行指令
//        switch (OP) {
//            //CPU
//            case 2:
////                //阻塞->就绪 读到CPU指令 与FIFC无关
////                System.out.println("进程" + p.getPid() + "：现在的状态是" + "：" + p.getPcb().getStatus());
////                OS.memory.getBlockPCB().remove(p.getPcb());
////                System.out.println("进程" + p.getPid() + "：out STATUS_BLOCK");
////                OS.memory.getWaitPCB().add(p.getPcb());
////                System.out.println("进程" + p.getPid() + "：" + "in STATUS_WAIT");
////                p.getPcb().setStatus(PCB.STATUS_WAIT);
////                System.out.println("进程" + p.getPid() + "：现在的状态是" + "：" + p.getPcb().getStatus());
//
//                //就绪->运行 CPU调度 FIFC 指的是就绪状态和运行状态之间的转换
//                //判断当前是否有实际运行进程，没有的则申请进程调度
//                //没有运行的进程 或 运行的进程处于挂起状态
//                if (memory.getRunningPCB() == null || memory.getRunningPCB() == memory.getHangOutPCB()) {
//                    System.out.println("进程" + p.getPid() + "：申请进程调度");
//                    //为每一个已分配内存的程序进行调度
//                    cpu.lock.lock();
//                    //运行转就绪
//                    cpu.toReady();
//                    //重新调度
//                    cpu.dispatch();
//                    cpu.lock.unlock();
//                }
//
//                //运行->阻塞 释放CPU
//
//
//                break;
//            //Device A
//            case 3:
//                break;
//            //Device B
//            case 4:
//                break;
//            //Device C
//            case 5:
//                break;
//            //进程结束
//            case 15:
//                break;
//            //跳过
//            case 0:
//                break;
//            case 1:
//                break;
//            default:
//                System.out.println("无效指令：" + OP + DATA);
//                break;
//        }
//    }
}
