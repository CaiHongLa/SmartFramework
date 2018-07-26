package cn.cloudwalk.smartframework.common.domain.support;

import cn.cloudwalk.smartframework.common.domain.BaseDomain;

/**
 * @author LIYANHUI
 */
public interface SqlBuilderScanner {

    void onPropertyScan(String column, BaseDomain baseDomain, DomainMetadata metadata, DomainDataHandler domainDataHandler);
}
