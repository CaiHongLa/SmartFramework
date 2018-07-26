package cn.cloudwalk.smartframework.common.distributed;

import cn.cloudwalk.smartframework.common.IBaseComponent;
import cn.cloudwalk.smartframework.common.distributed.provider.DistributedServiceProvider;

import java.io.Closeable;
import java.util.List;
import java.util.Properties;

/**
 * Zookeeper服务
 * <p>
 * 包括连接Zookeeper、创建节点、删除节点、查找节点、获取服务等
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
public interface IZookeeperService extends IBaseComponent {

    //********************************config*****************************************

    /**
     * 获取分布式配置文件
     *
     * @return distributed-config.properties
     */
    Properties getZookeeperConfig();

    /**
     * 获取当前组件的运行模式
     *
     * @return DISTRIBUTED STANDALONE
     */
    RUNNING_MODE getRunningMode();

    //*********************************zookeeper*************************************

    /**
     * 连接Zookeeper服务
     *
     * @param url            zookeeper地址
     * @param sessionTimeout 会话超时事件
     * @param connectTimeout 连接超时事件
     * @param watcher        状态监听器
     */
    void connect(String url, String sessionTimeout, String connectTimeout, IZookeeperWatcher watcher);

    /**
     * 创建节点
     *
     * @param path       节点路径
     * @param persistent 是否永久节点
     */
    void createPath(String path, boolean persistent);

    /**
     * 是否存在节点
     *
     * @param path 节点路径
     * @return 存在：true 不存在：false
     */
    boolean existPath(String path);

    /**
     * 删除节点
     *
     * @param path 节点路径
     */
    void deletePath(String path);

    /**
     * 删除根节点，由配置中心调用，服务节点不能调用 否则将导致整个服务树删除
     */
    void cleanup();

    /**
     * 断开zookeeper连接
     */
    void disconnect();

    /**
     * 获取根节点路径
     *
     * @return 配置在distributed-config.properties文件中的zookeeper.rootPath
     */
    String getRootPath();

    /**
     * 获取分布式锁路径
     *
     * @return 根节点+“/lock”
     */
    String getLockPath();

    /**
     * 获取本机IP
     * 注册服务时使用
     *
     * @return 配置在distributed-config.properties文件中的zookeeper.localIp
     */
    String getLocalIp();

    /**
     * 获取本机所有的IP
     *
     * @return 各个网卡的IP
     */
    String getAvailableLocalIp();

    /**
     * 获取Web容器的启动端口
     *
     * @return HTTP服务端口
     */
    Integer getAvailableLocalPort();

    /**
     * 获取组件ID
     *
     * @return 配置在distributed-config.properties文件中的zookeeper.id
     */
    String getId();

    /**
     * 获取Zookeeper客户端实例
     *
     * @return CuratorFramework
     */
    Closeable getClient();

    //********************************service*****************************************

    /**
     * 注册服务
     *
     * @param path                       服务几点
     * @param distributedServiceProvider 节点数据
     */
    void registerService(String path, DistributedServiceProvider distributedServiceProvider);

    /**
     * 根据节点和服务类型获取服务列表
     *
     * @param path              节点路径
     * @param remoteServiceType 服务类型（HTTP/RPC）
     * @return 服务列表
     */
    List<DistributedServiceProvider> getAvailableServiceList(String path, REMOTE_SERVICE_TYPE remoteServiceType);

    /**
     * 获取可用服务数量
     *
     * @param path              节点路径
     * @param remoteServiceType 服务类型（HTTP/RPC）
     * @return 服务数量
     */
    int getAvailableServiceCount(String path, REMOTE_SERVICE_TYPE remoteServiceType);

    /**
     * 获取孩子节点的路径列表
     *
     * @param path 节点路径
     * @return 孩子节点的路径列表
     */
    List<String> getChildernPathList(String path);

    /**
     * 获取孩子节点的路径列表（排序）
     *
     * @param path  节点路径
     * @param order 是否排序
     * @return 孩子节点的路径列表
     */
    List<String> getChildernPathList(String path, boolean order);

    /**
     * 获取最佳服务
     *
     * @param path              节点路径
     * @param remoteServiceType 服务类型（HTTP/RPC）
     * @return 服务提供者
     */
    DistributedServiceProvider getBestServiceProvider(String path, REMOTE_SERVICE_TYPE remoteServiceType);

    /**
     * 获取服务树
     *
     * @return 服务树的字符串形式
     */
    String getServiceTreeInfoAsString();

    /**
     * 获取某个服务的服务树
     *
     * @param path Http服务节点
     * @return 服务树的字符串形式
     */
    String getTreeInfoAsString(String path);

    /**
     * 获取不可用服务列表
     * <p>
     * 可用服务定义：具备完整的服务路径
     * 不可用服务定义：节点路径不完整
     *
     * @return 不可用服务的节点列表
     */
    List<String> getInvalidService();

    /**
     * 获取Http服务根节点
     *
     * @return 配置在distributed-config.properties文件中的zookeeper.rootPath + “/http”
     */
    String getHttpServicePath();

    /**
     * 获取Rpc服务根节点
     *
     * @return 配置在distributed-config.properties文件中的zookeeper.rootPath + “/rpc”
     */
    String getRpcServicePath();

    /**
     * 运行模式
     */
    enum RUNNING_MODE {
        /**
         * 单机运行模式 此模式下不连接Zookeeper
         */
        STANDALONE,

        /**
         * 分布式部署模式，此模式下该节点为Zookeeper的某个服务节点
         */
        DISTRIBUTED
    }

    /**
     * 服务注册方式
     */
    enum REMOTE_SERVICE_TYPE {
        /**
         * 注解PublicHttpService的服务，以Http接口的形式提供服务
         */
        HTTP,

        /**
         * 注解PublicRpcService的服务，以Rpc接口的形式提供服务
         */
        RPC
    }

}
