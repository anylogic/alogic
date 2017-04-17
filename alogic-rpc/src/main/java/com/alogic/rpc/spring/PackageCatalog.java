package com.alogic.rpc.spring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.anysoft.util.DefaultProperties;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;
import com.anysoft.util.Settings;
import com.logicbus.models.catalog.CatalogNode;
import com.logicbus.models.catalog.Path;
import com.logicbus.models.servant.DefaultServiceDescription;
import com.logicbus.models.servant.ServantCatalog;
import com.logicbus.models.servant.ServiceDescription;
import com.logicbus.models.servant.ServiceDescriptionWatcher;
import com.logicbus.models.servant.impl.ServantCatalogNodeImpl;

public class PackageCatalog extends ServantCatalog.Abstract {
    static final String CLASS_PATTERN = "**/*.class";
    static final String CLASSPATH_ALL_URL_PREFIX = "classpath*:";
    protected static Logger logger = LoggerFactory.getLogger(PackageCatalog.class);

    private PathMatchingResourcePatternResolver classResolver = new PathMatchingResourcePatternResolver();
    private CatalogNode root;
    private String pattern;
    private String serializer;
    private String servant = SpringServant.class.getName();

    @Override
    public ServiceDescription findService(Path id) {
        String pkg = id.getPackage();
        String serviceId = id.getId();
        ServantCatalogNodeImpl node = (ServantCatalogNodeImpl) getChildByPath(this.root, new Path(pkg));
        if (node == null) {
            return null;
        }

        return node.findService(serviceId);
    }

    @Override
    public void addWatcher(ServiceDescriptionWatcher watcher) {

    }

    @Override
    public CatalogNode getRoot() {
        return this.root;
    }

    @Override
    public CatalogNode[] getChildren(CatalogNode parent) {
        return (CatalogNode[]) parent.getData();
    }

    @Override
    public CatalogNode getChildByPath(CatalogNode parent, Path path) {
        if (parent == null) {
            return null;
        }
        Path parentPath = parent.getPath();
        if (StringUtils.equals(parentPath.getPath(), path.getPath())) {
            return parent;
        }
        CatalogNode[] children = getChildren(parent);
        if (ArrayUtils.isEmpty(children)) {
            return null;
        }
        return getChildByPath(children[0], path);
    }

    @Override
    public void configure(Properties p) {
        String _pattern = p.GetValue("package", "");
        if (StringUtils.isEmpty(_pattern)) {
            return;
        }
        pattern = _pattern.replaceAll("\\.", "/");
        String path = p.GetValue("path", "");
        path = StringUtils.strip(path, "/");
        
        serializer = PropertiesConstants.getString(p,"rpc.serializer","");
        servant = PropertiesConstants.getString(p,"servant",servant);
        root = createCatalogNode(path, new Path(""));
    }

    private CatalogNode createCatalogNode(String path, Path parent) {
        int index = StringUtils.indexOf(path, "/");
        if (index < 0) {
            Path _path = parent.append(path);
            ServantCatalogNodeImpl node = new ServantCatalogNodeImpl(_path, null);
            List<ServiceDescription> list = scanServiceDescriptions(_path);
            if (list != null) {
                for (ServiceDescription sd : list) {
                    node.addService(sd.getServiceID(), sd);
                }
            }
            return node;
        }

        Path _path = parent.append(path.substring(0, index));
        path = path.substring(index + 1);
        CatalogNode child = createCatalogNode(path, _path);
        return new ServantCatalogNodeImpl(_path, new CatalogNode[]{child});
    }

    private List<ServiceDescription> scanServiceDescriptions(Path _path) {
        String locationPattern = CLASSPATH_ALL_URL_PREFIX + this.pattern + CLASS_PATTERN;
        try {
            Resource[] resources = classResolver.getResources(locationPattern);
            if (resources == null) {
                return null;
            }
            List<ServiceDescription> list = new ArrayList<ServiceDescription>();
            for (Resource resource : resources) {
                String name = resource.getURI().toString();
                String classPath = name.substring(name.indexOf(pattern));
                String className = StringUtils.substringBefore(classPath.replaceAll("/", "."), ".class");
                try {
                    Class<?> clazz = Settings.getClassLoader().loadClass(className);
                    Primary primary = clazz.getAnnotation(Primary.class);
                    if (primary != null) {
                        extractByPrimary(list, clazz, _path);
                    }
                    Service service = clazz.getAnnotation(Service.class);
                    if (service != null) {
                        extractByService(list, clazz, service, _path);
                    }
                } catch (ClassNotFoundException e) {
                    logger.error("Load class failed! Class: " + className, e);
                }
            }
            return list;
        } catch (IOException e) {
            logger.error("Scan servant failed! Package: " + locationPattern, e);
            return null;
        }
    }

    private void extractByService(List<ServiceDescription> list, Class<?> clazz, Service service, Path _path) {
        String value = service.value();
        if (StringUtils.isEmpty(value)) {
            value = StringUtils.uncapitalize(clazz.getSimpleName());
        }
        DefaultServiceDescription sd = getDefaultServiceDescription(_path, value);
        list.add(sd);
    }

    private DefaultServiceDescription getDefaultServiceDescription(Path _path, String value) {
        DefaultServiceDescription sd = new DefaultServiceDescription(value);
        sd.setModule(servant);
        sd.setPath(_path.append(value).getPath());
        if (this.serializer != null) {
            DefaultProperties props = new DefaultProperties();
            props.SetValue("rpc.serializer", this.serializer);
            sd.setProperties(props);
        }
        return sd;
    }

    private void extractByPrimary(List<ServiceDescription> list, Class<?> clazz, Path _path) {
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces == null) {
            return;
        }
        for (Class<?> cl : interfaces) {
            String canonicalName = cl.getCanonicalName();
            DefaultServiceDescription sd = getDefaultServiceDescription(_path, canonicalName);
            list.add(sd);
        }
    }

    @Override
    public void removeWatcher(ServiceDescriptionWatcher w) {
    }

}
