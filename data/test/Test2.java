import org.apache.poi.ss.usermodel.*;

public class Test2 {

  private void fun(Workbook wb) {
    CellStyle style = wb.createCellStyle();
    Sheet sheet = wb.createSheet("sheet");
    Row row = sheet.createRow(0);
    Cell cell = row.createCell(0);
    style.setFillForegroundColor(IndexedColors.RED.getIndex());
    style.setFillPattern(FillPattern.SOLID);
    cell.setCellStyle(style);
  }

  @Test
  public void testPrefixSpan(){
    String seqPath = Config.getSeqPath();
    System.out.println("这是seqPath：  "+seqPath);
    PrefixSpan.mine(seqPath,4, 3);
  }


}