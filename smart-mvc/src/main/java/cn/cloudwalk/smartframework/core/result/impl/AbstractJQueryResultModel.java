package cn.cloudwalk.smartframework.core.result.impl;

import java.util.List;
import java.util.Map;

/**
 * JQuery 组件数据model
 *
 * @author liyanhui@cloudwalk.cn
 * @date 18-8-22 上午10:32
 * @since 2.0.10
 */
public abstract class AbstractJQueryResultModel extends AbstractJsonResultModel {

    final String SUCCESS_CODE = "200";

    final String SUCCESS_DESC = "请求完成";

    /**
     * 组件需要的最终数据
     */
    List<Map<String, Object>> datas;

    String respCode;

    String respDesc;

    public void setDatas(List<Map<String, Object>> datas) {
        this.datas = datas;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespDesc() {
        return respDesc;
    }

    public void setRespDesc(String respDesc) {
        this.respDesc = respDesc;
    }

    public List<Map<String, Object>> getDatas() {
        return datas;
    }


}
