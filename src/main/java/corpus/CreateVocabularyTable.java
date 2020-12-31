package corpus;

import utils.DatabaseConnector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author zmr
 * @Date 2020/10/10,1:43 PM
 */
public class CreateVocabularyTable {

    public static void main(String[] args) {

        // 访问数据库获取所有方法全名
        DatabaseConnector dbC = new DatabaseConnector();
        Connection conn = dbC.getConn();
        List<String> qNames = new ArrayList<String>();
        String sql = "SELECT qualified_name FROM api_entity WHERE api_type = 11";
        ResultSet rs = null;
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                qNames.add(rs.getString("qualified_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 将方法全名存入文件作为词表
        File vocabularyLibrary = new File("data/library_api.txt");
        if (vocabularyLibrary.exists()) vocabularyLibrary.delete();
        try {
            vocabularyLibrary.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(vocabularyLibrary));
            for (String qName : qNames) {
                bw.write(qName);
                bw.write("\n");
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
