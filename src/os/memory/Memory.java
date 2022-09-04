package os.memory;

import os.OS;
import os.constant.Constant;
import os.process.PCB;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

/**
 * 内存模块
 */
public class Memory {
    //内存分配表
    private volatile List<SubArea> subAreas;
    //用户区内存
    private byte[] userArea;
    //就绪进程控制块
    private Queue<PCB> waitPCB;
    //运行进程
    private PCB runningPCB;
    //阻塞进程控制块
    private Queue<PCB> blockPCB;
    //闲逛进程
    private PCB hangOutPCB;//程序0：无实际意义，为os刚开始工作服务，便于进入os的CPU

    public Thread takeFromWaiting;//为等待进程队列中进程分配内存的进程
    //互斥锁
    private static Lock lock = new ReentrantLock();

    //构造器
    public Memory() {
        subAreas = Collections.synchronizedList(new LinkedList<>());
        userArea = new byte[Constant.USER_AREA_SIZE];
        waitPCB = new LinkedList<>();
        blockPCB = new LinkedList<>();//TODO 与可能Program的等待队列重复
        hangOutPCB = new PCB();//程序0：无实际意义，为os刚开始工作服务，便于进入os的CPU
        init();
    }

    public void init() {
        Arrays.fill(userArea, (byte) 0);
        subAreas.removeAll(subAreas);
        SubArea subArea = new SubArea();
        subArea.setSize(Constant.USER_AREA_SIZE);
        subArea.setStartAdd(0);
        subArea.setStatus(SubArea.STATUS_FREE);
        subAreas.add(subArea);
        waitPCB.removeAll(waitPCB);
        blockPCB.removeAll(blockPCB);
        hangOutPCB.setStatus(PCB.STATUS_RUN);//程序0：无实际意义，为os刚开始工作服务，便于进入os的CPU
        runningPCB = hangOutPCB;//程序0：无实际意义，为os刚开始工作服务，便于进入os的CPU
    }

    public int getStartAdd(int pid) {
        for (int i = 0; i < subAreas.size(); i++) {
            if (subAreas.get(i).getTaskNo() == pid) {
                return subAreas.get(i).getStartAdd();
            }
        }
        return -1;
    }

    public List<SubArea> getSubAreas() {
        return subAreas;
    }

    public void setSubAreas(List<SubArea> subAreas) {
        this.subAreas = subAreas;
    }

    public byte[] getUserArea() {
        return userArea;
    }

    public void setUserArea(byte[] userArea) {
        this.userArea = userArea;
    }

    //分配内存
    //TODO program.length改为从程序文件中获取的大小
    public synchronized void malloc(Program p, Memory memory, int pid) throws Exception {

        /*申请内存*/
        SubArea subArea = null;
        //首次适配法
        ListIterator<SubArea> it = memory.getSubAreas().listIterator();
        while (it.hasNext()) {
            SubArea s = it.next();
            if (s.getStatus() == SubArea.STATUS_FREE && s.getSize() >= p.getMemLength()) {
                subArea = s;
                break;
            }
        }
        if (subArea == null)
            throw new Exception("内存不足!\n");

        //如果区域过大，分出一块新的空闲区成两块
        if (subArea.getSize() > p.getMemLength()) {//TODO length从程序文件中读取
            int newSubAreaSize = subArea.getSize() - p.getMemLength();
            subArea.setSize(p.getMemLength());
            subArea.setStatus(SubArea.STATUS_USED);
            subArea.setTaskNo(pid);

            SubArea newSubArea = new SubArea();
            //新的空闲区域
            newSubArea.setStatus(SubArea.STATUS_FREE);
            newSubArea.setSize(newSubAreaSize);
            newSubArea.setStartAdd(subArea.getStartAdd() + subArea.getSize());
            it.add(newSubArea);
        } else {
            subArea.setSize(p.getMemLength());
            subArea.setStatus(SubArea.STATUS_USED);
            subArea.setTaskNo(pid);
        }
        System.out.printf("内存" + "：为进程：%d 分配内存，进程大小：%d，进程首地址：%d，进程结束地址：%d。\n",
                pid, p.getMemLength(), subArea.getStartAdd(), subArea.getStartAdd() + p.getMemLength());
        //TODO 将数据复制到用户区？？貌似不需要
        byte[] userArea = memory.getUserArea();
        for (int i = subArea.getStartAdd(), j = 0; i < subArea.getStartAdd() + subArea.getSize(); i++, j++) {
//            userArea[i]=p.getProgram()[j];
        }
        subAreas.sort(Comparator.comparingInt(SubArea::getStartAdd));
//        OS.memory.printSubareas();
    }

