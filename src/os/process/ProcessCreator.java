package os.process;


import os.OS;
import os.constant.Constant;
import os.memory.Memory;
import os.memory.Program;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * 进程创建者
 * program层面
 * 为程序文件创建进程
 * 每执行一条程序文件的指令，就加入ProgramList队列，等待CPU模块处理指令（cpu device。。。指令）
 */
public class ProcessCreator {
    private Memory memory;
    private CPU cpu;

    /*构造器*/
    public ProcessCreator() {
        this.memory = OS.memory;
        this.cpu = OS.cpu;

        File folder = new File("src/program_file");
        File[] list = folder.listFiles();


        //获取程序文件的数目
        int fileCount = 0, folderCount = 0;
        long length = 0;
        for (File file : list) {
            if (file.isFile()) {
                fileCount++;
                length += file.length();
//                System.out.println(file.getName());//程序文件的名字
            } else {
                folderCount++;
            }
        }
        System.out.printf("进程总数：%d\n", fileCount);
//        System.out.println("文件夹的数目: " + folderCount + " 文件的数目: " + fileCount);


        //遍历程序文件
        for (File file : list) {
            System.out.println(file.getName());

            //只读取程序文件的第一行指令，得到第一条指令firstInstruction
            BufferedReader reader = null;
            String firstInstruction = null;
            try {
//                System.out.println("以行为单位读取文件内容，一次读一整行：");
                reader = new BufferedReader(new FileReader(file));
                int line = 1;
                firstInstruction = reader.readLine();
                System.out.println(file.getName() + "：line" + line + ": " + firstInstruction);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                    }
                }
            }


            /*分配PCB*/
            PCB newPCB = new PCB();//为进程分配PCB

            //根据第一条指令（01指令）获取程序需要内存的大小
            int programLength = Integer.valueOf(firstInstruction) % 100;//对指令进行取余，后两位是程序所需内存大小
            System.out.println("进程" + newPCB.getPID() + "：所需内存大小为：" + programLength);

            //程序主体属性设置
            Program p = new Program();
            p.setProgram(file.getName().getBytes());//将文件名转化为字节数组
            p.setPcb(newPCB);//pcb
            p.setPid(newPCB.getPID());//pid
            p.setMemLength(programLength);//将程序需要内存大小存在PCB中


