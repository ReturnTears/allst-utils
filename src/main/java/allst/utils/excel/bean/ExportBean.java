package allst.utils.excel.bean;

/**
 * @author June
 * @since 2021年09月
 */
public class ExportBean {
    private String name; // 字段名称

    private String field; // 字段属性

    private String tableField; // 数据库保存字段

    private String fieldType; // 字段类型

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getTableField() {
        return tableField;
    }

    public void setTableField(String tableField) {
        this.tableField = tableField;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }
}
