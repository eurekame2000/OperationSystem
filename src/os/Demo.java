package os;

import os.constant.Constant;

/**
 * 进程调度：先来先服务
 * 内存分配：首次适应
 */

public class Demo {
    public static OS os;
    //主函数
    public static void main(String []args) throws Exception {
        System.out.println("\n*********操作系统*********\n***进程调度：先来先服务****\n" +
                "****内存分配：首次适应*****"+"\n***程序最大指令数："+ Constant.ORDER_MAX+"条****\n***用户区内存大小："+Constant.USER_AREA_SIZE+"字节***\n");
        os=new OS();
        os.start();
    }
}
