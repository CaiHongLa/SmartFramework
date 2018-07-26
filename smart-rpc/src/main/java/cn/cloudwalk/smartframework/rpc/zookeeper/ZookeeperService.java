package cn.cloudwalk.smartframework.rpc.zookeeper;

import cn.cloudwalk.smartframework.common.BaseComponent;
import cn.cloudwalk.smartframework.common.distributed.IServiceDiscoveryStrategy;
import cn.cloudwalk.smartframework.common.distributed.IZookeeperNodeCacheWatcher;
import cn.cloudwalk.smartframework.common.distributed.IZookeeperService;
import cn.cloudwalk.smartframework.common.distributed.IZookeeperWatcher;
import cn.cloudwalk.smartframework.common.distributed.provider.DistributedServiceProvider;
import cn.cloudwalk.smartframework.common.distributed.provider.HttpServiceProvider;
import cn.cloudwalk.smartframework.common.distributed.provider.RpcServiceProvider;
import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.util.*;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLBackgroundPathAndBytesable;
import org.apache.curator.framework.api.BackgroundPathAndBytesable;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Closeable;
import java.util.*;

/**
 * ZookeeperService
 *
 * @author LIYANHUI
 * @since 1.0.0
 */
@Component("zookeeperService")
public class ZookeeperService extends BaseComponent implements IZookeeperService {
    private static final Logger logger = LogManager.getLogger(ZookeeperService.class);

    private Properties zookeeperConfig;
    private boolean isStopped;
    private String localIp;
    private Integer localPort;
    private IServiceDiscoveryStrategy discoveryStrategy;
    private CuratorFramework client;
    private TreeCache treeCache;

    @Autowired
    @Qualifier("zookeeperNodeCacheWatcher")
    private IZookeeperNodeCacheWatcher zookeeperNodeCacheWatcher;


    public ZookeeperService() {
        this.isStopped = false;
    }