            reader = null;
            try {

                //遍历程序文件的每一条指令，存指

//                System.out.println("以行为单位读取文件内容，一次读一整行：");
                reader = new BufferedReader(new FileReader(file));
                String tempString = null;
                int line = 0;
                // 一次读入一行，直到读入null为文件结束
                while ((tempString = reader.readLine()) != null) {
                    int order = Integer.valueOf(tempString);
                    p.setOrders(order, line);//存
                    /*// 显示行号
                    System.out.println("line " + line + ": " + tempString);*/
                    line++;
                }
                System.out.println("进程" + p.getPid() + "：完成取指令 指令存储如下：");
                for (int i = 0; i < Constant.ORDER_MAX; i++) {
                    System.out.println("进程" + p.getPid() + "：line" + i + "：" + p.getOrders()[i]);
                }
                reader.close();


                //异常处理
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                    }
                }
            }

            //分配内存 ->成功 如果程序文件有指令，则加入ProgramList，请求CPU模块对指令的处理（cpu device。。指令）
            try {
                create(p);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //TODO 睡眠几个时间片 再遍历下一个程序
        }//遍历文件的括号


    }

    /**
     * 程序层面：分配内存  ->成功->  加入ProgramList，请求CPU模块处理指令（cpu device。。指令）
     */
    public void create(Program p) throws Exception {

        try {
            memory.malloc(p, memory, p.getPid());//尝试分配内存

            System.out.println("内存：" + "--------------- 进程" + p.getPid() + " 内存分配成功---------------");


            //与Memory.java重复
            /*申请内存*//*
        SubArea subArea=null;
        //首次适配法
        ListIterator<SubArea> it=memory.getSubAreas().listIterator();
        while(it.hasNext()){
            SubArea s=it.next();
            if (s.getStatus()==SubArea.STATUS_FREE&&s.getSize()>=program.length) {
                subArea = s;
                break;
            }
        }
        if (subArea==null)
            throw new Exception("内存不足");*/

//        PCB newPCB=new PCB();//为进程分配PCB

       /* //如果区域过大，分出一块新的空闲区成两块
        if (subArea.getSize()>program.length){
            int newSubAreaSize=subArea.getSize()-program.length;
            subArea.setSize(program.length);
            subArea.setTaskNo(newPCB.getPID());//csy
            subArea.setStatus(SubArea.STATUS_USED);
            SubArea newSubArea=new SubArea();
            //新的空闲区域
            newSubArea.setStatus(SubArea.STATUS_FREE);
            newSubArea.setSize(newSubAreaSize);
            newSubArea.setStartAdd(subArea.getStartAdd()+subArea.getSize());
            it.add(newSubArea);
        }else {
            subArea.setSize(program.length);
            subArea.setTaskNo(newPCB.getPID());//csy
            subArea.setStatus(SubArea.STATUS_USED);
        }
        //  System.out.println("进程首地址："+subArea.getStartAdd());
        //将数据复制到用户区
        byte[] userArea=memory.getUserArea();
        for (int i=subArea.getStartAdd(),j=0;i<subArea.getStartAdd()+subArea.getSize();i++,j++){
            userArea[i]=program[j];
        }*/
//            OS.memory.addWaitPCB(p.getPcb());//创建的进程加入就绪队列
//            System.out.println("创建的进程ID为：" + p.getPid());


            //---》分配内存成功 就 1.配置PCB（程序文件创建program里的）2.加入ProgramList队列，等待CPU模块的处理

            //1.配置PCB（程序文件创建program里的）的 内存相关 初始化程序计数器为0 程序初始状态为就绪

            p.getPcb().setMemStart(memory.getStartAdd(p.getPid()));//通过pid查看为进程分配的内存的起始地址
            p.getPcb().setMemEnd(p.getMemLength());//TODO 有疑问,END和长度相等吗
            p.getPcb().setCounter(0);//记录当前执行的指令行数
//            p.getPcb().setStatus(PCB.STATUS_BLOCK);//初始状态为BLOCK阻塞状态
//            OS.memory.getBlockPCB().offer(p.getPcb());//创建的进程加入BLOCK阻塞队列
            p.getPcb().setStatus(PCB.STATUS_WAIT);//初始状态为 wait
            OS.memory.getWaitPCB().offer(p.getPcb());//创建的进程加入 WAIT队列
            System.out.printf("进程%d：初始状态：WAIT\n", p.getPid());


            //2.加入ProgramList队列，等待CPU模块的处理
            OS.programLinkedList.add(p);

//            System.out.printf("进程%d被放入进程队列\n",p.getPid());
//            System.out.println("队列中进程号：");
//            for(int i=0;i<OS.programLinkedList.size();i++){
//                System.out.print(OS.programLinkedList.get(i).getPid());
//            }
//            System.out.println();


//            //为每个程序创建线程
//            Thread t=new ProgramThread(p);
//            t.start();

            /*//判断当前是否有实际运行进程，没有的则申请进程调度
            //没有运行的进程，或运行的进程处于挂起状态
            if (memory.getRunningPCB() == null || memory.getRunningPCB() == memory.getHangOutPCB()) {
                System.out.println("申请进程调度");
                //为每一个已分配内存的程序进行调度
                cpu.lock.lock();
                cpu.toReady();
                cpu.dispatch();
                cpu.lock.unlock();
            }*/


        }//分配内存失败就加入内存分配等待队列
        catch (Exception e) {
            //首次内存分配失败的进程才输出错误信息
            if (p.isFirstMollced()==true) {
                System.out.printf("内存：" + "--------为大小为：%d 的进程：%d 分配内存失败，暂时放入等待列表--------\n", p.getMemLength(), p.getPid());
            }

            p.setFirstMollced(false);//此进程不是首次分配内存
            OS.waitingProgramList.add(p);//分配内存的程序加入等待队列
        }
    }

}