    //释放内存
    public synchronized void free(int pid, Memory memory) {
        System.out.print("进程" + pid + "：运行结束，撤销进程，");
        /*回收进程所占内存*/
        SubArea subArea = null;
        List<SubArea> subAreas = memory.getSubAreas();
        for (SubArea s : subAreas) {
            if (s.getTaskNo() == pid) {
                subArea = s;
                break;
            }
        }
        subArea.setStatus(SubArea.STATUS_FREE);
        subArea.setTaskNo(-1);
        System.out.printf("释放内存地址：%d~%d\n", subArea.getStartAdd(), subArea.getStartAdd() + subArea.getSize());
        int index = subAreas.indexOf(subArea);
//        OS.memory.printSubareas();
        //如果不是第一个，判断上一个分区是否为空闲
        if (index > 0) {
            SubArea preSubArea = subAreas.get(index - 1);
            if (preSubArea.getStatus() == SubArea.STATUS_FREE) {
                preSubArea.setSize(preSubArea.getSize() + subArea.getSize());
                subAreas.remove(subArea);
                subArea = preSubArea;
                index = index - 1;
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
//        System.out.println("释放内存后查看内存状态");
////        subAreas.sort(Comparator.comparingInt(SubArea::getStartAdd));
//        OS.memory.printSubareas();
    }

    //处理等待分配内存的进程队列的函数，用于测试内存分配功能
    public void waitingProgramHandling() {
        takeFromWaiting = new Thread(new waitingProgramCheck());
        takeFromWaiting.start();
//        destroyProgram=new Thread(new Program.destroyProgram());
//        destroyProgram.start();
    }
/*
    public void waitingProgramCheck() {
        try {
            if (OS.waitingProgramList.size() != 0) {//自旋尝试为等待队列中的进程分配内存空间
                Program p = OS.waitingProgramList.remove(0);

                try {
                    OS.processCreator.create(p);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } finally {

        }
    }*/


    public static class waitingProgramCheck implements Runnable {
        @Override
        public synchronized void run() {
            while (OS.launched) {
                lock.lock();
                try {
                    /*if(OS.waitingProgramList.size()!=0){//自旋尝试为等待队列中的进程分配内存空间

                        byte[] p=OS.waitingProgramList.remove(0);
                        try {
                            OS.processCreator.create();
                        } catch (Exception e) {
                            System.out.printf("为大小为：%d 的进程分配内存失败！暂时将其放回等待分配内存队列！\n",p.length);
                            OS.waitingProgramList.add(p);
//                            OS.printls(OS.programList);
//                            System.out.println();
//                            OS.printls(OS.waitingProgramList);
                            //OS.memory.printSubareas();
                        }
                    }*/

                    if (OS.waitingProgramList.size() != 0) {//自旋尝试为等待队列中的进程分配内存空间
                        os.memory.Program p = OS.waitingProgramList.remove(0);
                        try {
                            OS.processCreator.create(p);
                        } catch (Exception e) {
                            System.out.printf("为大小为：%d 的进程：%d 分配内存失败！暂时将其放回等待队列！\n", p.getMemLength(), p.getPid());
                            OS.waitingProgramList.add(p);
//                            OS.printls(OS.programList);
//                            System.out.println();
//                            OS.printls(OS.waitingProgramList);
                            //OS.memory.printSubareas();
                        }
                    }


                } finally {
                    lock.unlock();
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    //调试查看内存分配情况
    public void printSubareas() {
        for (int i = 0; i < subAreas.size(); i++) {
            System.out.println("打印内存区状态");
            System.out.printf("开始地址：%d\n", subAreas.get(i).getStartAdd());
            System.out.printf("内存状态：%d\n", subAreas.get(i).getStatus());
            System.out.printf("内存大小:%d\n", subAreas.get(i).getSize());
            System.out.printf("作业号:%d\n", subAreas.get(i).getTaskNo());
        }
    }

    public Queue<PCB> getWaitPCB() {
        return waitPCB;
    }

    public void addWaitPCB(PCB pcb) {
        this.waitPCB.add(pcb);
    }

    public Queue<PCB> getBlockPCB() {
        return blockPCB;
    }

    public void setBlockPCB(Queue<PCB> blockPCB) {
        this.blockPCB = blockPCB;
    }

    public PCB getRunningPCB() {
        return runningPCB;
    }

    public void setRunningPCB(PCB runningPCB) {
        this.runningPCB = runningPCB;
    }

    public PCB getHangOutPCB() {
        return hangOutPCB;
    }

    public List<PCB> getAllPCB() {
        List<PCB> allPCB = new ArrayList<>(10);
        allPCB.add(runningPCB);
        allPCB.addAll(blockPCB);
        allPCB.addAll(waitPCB);
        return allPCB;
    }
}
