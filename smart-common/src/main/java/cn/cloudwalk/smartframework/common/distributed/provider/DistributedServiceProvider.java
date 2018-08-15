package cn.cloudwalk.smartframework.common.distributed.provider;

import cn.cloudwalk.smartframework.common.model.BaseDataModel;
import cn.cloudwalk.smartframework.common.util.JsonUtil;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 分布式服务提供者
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public abstract class DistributedServiceProvider extends BaseDataModel {

    /**
     * 服务IP
     */
    private String ip;

    /**
     * 服务端口
     */
    private Integer port;

    /**
     * 服务注册事件
     */
    private Date registerTime;

    /**
     * 服务组件ID
     */
    private String id;

    /**
     * 服务实例ID
     */
    private Integer instanceId;


    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return this.port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Date getRegisterTime() {
        return this.registerTime;
    }

    public void setRegisterTime(Date registTime) {
        this.registerTime = registTime;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getInstanceId() {
        return this.instanceId;
    }

    public void setInstanceId(Integer instanceId) {
        this.instanceId = instanceId;
    }

    public byte[] toBytes() {
        return JsonUtil.object2Json(this).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        return "DistributedServiceProvider{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", registerTime=" + registerTime +
                ", id='" + id + '\'' +
                ", instanceId=" + instanceId +
                "} ";
    }
}
