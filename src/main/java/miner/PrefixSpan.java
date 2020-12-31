package miner;

import org.apache.commons.lang3.StringUtils;
import sun.nio.cs.ext.GBK;
import utils.Config;

import java.io.*;

public class PrefixSpan {

  public static void mine(String seqList, int minLen, boolean ifTopK, int value) {

    String ret = "";
    try {
//      Process p = Runtime.getRuntime().exec(new String[]{ "cmd", "/c", "prefixspan-cli top-k " + topK + " --minlen=" + minLen + " --closed " + seqList});
      String path = Config.getProjectPath();
      String cmdStr = "python" + " " +path+ "\\PrefixSpan-py-master\\prefixspan-cli" + " ";
      if (ifTopK) cmdStr += "top-k" + " " + value + " " + "--minlen=" + minLen + " " + seqList;
      else cmdStr += "frequent" + " " + value + " " + "--minlen=" + minLen + " " + seqList;
      Process p = Runtime.getRuntime().exec(cmdStr);
      BufferedReader stdInput = new BufferedReader(new
              InputStreamReader(p.getInputStream(), "GBK"));
      BufferedReader stdError = new BufferedReader(new
              InputStreamReader(p.getErrorStream(), "GBK"));

      String s, errors = "";
      while ((s = stdInput.readLine()) != null) {
        ret += s + "\n";
      }
      while ((s = stdError.readLine()) != null) {
        errors += s + "\n";
      }
      if (errors.length() > 0) {
        System.out.println("Here is the standard error of PrefixSpan (if any):\n" + errors);
      }

      FileWriter fw = new FileWriter(Config.getPatternsPath());

      fw.write(ret);
      fw.close();
    }
    catch (IOException e) {
        System.out.println("exception happened in PrefixSpan - here's what I know: ");
        e.printStackTrace();
        System.exit(-1);
    }
  }

  public static void main(String[] args) throws IOException {
//    String seqPath = Config.getSeqPath();
//    System.out.println("这是seqPath：  "+seqPath);
//    PrefixSpan.mine(seqPath,4, 3);
    //获取当前项目的路径

  }

}
