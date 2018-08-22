package cn.cloudwalk.smartframework.core.service;

import cn.cloudwalk.smartframework.common.BaseComponent;
import cn.cloudwalk.smartframework.common.domain.support.SqlBuilder;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.mvc.dao.builder.NamedParamsBuilder;
import cn.cloudwalk.smartframework.common.util.ReflectUtil;
import cn.cloudwalk.smartframework.common.util.TextUtil;
import cn.cloudwalk.smartframework.config.SpringContextHolder;
import cn.cloudwalk.smartframework.core.result.impl.JQueryTableResultModel;
import org.apache.http.annotation.ThreadSafe;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 查询JQueryTable数据服务
 * <p>
 * 返回标准Table格式数据
 *
 * @author liyanhui@cloudwalk.cn
 * @date 18-8-22 上午11:09
 * @since 2.0.10
 */
@ThreadSafe
public class JQueryTableService extends BaseComponent {

    /**
     * 线程持有自己的实例
     */
    private static final ThreadLocal<JQueryTableService> LOCAL_INSTANCE = ThreadLocal.withInitial(JQueryTableService::new);

    public static JQueryTableService getInstance() {
        return LOCAL_INSTANCE.get();
    }

    /**
     * 查询
     *
     * @param sql     sql
     * @param builder 参数
     * @param model   页面数据封装
     * @return 带有数据封装的model
     */
    public JQueryTableResultModel query(JQueryTableResultModel model, String sql, NamedParamsBuilder builder) {
        try {
            Map<String, Object> params = builder.getAll();
            JQueryTableResultModel resultModel = prepareQuery(model, sql, params);
            long startIndex = (resultModel.getCurrentPage() - 1) * resultModel.getRowsOfPage();
            String dataSql = SqlBuilder.generateOrderBySql(sql, model.getOrderBy());
            dataSql = SqlBuilder.generatePageSql(dataSql, startIndex, resultModel.getRowsOfPage());
            logger.info(TextUtil.formatSql4DMLOrDQL(dataSql));
            List<Map<String, Object>> datas = getNamedParameterJdbcTemplate().queryForList(dataSql, params);
            if (datas != null && datas.size() > 50) {
                logger.warn("SQL : " + dataSql + "\nSQL params : " + params + "\nResult Size : " + datas.size());
            }
            resultModel.setDatas(datas);
            return resultModel;
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        } finally {
            LOCAL_INSTANCE.remove();
            builder.clear();
        }
    }

    /**
     * 查询 并将返回数据按照类型转换
     *
     * @param model   页面数据封装
     * @param sql     sql
     * @param builder 参数
     * @param type    返回类型
     * @param <V>     V
     * @return 带有数据封装的model
     */
    @SuppressWarnings("unchecked")
    public <V> JQueryTableResultModel query(JQueryTableResultModel model, String sql, NamedParamsBuilder builder, Class<V> type) {
        JQueryTableResultModel resultModel = query(model, sql, builder);
        List<Map<String, Object>> datas = resultModel.getDatas();
        List<V> list = new ArrayList<>();
        if (datas != null && datas.size() > 0) {
            try {
                for (Map<String, Object> data : datas) {
                    V instance = type.newInstance();
                    ReflectUtil.copyTableData2Bean(instance, data);
                    list.add(instance);
                }
            } catch (Exception exception) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc(exception));
            }
        }
        resultModel.setDatas((List<Map<String, Object>>) list);
        return resultModel;
    }

    private JQueryTableResultModel prepareQuery(JQueryTableResultModel model, String sql, Map<String, Object> params) {
        JQueryTableResultModel resultModel = new JQueryTableResultModel();
        resultModel.setCurrentPage(model.getCurrentPage());
        resultModel.setRowsOfPage(model.getRowsOfPage());
        String totalSql = SqlBuilder.generateCountSql(sql);
        logger.info(TextUtil.formatSql4DMLOrDQL(totalSql));
        Map<String, Object> result;
        result = getNamedParameterJdbcTemplate().queryForMap(totalSql, params);
        if (result != null) {
            Long total = (Long) result.get("t");
            if (total != null) {
                resultModel.setTotalRows(total);
            }
        }
        if (resultModel.getTotalRows() % resultModel.getRowsOfPage() == 0) {
            resultModel.setTotalPages(resultModel.getTotalRows() / resultModel.getRowsOfPage());
        } else {
            resultModel.setTotalPages(resultModel.getTotalRows() / resultModel.getRowsOfPage() + 1);
        }
        return resultModel;
    }

    private NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        Object bean = SpringContextHolder.getStaticApplicationContext().getBean("namedParameterJdbcTemplate");
        if (bean == null) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("error：spring.profiles.active is not standard"));
        }
        return (NamedParameterJdbcTemplate) bean;
    }
}
