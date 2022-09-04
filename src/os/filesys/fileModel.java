package os.filesys;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fileModel {
    public Map<String, fileModel> subMap = new HashMap();
    private String name;
    private String type;
    private int attr;
    private int startNum;
    private int size;
    private List<String> content = new ArrayList();
    private fileModel father = null;

    public fileModel(String name, String type, int startNum, int size, List content) {
        this.name = name;
        this.type = type;
        this.attr = 2;
        this.startNum = startNum;
        this.size = size;
        this.content = content;
    }

    public fileModel(String name, int startNum) {
        this.name = name;
        this.attr = 3;
        this.startNum = startNum;
        this.type = "  ";
        this.size = 1;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAttr() {
        return this.attr;
    }

    public void setAttr(int attr) {
        this.attr = attr;
    }

    public int getStartNum() {
        return this.startNum;
    }

    public void setStartNum(int startNum) {
        this.startNum = startNum;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<String> getContent() {
        return this.content;
    }

    public void setContent(List content) {
        this.content = content;
    }

    public fileModel getFather() {
        return this.father;
    }

    public void setFather(fileModel father) {
        this.father = father;
    }
}
