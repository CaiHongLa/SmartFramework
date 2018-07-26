package cn.cloudwalk.smartframework.core.domain;

import cn.cloudwalk.smartframework.common.domain.BaseDomain;
import cn.cloudwalk.smartframework.common.domain.support.DomainDataHandler;
import cn.cloudwalk.smartframework.common.domain.support.DomainMetadata;
import cn.cloudwalk.smartframework.common.domain.support.SqlBuilder;

/**
 * @author LIYANHUI
 */
public abstract class EmptyDomain extends BaseDomain {

    @Override
    public DomainDataHandler getDataHandler() {
        throw new UnsupportedOperationException("不能操作 EmptyDomain");
    }

    @Override
    public DomainMetadata getMetadata() {
        throw new UnsupportedOperationException("不能操作 EmptyDomain");
    }

    @Override
    public SqlBuilder getSqlBuilder() {
        throw new UnsupportedOperationException("不能操作 EmptyDomain");
    }
}
