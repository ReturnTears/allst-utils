package allst.utils.tools;

import allst.utils.service.ServiceException;
import jxl.Cell;
import jxl.Image;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 如果要输出图像,则使用: jxl.Image 类型
 *
 * @author June
 * @since 2021年09月
 */
public class JxlExcelUtil {
    /**
     * @param os 输出流
     */
    public static WritableWorkbook createWrite(OutputStream os) throws IOException {
        return Workbook.createWorkbook(os);
    }

    /**
     * @param is 输入流
     */
    public static Workbook createRead(InputStream is) throws IOException {
        try {
            return Workbook.getWorkbook(is);
        } catch (BiffException e) {
            throw new IOException(e);
        }
    }

    /**
     * @param is     输入流
     * @param sindex sindex
     */
    public static List<List<Object>> readExcel(InputStream is, int sindex) throws IOException {
        Workbook book = null;
        try {
            book = Workbook.getWorkbook(is);
            return readExcel(book, sindex);
        } catch (BiffException e) {
            throw new IOException(e);
        } finally {
            if (book != null) book.close();
        }
    }

    /**
     *
     */
    public static List<Map<String, Object>> readExcelToMapList(InputStream is, int sindex, int keyIndex, int dataStartIndex) throws IOException {
        List<List<Object>> list = readExcel(is, sindex);
        return parseListToMap(list, keyIndex, dataStartIndex);
    }

    /**
     * @param list           list
     * @param keyIndex       表示使用哪一行作为key
     * @param dataStartIndex 表示数据开始行index
     */
    public static List<Map<String, Object>> parseListToMap(List<List<Object>> list, int keyIndex, int dataStartIndex) {

        if (list == null || list.size() <= dataStartIndex) return new ArrayList<>();
        List<Object> keyObjects = CollectionUtil.getIndex(list, keyIndex);
        if (keyObjects == null || keyObjects.size() == 0) ServiceException.error("Excel数据中不包含映射Map的key行");
        List<String> keys = keyObjects.stream().map(t -> {
            return StringUtil.toString(t);
        }).collect(Collectors.toList());
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (int i = dataStartIndex, n = list.size(); i < n; i++) {
            List<Object> data = list.get(i);
            Map<String, Object> map = new HashMap<>();
            for (int index = 0, count = keys.size(); index < count; index++) {
                String key = keys.get(index);
                map.put(key, CollectionUtil.getIndex(data, index));
            }
            resultList.add(map);
        }
        return resultList;
    }

    /**
     * 如果包含图像则 类型为 jxl.Image
     */
    public static List<List<Object>> readExcel(Workbook book, int sindex) {

        Sheet sheet = book.getSheet(sindex);
        List<List<Object>> rows = new ArrayList<List<Object>>();
        int rn = sheet.getRows();
        for (int i = 0; i < rn; i++) {
            Cell[] cls = sheet.getRow(i);
            List<Object> objs = new ArrayList<Object>();
            for (Cell cl : cls) {
                objs.add(cl.getContents());
            }

            rows.add(objs);
        }

        //尝试读取图像
        int imageCount = sheet.getNumberOfImages();
        for (int i = 0; i < imageCount; i++) {
            Image img = sheet.getDrawing(i);

            int irow = (int) img.getRow() + 1; //下标+1
            int icol = (int) img.getColumn() + 1; //下标+1

            List<Object> curRow = null;
            //row不足则.进行补偿row
            if (rows.size() < irow) {
                for (int t = rows.size(); t <= irow; t++) {
                    curRow = new ArrayList<Object>();
                    rows.add(curRow);
                }
            }
            curRow = rows.get(irow - 1);

            //col不足则尝试补齐col
            if (curRow.size() < icol) {
                for (int t = curRow.size(); t <= icol; t++) {
                    curRow.add("");
                }
            }
            curRow.set(icol - 1, img);
        }
        return rows;
    }

