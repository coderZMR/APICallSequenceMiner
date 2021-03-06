package parser.demo;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.base.Strings;
import parser.meta.DirExplorer;

import java.io.File;
import java.io.IOException;

public class MethodCallDemo {

  public static void listMethodCalls(File projectDir) {
    new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
      System.out.println(path);
      System.out.println(Strings.repeat("=", path.length()));
      try {
        new VoidVisitorAdapter<Object>() {
          @Override
          public void visit(MethodCallExpr n, Object arg) {
            super.visit(n, arg);
            System.out.println(n.getName());
          }
          public void visit(TryStmt n, Object arg) {
            System.out.println("Start try");
            super.visit(n.getTryBlock(), arg);
            System.out.println("End try");
          }
        }.visit(JavaParser.parse(file), null);
        System.out.println(); // empty line
      } catch (IOException e) {
        new RuntimeException(e);
      }
    }).explore(projectDir);
  }

  public static void main(String[] args) {
    File projectDir = new File("data/test");
    listMethodCalls(projectDir);
  }
}