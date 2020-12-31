package miner;

import parser.entity.SCSFile;
import parser.entity.SCSUnit;
import utils.Config;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class HashSolver {

  private List<List<String>> items;

  public HashSolver() {
    items = new ArrayList<>();
    ApiLoader.loadApis();
  }

  public List<List<String>> getItems() {
    return items;
  }


  public String locateApiFullName(List<String> libs, String shortName) {
    for (String lib: libs) {
      String candidate = lib + ".*." + shortName;
      String suffix = "." + shortName;
      for (String api: ApiLoader.getLibraryApis().keySet()) {
        if (api.endsWith(suffix) && Pattern.matches(candidate, api)) return api;
      }
    }
    return "";
  }

  //
  public void solve(List<SCSFile> scsFiles) {
    // 获得所有scsFiles中的scsUnits记录的方法调用
    for (SCSFile scsfile: scsFiles) {
      // importedLibraries记录Java文件中的import的Libraries信息
      List<String> importedLibraries = scsfile.getImportedLibraries();
      // unit记录每一个方法以及其中的方法调用
      for (SCSUnit unit: scsfile.getUnits()) {
        List<String> item = new ArrayList<>();
        for (String shortName: unit.getInvocationShortNames()) {
          // 根据词表获取方法全名
          // TODO 没有考虑到 java.lang.StringBuilder.append(java.lang.Object) 这样的API，类似的API不会有对应的import语句，
          //  导致locateApiFullName方法找不到对应结果会return ""，从而导致对应的API没有加入到序列中
          String fullName = locateApiFullName(importedLibraries, shortName);
//          System.out.println("fullName" + ":" + fullName);
          if (fullName.length() == 0) continue;
          if (Config.useShortName()) {
            item.add(shortName);
          } else {
            item.add(fullName);
          }
        }
        items.add(item);
      }
    }

    // 获得词典
    Map<String, Integer> map;
    if (Config.useShortName()) {
      map = ApiLoader.getLibraryShortApis();
    } else {
      map = ApiLoader.getLibraryApis();
    }

    // 将API转换为ID并存储到List
    List<String> seq = new ArrayList<>();
    for (List<String> item: items) {
      if (item.size() == 0) continue;
      List<String> numbers = new ArrayList<>();
      for (String str: item) numbers.add(map.get(str).toString());  // 构建每行数据
      seq.add(String.join(" ", numbers)); // 格式化并存储每行数据
    }

    // 写入文件
    try {
      FileWriter fw = new FileWriter(Config.getSeqPath());
      fw.write(String.join("\n", seq)); // 格式化并写入文件
      fw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