    /**
     * 直接将数据进行输出
     *
     * @param data   首行表示标题,其他表示数据
     * @param os
     * @param sindex
     */
    public static void writeExcel(List<List<Object>> data, OutputStream os, int sindex) {
        // 打开文件
        try {
            WritableWorkbook book = Workbook.createWorkbook(os);
            WritableSheet sheet = book.createSheet(String.valueOf(sindex), sindex);
            Object temp = null;
            for (int i = 0; i < data.size(); i++) {
                List<Object> list = data.get(i);
                for (int j = 0; j < list.size(); j++) {
                    temp = list.get(j);
                    sheet.addCell(new Label(j, i, temp == null ? null : temp.toString()));
                }
            }
            book.write();
            book.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public static void writeExcel(String sheetName, List<Object> title, List<List<Object>> data, OutputStream os, int colOffset, int rowOffset) {
        // 打开文件
        try {
            WritableWorkbook book = Workbook.createWorkbook(os);
            writeExcel(book, sheetName, 0, title, data, colOffset, rowOffset);
            book.write();
            book.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void writeExcel(WritableWorkbook book, String sheetName, int sheetIndex, List<Object> title, List<List<Object>> data) {
        writeExcel(book, sheetName, sheetIndex, title, data, 0, 0);
    }

    /**
     * @param book
     * @param sheetName
     * @param sheetIndex
     * @param title
     * @param data
     * @param colOffset  列偏移
     * @param rowOffset  行偏移
     */
    public static void writeExcel(WritableWorkbook book, String sheetName, int sheetIndex, List<Object> title, List<List<Object>> data, int colOffset, int rowOffset) {
        // 打开文件
        try {
            WritableSheet sheet = book.createSheet(sheetName, sheetIndex);
            Object temp = null;

//			sheet.getSettings().setDefaultColumnWidth(24);
//			sheet.getSettings().setDefaultRowHeight(100);

//			WritableFont font = new WritableFont(WritableFont.createFont("宋体"), 16, WritableFont.BOLD);
//			WritableCellFormat titleFormat = new WritableCellFormat();
//			titleFormat.setFont(font);
//			titleFormat.setBackground(new Colour(160, 160, 160));

            int dataRowOffset = rowOffset;

            if (title != null) {
                for (int i = 0; i < title.size(); i++) {
                    temp = title.get(i);
                    Label l = new Label(i + colOffset, 0 + rowOffset, temp == null ? null : temp.toString());
//					l.setCellFormat(titleFormat);
                    sheet.addCell(l);
                }
                dataRowOffset = dataRowOffset + 1;
            }

            for (int i = 0; i < data.size(); i++) {
                List<Object> list = data.get(i);
                for (int j = 0; j < list.size(); j++) {
                    temp = list.get(j);
                    sheet.addCell(new Label(j + colOffset, i + dataRowOffset, temp == null ? null : temp.toString()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将数据对象转换成列表数据
     *
     * @param list
     * @param names
     *
     * @return 转换出来仅包含数据内容
     */
    public static List<List<Object>> buildListData(List<Map<String, Object>> list, String[] names) {
        List<List<Object>> result = new ArrayList<List<Object>>();
        if (list != null && names != null) {
            for (Map<String, Object> d : list) {
                List<Object> row = new ArrayList<Object>();
                for (String name : names) {
                    Object t = d.get(name);
                    row.add(t == null ? "" : t);
                }
                result.add(row);
            }
        }
        return result;
    }

    /**
     * 转换出来:以键为表头
     * 注意:
     * 必须保证首个map中的字段名称完整
     *
     * @param list
     *
     * @return
     */
    public static List<List<Object>> buildListTitleAndData(List<Map<String, Object>> list) {
        List<List<Object>> result = new ArrayList<List<Object>>();
        if (list != null && list.size() > 0) {

            Map<String, Object> first = list.get(0);
            Set<String> names = first.keySet();

            {
                List<Object> row = new ArrayList<Object>();
                row.addAll(names);
                result.add(row);
            }

            for (Map<String, Object> d : list) {
                List<Object> row = new ArrayList<Object>();
                for (String name : names) {
                    Object t = d.get(name);
                    row.add(t);
                }
                result.add(row);
            }
        }
        return result;
    }

    /**
     * 将第一行作为数据的key.后面的行作为值
     * 将列表数据转换成 map形式. 配合上面的方法使用.是上面方法的逆操作
     * 注意:
     * 禁止出现首行title比其他少列的情况
     *
     * @param list
     *
     * @return
     */
    public static List<Map<String, Object>> parseListData(List<List<Object>> list) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (list != null && list.size() > 0) {
            List<Object> titleObject = list.get(0);
            List<String> titles = new ArrayList<String>();
            for (Object obj : titleObject) {
                titles.add(obj.toString());
            }
            for (int i = 1, n = list.size(); i < n; i++) {
                List<Object> data = list.get(i);
                Map<String, Object> map = new HashMap<String, Object>();
                for (int ii = 0, nn = data.size(); ii < nn; ii++) {
                    map.put(titles.get(ii), data.get(ii));
                }
                result.add(map);
            }
        }
        return result;
    }

    public static <T> List<List<Object>> buildDataBean(Class<T> clazz, List<T> list, String[] propertyNames) {
        List<List<Object>> result = new ArrayList<List<Object>>();
        if (list != null && propertyNames != null) {
            for (T d : list) {
                List<Object> row = new ArrayList<Object>();
                for (String name : propertyNames) {
                    //Object t = d.get(name);
                    //row.add(t == null ? "" : t);
                }
                result.add(row);
            }
        }
        return result;
    }
}
