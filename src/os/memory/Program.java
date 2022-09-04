package os.memory;

import os.OS;
import os.constant.Constant;
import os.process.PCB;


public class Program {
    private PCB pcb;
    //进程号
    private int pid;
    //程序主体
    private byte[] program;
    //程序所需内存的长度
    private int memLength;
    //首次分配内存标志
    private boolean firstMollced=true;
    //存放程序指令代码,最多存放ORDER_MAX条指令
    private int[] orders = new int[Constant.ORDER_MAX];//初始化为默认值,int型为0

    public PCB getPcb() {
        return pcb;
    }

    public void setPcb(PCB pcb) {
        this.pcb = pcb;
    }

    public byte[] getProgram() {
        return program;
    }

    public void setProgram(byte[] program) {
        this.program = program;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getMemLength() {
        return memLength;
    }

    public void setMemLength(int memLength) {
        this.memLength = memLength;
    }

    public boolean isFirstMollced() {
        return firstMollced;
    }

    public void setFirstMollced(boolean firstMollced) {
        this.firstMollced = firstMollced;
    }


    public int[] getOrders() {
        return orders;
    }

    //将第i条指令存入指令数组
    public void setOrders(int order, int i) {
        this.orders[i] = order;
    }
    //在programList队列根据程序的pid找程序的program
    public Program findProgram(int pid){
        for(int i=0;i<OS.programLinkedList.size();i++){
            if(OS.programLinkedList.get(i).getPid()==pid){
                return OS.programLinkedList.get(i);
            }
        }
        return null;
    }

    //    public static class destroyProgram implements Runnable{
//        @Override
//        public void run() {
//            while (OS.launched) {
//                lock.lock();
//                try {
//                    if (OS.programList.size() != 0) {//撤销进程，释放进程占用的内存空间
//                        int randomNum=new Random().nextInt(OS.programList.size());
//                        Program p=OS.programList.remove(randomNum);
//                        OS.memory.free(p.getPid(),OS.memory);
////                        OS.printls(OS.programList);
//                    }
//                    else {
//                        try {
//                            sleep(2000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }finally {
//                    lock.unlock();
//                    try {
//                        sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }

}
