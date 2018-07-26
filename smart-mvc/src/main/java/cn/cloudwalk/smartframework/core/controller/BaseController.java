package cn.cloudwalk.smartframework.core.controller;

import cn.cloudwalk.smartframework.common.mvc.MvcComponent;
import cn.cloudwalk.smartframework.core.controller.builder.ProtocolBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author LIYANHUI
 */
public class BaseController extends MvcComponent {

    @Autowired
    @Qualifier("protocolBuilder")
    protected ProtocolBuilder protocolBuilder;

}