    @PostConstruct
    private void loadConfig() {
        String CONFIG_FILE_NAME = "distributed-config.properties";
        if (!FileUtil.isFileExistOnClasspathOrConfigDir(CONFIG_FILE_NAME)) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("没有在 classpath 下找到配置文件 " + CONFIG_FILE_NAME));
        } else {
            this.zookeeperConfig = PropertiesUtil.loadPropertiesOnClassPathOrConfigDir(CONFIG_FILE_NAME);
            logger.info("读取配置文件 " + CONFIG_FILE_NAME + " 完成：" + this.zookeeperConfig);
            String strategy = this.zookeeperConfig.getProperty("zookeeper.service.strategy");
            if (strategy == null) {
                strategy = "cn.cloudwalk.smartframework.rpc.service.discovery.adapter.DefaultServiceDiscoveryStrategy";
                logger.info("没有指定 zookeeper.service.strategy 策略，已自动使用 " + strategy);
            }
            try {
                this.discoveryStrategy = (IServiceDiscoveryStrategy) Class.forName(strategy).newInstance();
                this.discoveryStrategy.setZookeeperService(this);
            } catch (IllegalAccessException | ClassNotFoundException | InstantiationException exception) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc(exception));
            }
        }
    }

    @Override
    public Properties getZookeeperConfig() {
        return zookeeperConfig;
    }

    @Override
    public RUNNING_MODE getRunningMode() {
        return RUNNING_MODE.valueOf(zookeeperConfig.getProperty("zookeeper.mode", "distributed").toUpperCase());
    }

    @Override
    public void connect(String url, String sessionTimeout, String connectTimeout, IZookeeperWatcher watcher) {
        this.client = CuratorFrameworkFactory.newClient(
                url,
                Integer.parseInt(sessionTimeout),
                Integer.parseInt(connectTimeout),
                new ExponentialBackoffRetry(1000, 3));

        this.client.getConnectionStateListenable().addListener(
                (client, newState) -> watcher.process(
                        IZookeeperWatcher.ConnectionState.valueOf(newState + ""))
        );

        this.client.start();
        zookeeperNodeCacheWatcher.watch(client, getRootPath());
    }

    @Override
    public void createPath(String path, boolean persistent) {
        try {
            ((BackgroundPathAndBytesable) ((ACLBackgroundPathAndBytesable)
                    this.client.create().
                            creatingParentsIfNeeded().
                            withMode(persistent ? CreateMode.PERSISTENT : CreateMode.EPHEMERAL)).
                    withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)).
                    forPath(path);
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
    }

    @Override
    public boolean existPath(String path) {
        try {
            return this.client.checkExists().forPath(path) != null;
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
    }

    @Override
    public void deletePath(String path) {
        if (path != null && path.matches("/|/zookeeper*")) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("拒绝删除根路径或 zookeeper 系统路径"));
        } else {
            try {
                this.client.delete().deletingChildrenIfNeeded().forPath(path);
                logger.debug("节点 " + path + " 删除完成");
            } catch (Exception e) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
            }
        }
    }

    @Override
    public void cleanup() {
        this.deletePath(this.getHttpServicePath());
        this.deletePath(this.getRpcServicePath());
    }

    @Override
    public void disconnect() {
        if (this.client != null && !this.isStopped) {
            logger.info("正在断开 zookeeper 集群连接");

            try {
                this.zookeeperNodeCacheWatcher.getCache().close();
                this.client.close();
                this.isStopped = true;
                logger.info("zookeeper 集群连接断开成功");
            } catch (Exception e) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
            }
        }
    }

    @Override
    public String getRootPath() {
        return this.zookeeperConfig.getProperty("zookeeper.rootPath");
    }

    @Override
    public String getLockPath() {
        return "/lock";
    }

    @Override
    public String getLocalIp() {
        return this.zookeeperConfig.getProperty("zookeeper.localIp");
    }

    @Override
    public String getAvailableLocalIp() {
        if (this.localIp == null) {
            this.localIp = this.getAvailableLocalIpByConfig();
        }

        return this.localIp;
    }

    @Override
    public Integer getAvailableLocalPort() {
        if (this.localPort == null) {
            this.localPort = ServerUtil.getTomcatPortFromXmlConfig(this.getServletContext());
            if (this.localPort == null) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc("没有获取到 web 容器端口，因此无法继续"));
            }

            logger.info("已确定 web 容器端口为 " + this.localPort);
        }

        return this.localPort;
    }

    @Override
    public String getId() {
        return this.zookeeperConfig.getProperty("zookeeper.id");
    }

    @Override
    public Closeable getClient() {
        return client;
    }

    @Override
    public void registerService(String path, DistributedServiceProvider distributedServiceProvider) {
        try {
            logger.debug("正在注册 zookeeper 服务节点：path=" + path + ", data=" + distributedServiceProvider);
            ((BackgroundPathAndBytesable) ((ACLBackgroundPathAndBytesable) this.client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)).withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)).forPath(path, distributedServiceProvider.toBytes());
            logger.debug("zookeeper 服务节点注册完成：path=" + path + ", data=" + distributedServiceProvider);
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
    }

    @Override
    public List<DistributedServiceProvider> getAvailableServiceList(String path, REMOTE_SERVICE_TYPE remoteServiceType) {
        if (this.treeCache == null) {
            this.treeCache = (TreeCache) this.zookeeperNodeCacheWatcher.getCache();
        }

        List<DistributedServiceProvider> providers = new ArrayList<>();

        try {
            Map<String, ChildData> children = treeCache.getCurrentChildren(path);
            if (children != null) {

                for (Map.Entry<String, ChildData> item : children.entrySet()) {
                    ChildData data = item.getValue();
                    if (data != null && data.getData() != null && data.getData().length > 0) {
                        String dataPath = data.getPath();
                        if (dataPath.startsWith(getRpcServicePath())) {
                            providers.add(JsonUtil.json2Object(new String(data.getData(), "UTF-8"), RpcServiceProvider.class));
                        } else if (dataPath.startsWith(getHttpServicePath())) {
                            providers.add(JsonUtil.json2Object(new String(data.getData(), "UTF-8"), HttpServiceProvider.class));
                        } else {
                            logger.error("节点既不是RPC节点也不是HTTP节点: " + data);
                        }
                    }
                }
            }

            return providers;
        } catch (Exception exception) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(exception));
        }
    }

    @Override
    public int getAvailableServiceCount(String path, REMOTE_SERVICE_TYPE remoteServiceType) {
        return this.getAvailableServiceList(path, remoteServiceType).size();
    }

    @Override
    public List<String> getChildernPathList(String path) {
        return getChildernPathList(path, false);
    }

    @Override
    public List<String> getChildernPathList(String path, boolean order) {
        List<String> paths = new LinkedList<>();
        this.getSubPathListBase(path, paths);
        paths.add(path);
        if (order) {
            paths.sort((o1, o2) -> o2.length() - o1.length());
        }
        return paths;
    }

    @Override
    public DistributedServiceProvider getBestServiceProvider(String path, REMOTE_SERVICE_TYPE remoteServiceType) {
        List<DistributedServiceProvider> nodes = this.getAvailableServiceList(path, remoteServiceType);
        if (nodes != null && nodes.size() != 0) {
            if (nodes.size() == 1) {
                DistributedServiceProvider provider = nodes.get(0);
                logger.debug("请注意，" + path + " 服务的可用提供者仅一个实例（" + provider + "），可能发生单点故障");
                return provider;
            } else {
                return this.discoveryStrategy.selectBestService(path, nodes);
            }
        } else {
            logger.error(path + " 无可用服务提供者，请确定该服务是否还有在运行的提供者实例，或者该服务地址是否正确");
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(path + " 无可用服务提供者，请确定该服务是否还有在运行的提供者实例，或者该服务地址是否正确"));
        }
    }

    @Override
    public String getServiceTreeInfoAsString() {
        return this.getTreeInfoAsString(this.getHttpServicePath());
    }

    @Override
    public String getTreeInfoAsString(String path) {
        StringBuilder printInfo = new StringBuilder();
        printInfo.append("├ ").append(path).append("\n");
        this.getTreeInfoBase(path, printInfo);
        return printInfo.toString();
    }

    @Override
    public List<String> getInvalidService() {
        List<String> list = new ArrayList<>();
        this.getInvalidServiceBase(this.getRootPath(), list);
        return list;
    }

    @Override
    public String getHttpServicePath() {
        return this.getRootPath() + "/http";
    }

    @Override
    public String getRpcServicePath() {
        return this.getRootPath() + "/rpc";
    }

    //*************************************private********************************************

    private void getSubPathListBase(String path, List<String> paths) {
        List<String> children;
        try {
            children = this.client.getChildren().forPath(path);
        } catch (Exception var7) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(var7));
        }

        for (String child : children) {
            String nextPath = (path + "/" + child).replaceFirst("//", "/");
            paths.add(nextPath);
            this.getSubPathListBase(nextPath, paths);
        }
    }

    private void getTreeInfoBase(String path, StringBuilder printInfo) {

        if (this.treeCache == null) {
            this.treeCache = (TreeCache) this.zookeeperNodeCacheWatcher.getCache();
        }

        Map<String, ChildData> nodes = treeCache.getCurrentChildren(path);
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        String currNodePath;
        for (Iterator<Map.Entry<String, ChildData>> entryIterator = nodes.entrySet().iterator(); entryIterator.hasNext(); this.getTreeInfoBase(currNodePath, printInfo)) {
            Map.Entry<String, ChildData> node = entryIterator.next();
            String currPath = node.getKey();
            currNodePath = (path + "/" + currPath).replaceFirst("//", "/");

            for (int j = 0; j < currNodePath.split("/").length - 1; ++j) {
                printInfo.append("    ");
            }

            try {
                if (currPath.startsWith("provider-")) {
                    printInfo.append("├ ").append(currPath);
                    byte[] data = treeCache.getCurrentData(currNodePath).getData();
                    if (data != null && data.length > 0) {
                        HttpServiceProvider info = JsonUtil.json2Object(new String(data, "UTF-8"), HttpServiceProvider.class);
                        String registerTime = DateUtil.formatDate(info.getRegisterTime(), DateUtil.DATE_PATTERN.yyyy_MM_dd_HH_mm_ss);
                        printInfo.append(" (注册于 ").append(registerTime).append(")");
                    }
                    printInfo.append("\n");
                } else {
                    printInfo.append("├ ").append(currPath).append("\n");
                }
            } catch (Exception e) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
            }
        }
    }

    private String getAvailableLocalIpByConfig() {
        logger.info("开始根据 zookeeper.localIp 配置项获取可用 ip");
        String ipPattern = this.zookeeperConfig.getProperty("zookeeper.localIp");
        Map<String, String> ipList = NetUtil.getAvailableIp();
        logger.info("当前主机的可用 ip 列表为：" + ipList);
        Map<String, String> result = NetUtil.getAvailableIp(ipPattern);
        if (result != null && result.size() != 0) {
            if (result.size() > 1) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc("找到多个可用 ip（" + result + "），因此无法继续，可能原因为 zookeeper.localIp 正则配置错误（" + ipPattern + "），导致在可用 ip 列表中匹配到了多个，请检查"));
            } else {
                Map.Entry<String, String> targetIp = result.entrySet().iterator().next();
                logger.info("已确定可用 ip 为 " + targetIp.getKey() + "（" + targetIp.getValue() + "）");
                return targetIp.getKey();
            }
        } else {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("没有匹配到可用 ip，当前主机的所有可用 ip 列表为 " + (ipList != null && !ipList.isEmpty() ? ipList.keySet() : "{}") + "，" + "可能原因为 zookeeper.localIp 配置错误（当前配置为 " + ipPattern + "）：" + "1.ip 配置错误，没有包含在当前主机的可用 ip 列表中；" + "2.ip 正则配置错误，因此在所有可用 ip 列表中没有匹配到对象"));
        }
    }

    private void getInvalidServiceBase(String path, List<String> list) {
        try {
            List<String> children = this.client.getChildren().forPath(path);
            if (children != null && children.size() > 0) {

                for (String node : children) {
                    String currNodePath = (path + "/" + node).replaceFirst("//", "/");
                    this.getInvalidServiceBase(currNodePath, list);
                }
            } else if (!path.contains("provider-")
                    && !path.equals(getRootPath())) {
                list.add(path);
            }
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
    }

}
