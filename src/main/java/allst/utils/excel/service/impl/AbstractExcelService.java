package allst.utils.excel.service.impl;

import allst.utils.excel.bean.ExcelTemplate;
import allst.utils.excel.bean.ExportBean;
import allst.utils.excel.service.BaseExcelService;
import allst.utils.excel.util.PoiExcelUtil;
import allst.utils.tools.DateUtil;
import allst.utils.tools.JxlExcelUtil;
import allst.utils.tools.StringUtil;
import org.springframework.cglib.beans.BeanMap;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author June
 * @since 2021年09月
 */
public abstract class AbstractExcelService implements BaseExcelService {
    public static String SPLIT_STR = "-";

    /**
     * 导出数据为EXCEL
     *
     * @param title     标题
     * @param exportBeans 导出字段
     * @param dataList  数据列表
     */
    @Override
    public ByteArrayOutputStream exportExcel(String title, List<ExportBean> exportBeans, List<Map<String, Object>> dataList) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        List<Object> titles = buildTitle(exportBeans);
        List<List<Object>> datas = buildData(exportBeans, dataList);
        JxlExcelUtil.writeExcel(title, titles, datas, bos, 0, 0);
        return bos;
    }

    @Override
    public ByteArrayOutputStream exportExcelT(String title, List<ExportBean> exportBeans, List<?> dataList) {
        return exportExcel(title, exportBeans, listBeanToListMap(dataList));
    }

    /**
     * 构建excel导出数据
     */
    public List<List<Object>> buildData(List<ExportBean> exportBeans, List<Map<String, Object>> dataList) {
        List<List<Object>> datas = new ArrayList<>();
        List<String> names = new ArrayList<>();
        for (ExportBean bean : exportBeans) {
            names.add(bean.getField());
        }
        for (Map<String, Object> map : dataList) {
            List<Object> d = new ArrayList<>();
            for (String name : names) {
                d.add(map.get(name));
            }
            datas.add(d);
        }
        return datas;
    }

    /**
     * 构建excel标题
     */
    public List<Object> buildTitle(List<ExportBean> exportBeans) {
        List<Object> titles = new ArrayList<>();
        for (ExportBean bean : exportBeans) {
            titles.add(bean.getName());
        }
        return titles;
    }

    @SuppressWarnings({"unchecked"})
    private Map<String, Object> beanToMap(Object t) {
        if (t != null) {
            return BeanMap.create(t);
        }
        return null;
    }

    private List<Map<String, Object>> listBeanToListMap(List<?> list) {
        List<Map<String, Object>> returnList = new ArrayList<>();
        for (Object t : list) {
            returnList.add(beanToMap(t));
        }
        return returnList;
    }

    @Override
    public void exportExcelTemplate(HttpServletResponse response, List<ExportBean> exportBeanList, Map<String, Object> extendData) throws IOException {
        response.setContentType("application/x-excel");
        String filename = DateUtil.toDateNoSeparator(new Timestamp(System.currentTimeMillis())) + ".xls";
        response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(filename, "gbk"));
        ByteArrayOutputStream bos = this.exportExcelTemplate(exportBeanList, extendData);
        response.getOutputStream().write(bos.toByteArray());
    }

    @Override
    public ByteArrayOutputStream exportExcelTemplate(List<ExportBean> exportBeanList, Map<String, Object> extendData) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        List<ExcelTemplate> excelTemplateList = getExportExcelTemplateData(exportBeanList, extendData);
        PoiExcelUtil.createExcelTemplate(bos, excelTemplateList);
        return bos;
    }

    @Override
    public List<Map<String, Object>> getTableDataFromExcel(InputStream fileIn) throws IOException {
        return PoiExcelUtil.getTableDataFromExcel(fileIn);
    }

    public List<ExcelTemplate> getExportExcelTemplateData(List<ExportBean> exportBeanList, Map<String, Object> extendData) {
        List<ExcelTemplate> excelTemplateList = new ArrayList<>();
        ExcelTemplate excelTemplate;
        int index = 0;
        for (ExportBean field : exportBeanList) {
            excelTemplate = new ExcelTemplate();
            excelTemplate.setTableField(field.getTableField());
            excelTemplate.setName(field.getName());
            excelTemplate.setIndex(index);
            excelTemplate.setField(field.getField());
            excelTemplate.setFieldType(field.getFieldType() == null ? "textbox" : field.getFieldType());
            excelTemplateList.add(excelTemplate);
            index++;
        }
        buildExcelTemplateData(excelTemplateList, extendData);
        return excelTemplateList;
    }

    /**
     * 构建excel导出模版数据
     */
    protected List<ExcelTemplate> buildExcelTemplateData(List<ExcelTemplate> excelTemplateList, Map<String, Object> extendData) {
        String fieldType;
        for (ExcelTemplate excelTemplate : excelTemplateList) {
            fieldType = excelTemplate.getFieldType();
            if (StringUtil.checkEqual(fieldType, "combobox")) {
                excelTemplate.setData(buildComboboxData(excelTemplate.getField(), extendData));
            }
        }

        return excelTemplateList;
    }

    /**
     * 构建excel下拉选择字段数据
     */
    protected String[] buildComboboxData(String fieldName, Map<String, Object> extendData) {
        return null;
    }
}
