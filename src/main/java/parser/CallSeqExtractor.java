package parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.apache.commons.lang3.StringUtils;
import parser.entity.SCSFile;
import parser.entity.SCSUnit;
import parser.meta.DirExplorer;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * mmap存储用户自定义的方法（key: 方法名，value：该方法所对应的全名）
 */
public class CallSeqExtractor {

  static List<SCSFile> scsFiles = new ArrayList<>();
  static Map<String, List<String>> mmap = new HashMap<>();
  static List<String> relatedFiles = new ArrayList<>();

  static class CallSeqVisitor implements DirExplorer.FileHandler {
    @Override
    public void handle(int level, String path, File file) {

      SCSFile scsFile = new SCSFile();
      scsFile.setPath(path);

      //解决java中lang包下的类不会有import语句
      String libJavaLang = "java.lang";
      scsFile.addLibrary(libJavaLang);

      SCSUnit scsUnit = new SCSUnit();

      try {
        new VoidVisitorAdapter<Object>() {
          @Override
          public void visit(ImportDeclaration n, Object arg) {
            scsFile.addLibrary(n.getNameAsString());
            super.visit(n, arg);
          }

          /**
           * 获取用户自定义的方法声明并初始化该方法的scsUnit实例
           * @param n
           * @param arg
           */
          public void visit(MethodDeclaration n, Object arg) {
            if (scsUnit.getInvocationShortNames() != null && scsUnit.getInvocationShortNames().size() != 0){
              scsFile.addUnit(scsUnit.copy());
            }

            // scsUnit对象初始化
            scsUnit.setMethodShortName(n.getNameAsString());
            scsUnit.clear();  // 调用invocationShortNames.clear()初始化scsUnit中的方法调用记录List;

            super.visit(n, arg);  // 最后访问方法体节点，若将该语句放在前面则会导致处理后的结果错误
          }

          /**
           * 将用户自定义API调用过滤掉，仅保留官方API调用和第三方库API调用
           * 不支持Lambda表达式
           * @param n
           * @param arg
           */
          public void visit(MethodCallExpr n, Object arg) {
            super.visit(n, arg);  // 先访问该方法参数部分方法调用，再访问当前方法调用

            String mName = n.getNameAsString(); // 调用的方法名（shortName）

            //排除调用户自定义方法的API调用，得到官方或者第三方库的API名字，eg：toString
            if (mmap.containsKey(mName)) {
              boolean flag = false;
              List<String> qNames = mmap.get(mName);  // 获得该方法的全名（qName），全名可能不唯一（不同包的不同类中有相同命名的方法）
              for (String lib : scsFile.getImportedLibraries()) { // 获取该Java文件import的Libraries
                for (String qName: qNames) {
                  if (StringUtils.getCommonPrefix(qName, lib).equals(lib)) {
//                    scsFile.setLibRelated(true);  // 该Java文件使用了用户自定义的方法
                    flag=true;
                    break;
                  }
                }
                if (flag) break;
              }
              if (!flag) {
                  scsUnit.add(mName);
              }
            }
            else {
              // 此处将官方API调用和第三方库API调用加入到该scsUnit中的属性invocationShortNames中
              scsUnit.add(mName);
            }
          }
        }.visit(JavaParser.parse(file), null);
      } catch (ParseProblemException | IOException e) {
        System.out.println("Exception found in parsing " + path);
        new RuntimeException(e);
      }

      if (scsUnit.getInvocationShortNames() != null && scsUnit.getInvocationShortNames().size() != 0) {
        scsFile.addUnit(scsUnit);
      }

      scsFiles.add(scsFile);
      relatedFiles.add(scsFile.getPath());
    }
  }

  public static List<SCSFile> extract(File projectDir, Set<String> apis) {
    // 将apis转存为Map结构的mmap，mmap存储用户自定义的方法（key: 方法名，value：方法全名）
    for (String api: apis) {
      String[] tokens = api.split("\\.");
      if (tokens.length == 0) {
        System.out.println(api);
      }
      //shortName是方法的名字或者类名
      String shortName = tokens[tokens.length - 1]; // shortName为方法名

      //如果mmap中有shortNmae那么qNames为对应的List,否则qNmame新建List
      List<String> qNames = mmap.getOrDefault(shortName, new ArrayList<>());  // qName为方法全名（shortName所对应的）
      qNames.add(api);

      mmap.put(shortName, qNames);  // mmap存储用户自定义的方法（key: 方法名，value：方法全名）
    }

    // 遍历并找到使用到用户自定义方法的Java文件
    new DirExplorer(((level, path, file) -> path.endsWith("java")), new CallSeqVisitor())
            .explore(projectDir);

    System.out.println("************************ scsFiles ************************");
    System.out.println(scsFiles.size());
    for (SCSFile scsfile : scsFiles) {
      System.out.println(scsfile);
    }
    return scsFiles;
  }
}
