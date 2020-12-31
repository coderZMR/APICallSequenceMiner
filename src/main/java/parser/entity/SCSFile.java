package parser.entity;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * 每一个Java文件为一个SCSFile
 * path为文件路径
 * libRelated
 * importedLibraries记录该Java文件中的import的Libraries信息
 * units为该Java文件中所有的方法的实例（SCSUnit）
 */
public class SCSFile {
  private String path;
  private boolean libRelated;
  private List<String> importedLibraries;
  private List<SCSUnit> units;

  public SCSFile() {
    libRelated = false;
    importedLibraries = new ArrayList<>();
    units = new ArrayList<>();
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }

  public void setLibRelated(boolean related) {
    this.libRelated = related;
  }

  public boolean getLibRelated() {
    return libRelated;
  }

  public List<String> getImportedLibraries() {
    return importedLibraries;
  }

  public List<SCSUnit> getUnits() {
    return units;
  }

  public void addLibrary(String library) {
    if (!importedLibraries.contains(library)) {
      importedLibraries.add(library);
    }
  }

  public void addUnit(SCSUnit unit) {
    units.add(unit);
  }

  /**
   * 返回Java文件SCSFile实例的展示内容
   * @return
   */
  public String toString() {
    String ret = path + "\n";
    ret += Strings.repeat("=", path.length());
    ret += "\nImported libraries:\n";
    for (String library: importedLibraries) {
      ret += "  " + library + "\n";
    }
    ret += "--------\nSCSUnits:\n";
    for (SCSUnit unit: units) {
      ret += unit.toString();
    }
    return ret;
  }
}
