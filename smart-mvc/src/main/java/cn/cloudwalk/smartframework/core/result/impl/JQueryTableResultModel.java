package cn.cloudwalk.smartframework.core.result.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JQuery Table组件数据model
 *
 * @author liyanhui@cloudwalk.cn
 * @date 18-8-22 上午10:24
 * @since 2.0.10
 */
public class JQueryTableResultModel extends AbstractJQueryResultModel {

    /**
     * 每页多少行
     */
    private long rowsOfPage;

    /**
     * 当前页 从1开始
     */
    private long currentPage;

    /**
     * 总页数
     */
    private long totalPages;

    /**
     * 总行数
     */
    private long totalRows;

    /**
     * 页面字段排序
     */
    private String orderBy;

    public JQueryTableResultModel() {
        this.respCode = this.SUCCESS_CODE;
        this.respDesc = this.SUCCESS_DESC;
    }

    public JQueryTableResultModel(List<Map<String, Object>> datas) {
        this.respCode = this.SUCCESS_CODE;
        this.respDesc = this.SUCCESS_DESC;
        this.datas = datas;
    }

    public JQueryTableResultModel(List<Map<String, Object>> datas, String message) {
        this.respCode = this.SUCCESS_CODE;
        this.respDesc = message;
        this.datas = datas;
    }

    public JQueryTableResultModel(String resultCode, String message) {
        this.respCode = resultCode;
        this.respDesc = message;
    }

    public JQueryTableResultModel(List<Map<String, Object>> datas, String resultCode, String message) {
        this.respCode = resultCode;
        this.respDesc = message;
        this.datas = datas;
    }

    public long getRowsOfPage() {
        return rowsOfPage;
    }

    public void setRowsOfPage(long rowsOfPage) {
        this.rowsOfPage = rowsOfPage;
    }

    public long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(long currentPage) {
        this.currentPage = currentPage;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(long totalRows) {
        this.totalRows = totalRows;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    @Override
    protected Object getObject() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("respCode", this.respCode);
        map.put("respDesc", this.respDesc);
        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("rowsOfPage", rowsOfPage);
        dataMap.put("currentPage", currentPage);
        dataMap.put("totalPages", currentPage);
        dataMap.put("totalRows", totalRows);
        dataMap.put("datas", datas);
        map.put("data", dataMap);
        return map;
    }

}
