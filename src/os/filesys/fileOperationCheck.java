package os.filesys;

import os.OS;

import java.util.Scanner;

import static java.lang.Thread.sleep;

public class fileOperationCheck {
    public static Scanner s;
    public void init(){
        s=new Scanner(System.in);
        new Thread(()-> {
            while(OS.launched){
                if(s!=null){
                    //检查键盘输入，判定用户是否有对文件操作的需求
                    if(s.nextLine().equals("f")){
                        System.out.println("发出文件操作请求中断");
                        OS.hasFileOperationRequest=true;
                        s=null;
                    }
                    else {
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
