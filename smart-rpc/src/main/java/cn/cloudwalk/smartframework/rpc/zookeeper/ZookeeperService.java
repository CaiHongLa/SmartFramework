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
        String CONFIG_FILE_NAME = "application.properties";
        if (!FileUtil.isFileExistOnClasspathOrConfigDir(CONFIG_FILE_NAME)) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("No " + CONFIG_FILE_NAME+ " file were found under classpath. "));
        } else {
            logger.info("Start loading zookeeper configuration");
            this.zookeeperConfig = PropertiesUtil.loadPropertiesOnClassPathOrConfigDir(CONFIG_FILE_NAME);
            String strategy = this.zookeeperConfig.getProperty("zookeeper.service.strategy");
            if (strategy == null) {
                strategy = "cn.cloudwalk.smartframework.rpc.service.discovery.adapter.DefaultServiceDiscoveryStrategy";
                logger.info("No zookeeper.service.strategy policy specified. It has been used automatically with " + strategy);
            }
            try {
                this.discoveryStrategy = (IServiceDiscoveryStrategy) Class.forName(strategy).newInstance();
                this.discoveryStrategy.setZookeeperService(this);
            } catch (IllegalAccessException | ClassNotFoundException | InstantiationException exception) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc(exception));
            }
            logger.info("Loading zookeeper configuration complete");
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
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("No root path or zookeeper system path is denied."));
        } else {
            try {
                this.client.delete().deletingChildrenIfNeeded().forPath(path);
                logger.debug("Node " + path + " deleted");
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
            logger.info("Disconnecting zookeeper cluster connection");

            try {
                this.zookeeperNodeCacheWatcher.getCache().close();
                this.client.close();
                this.isStopped = true;
                logger.info("Zookeeper cluster connection disconnected successfully");
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
        return this.zookeeperConfig.getProperty("system.localIp");
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
            if(zookeeperConfig.containsKey("tomcat.port")){
                logger.info("Find tomcat.port property in application.properties, use tomcat.port instead of tomcat port from server.xml");
                try {
                    this.localPort = Integer.parseInt(zookeeperConfig.getProperty("tomcat.port"));
                }catch (Exception e){
                    throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
                }
            } else {
                this.localPort = ServerUtil.getTomcatPortFromXmlConfig(this.getServletContext());
            }
            if (this.localPort == null) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc("Unable to get the web container port, so it cannot continue."));
            }

            logger.info("The port of web container has been determined " + this.localPort);
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
            logger.debug("Registering zookeeper service node ：path=" + path + ", data=" + distributedServiceProvider);
            ((BackgroundPathAndBytesable) ((ACLBackgroundPathAndBytesable) this.client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)).withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)).forPath(path, distributedServiceProvider.toBytes());
            logger.debug("Zookeeper service node registration completed ：path=" + path + ", data=" + distributedServiceProvider);
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
    }

    @Override
    public List<DistributedServiceProvider> getAvailableServiceList(String path, REMOTE_SERVICE_TYPE
            remoteServiceType) {
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
                            logger.error("Nodes are neither RPC node nor HTTP node.: " + data);
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
                logger.debug("NOTE ，" + path + " is only one instance of the available provider （" + provider + "），single point failure may occur.");
                return provider;
            } else {
                return this.discoveryStrategy.selectBestService(path, nodes);
            }
        } else {
            logger.error(path + " no available service provider, make sure that the service still has a running provider instance, or that the service address is correct");
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(path + " no available service provider, make sure that the service still has a running provider instance, or that the service address is correct"));
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
                        printInfo.append(" (Registered ").append(registerTime).append(")");
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
        logger.info("Get available IP from the system.localIp configuration item.");
        String ipPattern = this.zookeeperConfig.getProperty("system.localIp");
        Map<String, String> ipList = NetUtil.getAvailableIp();
        logger.info("The available IP list for the current host is：" + ipList);
        Map<String, String> result = NetUtil.getAvailableIp(ipPattern);
        if (result != null && result.size() != 0) {
            if (result.size() > 1) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc("Multiple available IPS ("+result+") were found, and therefore could not continue, possibly due to a system.localIP regular configuration error（" + ipPattern + "），resulting in more than one match in the available IP list, please check"));
            } else {
                Map.Entry<String, String> targetIp = result.entrySet().iterator().next();
                logger.info("IP has been determined to be " + targetIp.getKey() + "（" + targetIp.getValue() + "）");
                return targetIp.getKey();
            }
        } else {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("No matching to available IP, all available IP lists for the current host are " + (ipList != null && !ipList.isEmpty() ? ipList.keySet() : "{}") + "，" + "possible cause is system.localIp configuration error（current configuration is " + ipPattern + "）：1.IP configuration error is not included in the available IP list of the current host；2.IP regular configuration error, so there is no match to objects in all available IP lists."));
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
