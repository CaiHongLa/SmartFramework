package cn.cloudwalk.smartframework.core.result.impl;

import com.github.pagehelper.Page;

/**
 * Mybatis PageHelper分页后的数据
 *
 * @author liyanhui@cloudwalk.cn
 * @date 18-8-30 下午5:09
 * @since 2.0.10
 */
public class MybatisTableResultModel<E> extends JQueryTableResultModel {

    public MybatisTableResultModel() {
        this.respCode = this.SUCCESS_CODE;
        this.respDesc = this.SUCCESS_DESC;
    }

    public MybatisTableResultModel(Page<E> page) {
        this.respCode = this.SUCCESS_CODE;
        this.respDesc = this.SUCCESS_DESC;
        this.datas = page.getResult();
        setCurrentPage(page.getPageNum());
        setTotalPages(page.getPages());
        setTotalRows(page.getTotal());
        setRowsOfPage(page.getPageSize());
    }

    public MybatisTableResultModel(Page<E> page, String message) {
        this.respCode = this.SUCCESS_CODE;
        this.respDesc = message;
        this.datas = page.getResult();
        setCurrentPage(page.getPageNum());
        setTotalPages(page.getPages());
        setTotalRows(page.getTotal());
        setRowsOfPage(page.getPageSize());
    }

    public MybatisTableResultModel(String resultCode, String message) {
        this.respCode = resultCode;
        this.respDesc = message;
    }

    public MybatisTableResultModel(Page<E> page, String resultCode, String message) {
        this.respCode = resultCode;
        this.respDesc = message;
        this.datas = page.getResult();
        setCurrentPage(page.getPageNum());
        setTotalPages(page.getPages());
        setTotalRows(page.getTotal());
        setRowsOfPage(page.getPageSize());
    }
}
