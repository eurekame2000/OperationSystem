package os.filesys;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class OSManager {
    public Map<String, fileModel> totalFiles = new HashMap();
    private int[] fat = new int[128];
    private fileModel root = new fileModel("root", 1);
    private fileModel nowCatalog;

    public OSManager() {
        this.nowCatalog = this.root;

        for(int i = 0; i < this.fat.length; ++i) {
            this.fat[i] = 0;
        }

        this.fat[1] = 255;
        this.fat[0] = 126;
        this.root.setFather(this.root);
        this.totalFiles.put("root", this.root);
    }

    public int setFat(int size) {
        int[] startNum = new int[128];
        int i = 2;

        for(int j = 0; j < size; ++i) {
            if (this.fat[i] == 0) {
                startNum[j] = i;
                if (j > 0) {
                    this.fat[startNum[j - 1]] = i;
                }

                ++j;
            }
        }

        this.fat[i - 1] = 255;
        return startNum[0];
    }

    public void deleteFAT(int startNum) {
        int var10000 = this.fat[startNum];
        int nowPoint = startNum;

        int nextPoint;
        int count;
        for(count = 0; this.fat[nowPoint] != 0; nowPoint = nextPoint) {
            nextPoint = this.fat[nowPoint];
            if (nextPoint == 255) {
                this.fat[nowPoint] = 0;
                ++count;
                break;
            }

            this.fat[nowPoint] = 0;
            ++count;
        }

        int[] var5 = this.fat;
        var5[0] += count;
    }

    public void AddFAT(int startNum, int addSize) {
        int nowPoint = startNum;

        for(int nextPoint = this.fat[startNum]; this.fat[nowPoint] != 255; nextPoint = this.fat[nextPoint]) {
            nowPoint = nextPoint;
        }

        int i = 2;

        for(int count = 0; count < addSize; ++i) {
            if (this.fat[i] == 0) {
                this.fat[nowPoint] = i;
                nowPoint = i;
                ++count;
                this.fat[i] = 255;
            }
        }

    }

    public void createFile(String name, String type) {
        if (this.fat[0] >= 1) {
            fileModel value = (fileModel)this.nowCatalog.subMap.get(name);
            if (value != null) {
                if (value.getAttr() == 3) {
                    System.out.println("File fails to create because the same-name catalog already exists");
                    System.out.println(" < " + this.nowCatalog.getName() + " > ");
                } else if (value.getAttr() == 2) {
                    System.out.println("File fails to create because the file already exists");
                }

                System.out.println(" < " + this.nowCatalog.getName() + " > ");
            } else if (value == null) {
                int startNum = this.setFat(1);
                List<String> content = new ArrayList();
                fileModel file = new fileModel(name, type, startNum, 1, content);
                file.setFather(this.nowCatalog);
                this.nowCatalog.subMap.put(name, file);
                this.totalFiles.put(file.getName(), file);
                int var10002 = this.fat[0]--;
                System.out.println("File is successfully created!");
                System.out.println(" < " + this.nowCatalog.getName() + " > ");
            }
        } else {
            System.out.println("File fails to create because insufficient disk space！");
        }

    }

    public int write(String name) {
        new ArrayList();
        this.nowCatalog = this.nowCatalog.getFather();
        fileModel value = (fileModel)this.nowCatalog.subMap.get(name);
        List<String> content = value.getContent();
        int size0 = content.size();
        if (this.nowCatalog.subMap.containsKey(name)) {
            System.out.println("Please enter what you want to write");
            Scanner sc = new Scanner(System.in);
            String inputString = sc.nextLine();
            String[] stringArray = inputString.split(" ");

            for(int i = 0; i < stringArray.length; ++i) {
                content.add(stringArray[i]);
            }

            value.setContent(content);
            System.out.println("Contents has been writed.");
            System.out.println(" < " + this.nowCatalog.getName() + " > ");
        } else {
            System.out.println("Write failed because there is no this file");
            System.out.println(" < " + this.nowCatalog.getName() + " > ");
        }

        return content.size() - size0;
    }

    public void read(String name) {
        this.nowCatalog = this.nowCatalog.getFather();
        if (this.nowCatalog.subMap.containsKey(name)) {
            fileModel value = (fileModel)this.nowCatalog.subMap.get(name);
            new ArrayList();
            List<String> list = value.getContent();
            Iterator iterator = list.iterator();

            while(iterator.hasNext()) {
                System.out.print((String)iterator.next() + " ");
            }

            System.out.println("\n < " + this.nowCatalog.getName() + " > ");
        } else {
            System.out.println("Read failed because there is no this file");
            System.out.println(" < " + this.nowCatalog.getName() + " > ");
        }

    }

    public void createCatolog(String name) {
        if (this.fat[0] >= 1) {
            fileModel value = (fileModel)this.nowCatalog.subMap.get(name);
            if (value != null) {
                if (value.getAttr() == 2) {
                    System.out.println("Directory fails to create because the same-name file already exists!");
                    System.out.println(" < " + this.nowCatalog.getName() + " > ");
                } else if (value.getAttr() == 3) {
                    System.out.println("Directory fails to create because the directory already exists!");
                    System.out.println(" < " + this.nowCatalog.getName() + " > ");
                }
            } else if (value == null) {
                int startNum = this.setFat(1);
                fileModel catalog = new fileModel(name, startNum);
                catalog.setFather(this.nowCatalog);
                this.nowCatalog.subMap.put(name, catalog);
                int var10002 = this.fat[0]--;
                this.totalFiles.put(catalog.getName(), catalog);
                System.out.println("Directory is successfully created!");
                System.out.println(" < " + this.nowCatalog.getName() + " > ");
            }
        } else {
            System.out.println("Directory fails to create because insufficient disk space！");
        }

    }

    public void showFile() {
        System.out.println("***************** < " + this.nowCatalog.getName() + " > *****************");
        if (!this.nowCatalog.subMap.isEmpty()) {
            Iterator var1 = this.nowCatalog.subMap.values().iterator();

            while(var1.hasNext()) {
                fileModel value = (fileModel)var1.next();
                if (value.getAttr() == 3) {
                    System.out.println("File Name:" + value.getName());
                    System.out.println("Operation Type:Folder");
                    System.out.println("Starting Disk Blocks:" + value.getStartNum());
                    System.out.println("Size: " + value.getSize());
                    System.out.println("<-------------------------------------->");
                } else if (value.getAttr() == 2) {
                    PrintStream var10000 = System.out;
                    String var10001 = value.getName();
                    var10000.println("File Name:" + var10001 + "." + value.getType());
                    System.out.println("Operation Type: Readable & Writable File");
                    System.out.println("Starting Disk Blocks:" + value.getStartNum());
                    System.out.println("Size:" + value.getSize());
                    System.out.println("<-------------------------------------->");
                }
            }
        }

        for(int i = 0; i < 2; ++i) {
            System.out.println();
        }

        System.out.println("Disk Surplus Space:" + this.fat[0] + "            Exit the system please enter:exit");
        System.out.println();
    }

    public void deleteFile(String name) {
        fileModel value = (fileModel)this.nowCatalog.subMap.get(name);
        if (value == null) {
            System.out.println("Delete failed, No File or Folder!!");
        } else if (!value.subMap.isEmpty()) {
            System.out.println("Delete failed because the folder contains files!");
        } else {
            this.nowCatalog.subMap.remove(name);
            this.deleteFAT(value.getStartNum());
            if (value.getAttr() == 3) {
                System.out.println("Folder " + value.getName() + " Have been successfully deleted");
                System.out.println(" < " + this.nowCatalog.getName() + " > ");
            } else if (value.getAttr() == 2) {
                System.out.println("File " + value.getName() + "Have been successfully deleted");
                System.out.println(" < " + this.nowCatalog.getName() + " > ");
            }
        }

    }

    public void reName(String name, String newName) {
        if (this.nowCatalog.subMap.containsKey(name)) {
            if (this.nowCatalog.subMap.containsKey(newName)) {
                System.out.println("Rename failed because the same name file already exists!");
                System.out.println(" < " + this.nowCatalog.getName() + " > ");
            } else {
                fileModel value = (fileModel)this.nowCatalog.subMap.get(name);
                value.setName(newName);
                this.nowCatalog.subMap.remove(name);
                this.nowCatalog.subMap.put(newName, value);
                System.out.println("Rename has succeed");
                System.out.println();
                System.out.println(" < " + this.nowCatalog.getName() + " > ");
            }
        } else {
            System.out.println("Rename failed because there is no this file");
            System.out.println(" < " + this.nowCatalog.getName() + " > ");
        }

    }

    public void changeType(String name, String type) {
//        this.nowCatalog = this.nowCatalog.getFather();
        if (this.nowCatalog.subMap.containsKey(name)) {
            fileModel value = (fileModel)this.nowCatalog.subMap.get(name);
            if (value.getAttr() == 2) {
                value.setType(type);
                this.nowCatalog.subMap.remove(name);
                this.nowCatalog.subMap.put(name, value);
                System.out.println("Modify type success!");
                System.out.println(" < " + this.nowCatalog.getName() + " > ");
            } else if (value.getAttr() == 3) {
                System.out.println("Change error because the folder can not modify type!！");
                this.openFile(value.getName(), value.getType());
            }
        } else {
            System.out.println("Modify error, please check whether the input file name is correct！");
        }

    }

    public void openFile(String name, String type) {
        if (this.nowCatalog.subMap.containsKey(name)) {
            fileModel value = (fileModel)this.nowCatalog.subMap.get(name);
            if (value.getAttr() == 2) {
                this.nowCatalog = value;
                if (value.getType().equals(type)) {
                    System.out.println("The file has been opened and the file size is: " + value.getSize());
                } else {
                    System.out.println("Open failed because the file's type is not correct!");
                }
            } else if (value.getAttr() == 3) {
                this.nowCatalog = value;
                System.out.println("The file has been opened!");
                System.out.println(" < " + this.nowCatalog.getName() + " > ");
            }
        } else {
            System.out.println("Open failed because the file does not exist!");
        }

    }

    public void reAdd(String name, int addSize) {
        if (this.fat[0] >= addSize) {
            this.nowCatalog = this.nowCatalog.getFather();
            if (this.nowCatalog.subMap.containsKey(name)) {
                fileModel value = (fileModel)this.nowCatalog.subMap.get(name);
                if (value.getAttr() == 2) {
                    value.setSize(value.getSize() + addSize);
                    this.AddFAT(value.getStartNum(), addSize);
                    int[] var10000 = this.fat;
                    var10000[0] -= addSize;
                    System.out.println("Addition content is successful! The file is being reopened...");
                    this.openFile(name, value.getType());
                } else {
                    System.out.println("The appended content failed, please verify that the filename is entered correctly.");
                }
            }
        } else {
            System.out.println("Addition content is failed because insufficient memory space");
        }

    }

    public void backFile() {
        if (this.nowCatalog.getFather() == null) {
            System.out.println("The document does not have a superior directory!");
        } else {
            this.nowCatalog = this.nowCatalog.getFather();
            System.out.println(" < " + this.nowCatalog.getName() + " > ");
        }

    }

    public void searchFile(String[] roadName) {
        fileModel theCatalog = this.nowCatalog;
        if (this.totalFiles.containsKey(roadName[roadName.length - 1])) {
            this.nowCatalog = this.root;
            if (this.nowCatalog.getName().equals(roadName[0])) {
                System.out.println("yes");

                for(int i = 1; i < roadName.length; ++i) {
                    if (!this.nowCatalog.subMap.containsKey(roadName[i])) {
                        System.out.println("Can't find the file or directory under this path, please check whether the path is correct!");
                        this.nowCatalog = theCatalog;
                        System.out.println(" < " + this.nowCatalog.getName() + " > ");
                        break;
                    }

                    this.nowCatalog = (fileModel)this.nowCatalog.subMap.get(roadName[i]);
                }

                if (roadName.length > 1) {
                    this.nowCatalog = this.nowCatalog.getFather();
                    System.out.println(" < " + this.nowCatalog.getName() + " > ");
                }
            } else {
                this.nowCatalog = theCatalog;
                System.out.println("Please enter the correct absolute path！");
                System.out.println(" < " + this.nowCatalog.getName() + " > ");
            }
        } else {
            System.out.println("This file or directory does not exist, please enter the correct absolute path！");
            System.out.println(" < " + this.nowCatalog.getName() + " > ");
        }

    }

    public void showFAT() {
        int var10001;
        for(int j = 0; j < 125; j += 5) {
            System.out.println("Item number | " + j + "        " + (j + 1) + "        " + (j + 2) + "        " + (j + 3) + "        " + (j + 4));
            var10001 = this.fat[j];
            System.out.println("content | " + var10001 + "        " + this.fat[j + 1] + "        " + this.fat[j + 2] + "        " + this.fat[j + 3] + "        " + this.fat[j + 4]);
            System.out.println();
        }

        int j = 125;
        System.out.println("Item number | " + j + "        " + (j + 1) + "        " + (j + 2));
        var10001 = this.fat[j];
        System.out.println("content | " + var10001 + "        " + this.fat[j + 1] + "        " + this.fat[j + 2]);
        System.out.println();
        System.out.println(" < " + this.nowCatalog.getName() + " > ");
    }
}
