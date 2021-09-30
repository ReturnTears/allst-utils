package allst.utils.tools;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author June
 * @since 2021年09月
 */
public class ExcelUtil {
    /**
     *
     */
    public static List<List<Object>> readRows(String excelFile, int startRowIndex, int rowCount) throws IOException {
        return readRows(new FileInputStream(excelFile), startRowIndex, rowCount);
    }

    /**
     * 在try {} catch {} 的catch块中写业务逻辑，这种逻辑存在一定的风险，若A调用了的B的某个工具类,B捕获了异常且做了处理没有抛出来，
     * 那么这时候可能A就执行不到catch块中的逻辑， 这时就出现了逻辑Bug，
     *
     * 建议：catch语句块中尽量不要写业务逻辑，就打印写异常日志就可以了
     */
    public static List<List<Object>> readRows(InputStream is, int startRowIndex, int rowCount) throws IOException {
        Workbook wb = null;
        ByteArrayOutputStream byteOS = new ByteArrayOutputStream();
        byte[] buffer = new byte[512];
        int count = -1;
        while ((count = is.read(buffer)) > 0)
            byteOS.write(buffer, 0, count);
        byteOS.close();
        byte[] allBytes = byteOS.toByteArray();

        try {
            wb = new XSSFWorkbook(new ByteArrayInputStream(allBytes));
        } catch (Exception ex) {
            wb = new HSSFWorkbook(new ByteArrayInputStream(allBytes));
        }

        Sheet sheet = wb.getSheetAt(0);

        return readRows(sheet, startRowIndex, rowCount);
    }

    /**
     *
     */
    public static List<List<Object>> readRows(Sheet sheet, int startRowIndex, int rowCount) {
        List<List<Object>> rowList = new ArrayList<List<Object>>();
        int i = 0;
        for (Row row : sheet) {
            i++;
            if (i > startRowIndex && i <= (startRowIndex + rowCount)) {
                ArrayList<Object> cellList = new ArrayList<Object>();
                for (Cell cell : row) {
                    cellList.add(readCell(cell));
                }
                rowList.add(cellList);
            }
        }
        return rowList;
    }

    /**
     *
     */
    public static Object readCell(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                String str = cell.getRichStringCellValue().getString();
                return str == null ? "" : str.trim();
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();
            case Cell.CELL_TYPE_FORMULA:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getCellFormula();
                }
            case Cell.CELL_TYPE_BLANK:
                return "";
            default:
                System.out.println("Data error for cell of excel: " + cell.getCellType());
                return "";
        }
    }

    /**
     * 暂时未处理,只做字符串处理
     */
    public static void writeCell(Cell cell, Object obj) {
        cell.setCellValue(obj.toString());
    }


    public static void main(String[] args) throws FileNotFoundException, IOException {
        List<List<Object>> rows = readRows("E:/data/1.xls", 0, 10);
        for (List<Object> row : rows) {
            for (Object cell : row) {
                System.out.print(cell + "\t");
            }
            System.out.println();
        }
    }
}
