import miner.HashSolver;
import miner.Pattern;
import miner.PatternParser;
import miner.PrefixSpan;
import org.apache.log4j.Logger;
import parser.CallSeqExtractor;
import parser.ListApiDemo;
import parser.entity.SCSFile;
import utils.Config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Main {
    // 配置Logger
    private static Logger logger = Logger.getLogger(Main.class);

  /**
   * 挖掘到的模式中不包含用户自定义的方法
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    logger.info("获取用户自定义方法");
    long listAPIsStartTime = System.currentTimeMillis();

    //在数据集中挖掘
   // Set<String> apis = ListApiDemo.listAPIs(new File(Config.getRepoCorpusPath1()));  // apis里面是用户自定义的方法
      Set<String> apis = ListApiDemo.listAPIs(new File(Config.getJavaCorpusPath()));
      long listAPIsEndTime = System.currentTimeMillis();
    logger.info("获取用户自定义方法" + " " + "程序运行时间：" + " " + (listAPIsEndTime - listAPIsStartTime) + " " + "ms" +
            " " + getGapTime(listAPIsEndTime - listAPIsStartTime));
    String seqPath = Config.getSeqPath();
    File seqFile = new File(seqPath);
    if (seqFile.exists()) {
      seqFile.delete();
    }

    logger.info("Step 1: Extract structured call sequence from corpus");
    long extractStartTime = System.currentTimeMillis();
    // Step 1: Extract structured call sequence from corpus
    File projectDir = new File(Config.getJavaCorpusPath());
    List<SCSFile> scsFiles = CallSeqExtractor.extract(projectDir,apis);
    long extractEndTime = System.currentTimeMillis();
    logger.info("Step 1: Extract structured call sequence from corpus" + " " + "程序运行时间：" + " " + (extractEndTime - extractStartTime) + " " + "ms" +
            " " + getGapTime(extractEndTime - extractStartTime));

    logger.info("Step 2: Hash call sequence to integer list");
    long HashSolverSolveStartTime = System.currentTimeMillis();
    // Step 2: Hash call sequence to integer list
    HashSolver solver = new HashSolver();
    solver.solve(scsFiles);
    long HashSolverSolveEndTime = System.currentTimeMillis();
    logger.info("Step 2: Hash call sequence to integer list" + " " + "程序运行时间：" + " " + (HashSolverSolveEndTime - HashSolverSolveStartTime) + " " + "ms" +
            " " + getGapTime(HashSolverSolveEndTime - HashSolverSolveStartTime));

    logger.info("Step 3: Mine patterns(frequent sub sequences) with PrefixSpan algorithm");
    long mineStartTime = System.currentTimeMillis();
    // Step 3: Mine patterns(frequent sub sequences) with PrefixSpan algorithm
    PrefixSpan.mine(Config.getSeqPath(),1, false, 1);
    long mineEndTime = System.currentTimeMillis();
    logger.info("Step 3: Mine patterns(frequent sub sequences) with PrefixSpan algorithm" + " " + "程序运行时间：" + " " + (mineEndTime - mineStartTime) + " " + "ms" +
            " " + getGapTime(mineEndTime - mineStartTime));

    logger.info("Step 4: Display patterns and fill some holes");
    long patternParserSolveStartTime = System.currentTimeMillis();
    // Step 4: Display patterns and fill some holes
    PatternParser patternParser = new PatternParser();
    patternParser.solve();
    long patternParserSolveEndTime = System.currentTimeMillis();
    logger.info("Step 4: Display patterns and fill some holes" + " " + "程序运行时间：" + " " + (patternParserSolveEndTime - patternParserSolveStartTime) + " " + "ms" +
            " " + getGapTime(patternParserSolveEndTime - patternParserSolveStartTime));

    String fileName=Config.getProjectPath()+"/data/result.txt";
    File result = new File(fileName);
    BufferedWriter writer = new BufferedWriter(new FileWriter(result));
    for (Pattern pattern: patternParser.getPatterns()) {
//      System.out.println(pattern.toString());
//      System.out.println("-----------------------------------------");
      writer.append(pattern.toString());
      writer.append('\n');
//      InnerVarSolver innerVarSolver = new InnerVarSolver(pattern.content);
//      innerVarSolver.solve();
    }
    writer.flush();
    writer.close();
  }

    /**
     * 毫秒转换为时分秒
     * @param time 毫秒数据
     * @return 时分秒字符串
     */
    public static String getGapTime(long time){
        long hours = time / (1000 * 60 * 60);
        long minutes = (time-hours*(1000 * 60 * 60 ))/(1000* 60);
        long second = (time-hours*(1000 * 60 * 60 )-minutes*(1000 * 60 ))/1000;
        String diffTime="";
        if(minutes<10){
            diffTime=hours+":0"+minutes;
        }else{
            diffTime=hours+":"+minutes;
        }
        if(second<10){
            diffTime=diffTime+":0"+second;
        }else{
            diffTime=diffTime+":"+second;
        }
        return diffTime;
    }
}
