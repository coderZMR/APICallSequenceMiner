package parser.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 每个Java文件中的每个方法为一个SCSUnit
 * methodShortName为该SCSUnit实例所代表的方法名
 * invocationShortNames记录的该SCSUnit实例所代表的方法内的方法调用
 */
public class SCSUnit {
  private String methodShortName;
  private List<String> invocationShortNames;

  public SCSUnit() {
    methodShortName = "";
    invocationShortNames = new ArrayList<>();
  }

  public void setMethodShortName(String name) {
    methodShortName = name;
  }

  public List<String> getInvocationShortNames() {
    return invocationShortNames;
  }

  public SCSUnit copy() {
    SCSUnit unitCopy = new SCSUnit();
    unitCopy.setMethodShortName(methodShortName);
    for (String item: invocationShortNames) {
      unitCopy.add(item);
    }
    return unitCopy;
  }

  /**
   * 记录方法调用
   * @param methodShortName
   */
  public void add(String methodShortName) {
    invocationShortNames.add(methodShortName);
  }

  /**
   * 清空记录的方法调用
   */
  public void clear() {
    invocationShortNames.clear();
  }

  /**
   * 返回记录的方法调用的展示内容
   * @return
   */
  public String toString() {
    String ret = methodShortName + ":\n";
    for (String name: invocationShortNames) {
      ret += "  " + name + "\n";
    }
    return ret;
  }
}
