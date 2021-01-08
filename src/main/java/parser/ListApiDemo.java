package parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.apache.commons.lang3.StringUtils;
import parser.meta.DirExplorer;
import utils.Config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListApiDemo {

  static String curPackage;
  static Set<String> apis = new HashSet<>();

  public static Set<String> listAPIs(File projectDir) {
    new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
      // System.out.println(Strings.repeat("=", path.length()));
      try {
        new VoidVisitorAdapter<Object>() {
          public void visit(PackageDeclaration n, Object arg) {
            super.visit(n, arg);
            //获得包声明   eg：package ****** ——> *******
            curPackage = n.getNameAsString();
          }
          @Override
          public void visit(ClassOrInterfaceDeclaration n, Object arg) {
            super.visit(n, arg);
            //cName为类的全称，com.xx.类名
            String cName = curPackage + "." + n.getNameAsString();
            apis.add(cName);

            for (MethodDeclaration method: n.getMethods()) {
              if (method.isPrivate()) continue;
              String mName = method.getNameAsString();
              if (mName.equals("main")) continue;

              //cName.mNmae是方法名的全称，com.company.NCE02.test2
              apis.add(cName + "." + mName);
            }
          }
        }.visit(JavaParser.parse(file), null);
      } catch (IOException e) {
        new RuntimeException(e);
      } catch (ParseProblemException e) {
        new RuntimeException(e);
      }
    }).explore(projectDir);
    return apis;
  }

  public static void main(String[] args) throws IOException {
    File projectDir = new File(
//            Config.getLibSrcPath()
            "data/test"
//            Config.getRepoCorpusPath()
    );
    Set<String>api =  listAPIs(new File(Config.getJavaCorpusPath()));
    for (String ss:api){
      System.out.println(ss);
    }
//    FileWriter fw = new FileWriter("test/ListApiDemo");
//    fw.write(String.join("\n", apis));
//    fw.close();
//    System.out.println("tot " + apis.size() + " apis");
  }
}