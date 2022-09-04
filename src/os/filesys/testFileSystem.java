package os.filesys;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class testFileSystem {
    int code = 0;

    public testFileSystem() {
    }

    public static void main(String[] args) {
        try {
            OSManager manager = new OSManager();
            menu(manager);
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public static void menu(OSManager manager) {
        Scanner s = new Scanner(System.in);
        String str = null;
        System.out.println("***********Welcome to use the file simulation operating system***********");
        System.out.println();
        manager.showFile();
        System.out.println("Please enter command line");

        for(; (str = s.nextLine()) != null; System.out.println("Please enter command line:")) {
            if (str.equals("exit")) {
                System.out.println("Thank you!");
                fileOperationCheck.s=new Scanner(System.in);
                break;
            }

            String[] strs = editStr(str);
            if (strs[0].equals("changeType")) {
                if (strs.length <= 3) {
                    System.out.println("The command you have entered is incorrect. Please check it.");
                } else {
                    manager.changeType(strs[1], strs[2]);
                }
            } else if (strs[0].equals("mkdir")) {
                if (strs[1].equals("c")) {
                    if (strs.length <= 3) {
                        System.out.println("The command you have entered is incorrect. Please check it.");
                    } else {
                        manager.createCatolog(strs[2]);
                    }
                } else if (strs[1].equals("f")) {
                    if (strs.length <= 4) {
                        System.out.println("The command you have entered is incorrect. Please check it.");
                    } else {
                        manager.createFile(strs[2], strs[3]);
                    }
                } else {
                    System.out.println("The command you have entered is incorrect. Please check it.");
                }
            } else if (strs[0].equals("delete")) {
                if (strs.length <= 2) {
                    System.out.println("The command you have entered is incorrect. Please check it.");
                } else {
                    manager.deleteFile(strs[1]);
                }
            } else if (strs[0].equals("rename")) {
                if (strs.length <= 3) {
                    System.out.println("The command you have entered is incorrect. Please check it.");
                } else {
                    manager.reName(strs[1], strs[2]);
                }
            } else if (strs[0].equals("search")) {
                if (strs.length <= 2) {
                    System.out.println("The command you have entered is incorrect. Please check it.");
                } else {
                    String[] roadName = strs[1].split("/");
                    manager.searchFile(roadName);
                }
            } else if (strs[0].equals("cd")) {
                if (strs.length <= 2) {
                    System.out.println("The command you have entered is incorrect. Please check it.");
                } else {
                    manager.openFile(strs[1], strs[2]);
                }
            } else if (strs[0].equals("cd..")) {
                manager.backFile();
            } else if (strs[0].equals("open")) {
                if (strs.length <= 2) {
                    System.out.println("The command you have entered is incorrect. Please check it.");
                } else {
                    manager.openFile(strs[1], strs[2]);
                }
            } else if (strs[0].equals("close")) {
                manager.backFile();
            } else if (strs[0].equals("showFAT")) {
                manager.showFAT();
            } else if (strs[0].equals("ls")) {
                manager.showFile();
            } else if (strs[0].equals("write")) {
                int addsize = manager.write(strs[1]);
                manager.reAdd(strs[1], addsize);
                manager.backFile();
            } else if (strs[0].equals("read")) {
                manager.read(strs[1]);
            } else {
                System.out.println("The command you have entered is incorrect. Please check it.");
            }
        }

    }

    public static String[] editStr(String str) {
        Pattern pattern = Pattern.compile("([a-zA-Z0-9.\\\\/]*) *");
        Matcher m = pattern.matcher(str);
        ArrayList list = new ArrayList();

        while(m.find()) {
            list.add(m.group(1));
        }

        String[] strs = (String[])list.toArray(new String[list.size()]);

        for(int i = 1; i < strs.length; ++i) {
            int j = strs[i].indexOf(".");
            if (j != -1) {
                String[] index = strs[i].split("\\.");
                strs[i] = index[0];
            }
        }

        return strs;
    }
}

