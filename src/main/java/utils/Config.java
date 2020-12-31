package utils;

public class Config {
  public static String getGithubAccessToken() {
    return "your_github_access_token";
  }

  public static String getFileCorpusPath() {
    return "data/test";
  }

  /**
   * 支持传入的路径可以为项目仓库的或者单个代码文件（.java）的
   * @return
   */
//  public static String getJavaCorpusPath() { return "G:\\NCE\\src\\"; }
//  public static String getJavaCorpusPath() { return "G:\\NCE\\src\\main\\java\\com\\company\\NCE08_1_mulMethod.java"; }
  public static String getJavaCorpusPath() { return "G:\\NCE\\src\\main\\java\\com\\company\\NCE02.java"; }
//  public static String getJavaCorpusPath() { return "E:\\bigcode\\dklaputa"; }

//  public static String getFilterPackage() {
//    return "org.apache.poi";
//  }

//  public static String getSearchQuery() {
//    return "apache+poi";
//  }

  public static String getLibraryApiPath() {
    return "data/library_api.txt";
  }

  public static String getLibrartSigPath() {
    return "data/library_sig.txt";
  }

  public static String getSeqPath() {
    return "data/pattern/" + (useShortName()? "short/": "full/") + "scs_seq.txt";
  }
  //======================
  public static String testSeqPath() {
    return "data/pattern/" + (useShortName()? "short/": "full/") + "scs_seq1.txt";
  }
  //===========================
  public static String getPatternsPath() {
    return "data/pattern/" + (useShortName()? "short/": "full/") + "pattern.txt";
  }

  public static String getLibSrcPath() {
    return "/Users/maxkibble/Documents/Data/DomainNLI/" + "src";
  }

//  public static String getCurrentTask() { return "fill_color"; }

  public static boolean useShortName() {
    return false;
  }

  public static String getHoleWithType(String type) {
    return "<HOLE: " + type + ">";
  }

  //获取当前项目的绝对路径
  public static String getProjectPath(){
    String path = System.getProperty("user.dir");
    String[] path1=path.split("\\\\");
    path="";
    for (int i=0;i<path1.length;i++){
      if (i!=path1.length-1){
        path=path+path1[i]+"\\\\";
      }else {
        path=path+path1[i];
      }
    }
    return path;
  }
}
