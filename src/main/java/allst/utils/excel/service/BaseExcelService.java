package allst.utils.excel.service;

import allst.utils.excel.bean.ExportBean;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author June
 * @since 2021年09月
 */
public interface BaseExcelService {
    /**
     * 导出数据为EXCEL
     *
     * @param title     标题
     * @param fieldList 导出字段
     * @param dataList  数据列表
     */
    ByteArrayOutputStream exportExcel(String title, List<ExportBean> fieldList, List<Map<String, Object>> dataList);

    /**
     * 导出数据为EXCEL
     *
     * @param title     标题
     * @param fieldList 导出字段
     * @param dataList  数据列表
     */
    ByteArrayOutputStream exportExcelT(String title, List<ExportBean> fieldList, List<?> dataList);

    /**
     * 导出excel模版
     *
     * @param response       HttpServletResponse
     * @param exportBeanList 导出字段
     * @param extendData     扩展字段
     */
    void exportExcelTemplate(HttpServletResponse response, List<ExportBean> exportBeanList, Map<String, Object> extendData) throws IOException;

    /**
     * 导出excel模版
     *
     * @param exportBeanList 导出字段
     * @param extendData     扩展字段
     */
    ByteArrayOutputStream exportExcelTemplate(List<ExportBean> exportBeanList, Map<String, Object> extendData) throws IOException;

    /**
     * 获取excel表格数据
     *
     * @param fileIn 输入流
     */
    List<Map<String, Object>> getTableDataFromExcel(InputStream fileIn) throws IOException;

    /**
     * 导入excel数据
     *
     * @param is         输入流
     * @param funType    功能类型
     * @param extendData 额外数据
     */
    void importExcel(InputStream is, String funType, Map<String, Object> extendData) throws Exception;
}
