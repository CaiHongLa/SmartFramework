package cn.cloudwalk.smartframework.common.domain;

import cn.cloudwalk.smartframework.common.domain.support.DomainDataHandler;
import cn.cloudwalk.smartframework.common.domain.support.DomainMetadata;
import cn.cloudwalk.smartframework.common.domain.support.SqlBuilder;
import cn.cloudwalk.smartframework.common.model.BaseDataModel;
import cn.cloudwalk.smartframework.common.protocol.ProtocolOut;

import java.beans.Transient;

/**
 * 基础实体
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public class BaseDomain extends BaseDataModel {

    /**
     * 将本实体转为另一个实体
     *
     * @param type 将要转换成的实体
     * @param <T>  BaseDomain
     * @return T
     */
    @Transient
    public <T extends BaseDomain> T toDomain(Class<T> type) {
        return this.createAndCopyProperties(type);
    }

    /**
     * 将本实体转为ProtocolOut对象
     *
     * @param type 将要转换成的ProtocolOut对象
     * @param <T>  ProtocolOut
     * @return T
     */
    @Transient
    public <T extends ProtocolOut> T toProtocolOut(Class<T> type) {
        return this.createAndCopyProperties(type);
    }

    /**
     * 获取实体的属性
     *
     * @return DomainMetadata
     */
    @Transient
    public DomainMetadata getMetadata() {
        return new DomainMetadata(this);
    }

    /**
     * 数据Handler
     *
     * @return DomainDataHandler
     */
    @Transient
    public DomainDataHandler getDataHandler() {
        return new DomainDataHandler(this);
    }

    /**
     * SqlBuilder
     *
     * @return SqlBuilder
     */
    @Transient
    public SqlBuilder getSqlBuilder() {
        return new SqlBuilder(this);
    }
}
