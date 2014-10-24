
数据库连接池开发文档
====================

@[数据库连接池|开发文档]

### 介绍

数据库连接池作为anyLogicBus的公共设施，为服务提供便捷的数据库连接环境。

v1.0.6版本提供旧版本的连接池实现，软件包位于com.logicbus.datasource.

v1.2.5版本重写了连接池实现，软件包为位于com.logicbus.dbcp.

### 相关概念

#### DataSource
数据源(DataSource)是全局概念，代表了整个数据库连接的入口。数据源缓存了一个或多个数据库连接池的实例，可以在数据源中通过全局唯一的名称来查找所需要的ConnectionPool。

#### ConnectionPoolFactory
连接池工厂(ConnectionPoolFactory)用于向数据源提供ConnectionPool实例，定义了数据源获取ConnectionPool的方式，例如可以从XML配置中来创建ConnectionPool，也可以从第三方缓冲池中来获取ConnectionPool。

#### ConnectionPool
连接池(ConnectionPool)管理了一个到多个数据库连接，包括连接的创建，连接的保持(maxActive,maxIdle).

### 连接池配置
#### DataSource和DS配置文件
数据源通过全局的配置文件来配置。配置文件名取自环境变量，如下：
- dbcp.ds.master 配置文件主地址，缺省值为：
```
    dbcp.ds.master=java:///com/logicbus/dbcp/ds.xml#com.logicbus.dbcp.DataSource
```
- dbcp.ds.secondary 备用地址，缺省值为：
```
    dbcp.ds.secondary=java:///com/logicbus/dbcp/ds.xml#com.logicbus.dbcp.DataSource
```

数据源可以由多个ConnectionPoolFactory来提供连接池实例，这一点体现在配置文件中。
例如：
```
    <?xml version="1.0" encoding="UTF-8"?>
    <datasources>
	    <ds module="com.logicbus.dbcp.impl.Inner">
	    ...
	    </ds>
	    <ds module="com.logicbus.dbcp.impl.Inner">
	    ...
	    </ds>
    </datasources>
```
其中，一个/ds节点代表了一个ConnectionPoolFactory，module为ConnectionPoolFactory的实现类名。

目前已经实现的ConnectionPoolFactory包括：
- com.logicbus.dbcp.jndi.JNDIConnectionPoolFactory 基于JNDI的实现，例如第三方tomcat的数据库连接池；
- com.logicbus.dbcp.impl.Inner 基于DBCM(DataBase Connection Model)的实现，配置信息来自ds配置文件内部;
- com.logicbus.dbcp.impl.Provided 基于DBCM(DataBase Connection Model)的实现，配置信息来自Provider

#### Inner实现
ConnectionPoolFactory的Inner实现的配置信息来嵌入在ds配置文件的/ds节点中，例如：
```
	<ds module="com.logicbus.dbcp.impl.Inner">
		<dbcp
	    	name = "Default"
	    	driver = "org.mariadb.jdbc.Driver"
	    	url = "jdbc:mariadb://localhost:3306/harry"
	    	username = "root"
	    	password = "root"
	    	maxActive = "30"
	    	maxIdle = "5"
	    	maxWait = "10000"
		/>
		<dbcp
	    	name = "Default2"
	    	driver = "org.mariadb.jdbc.Driver"
	    	url = "jdbc:mariadb://localhost:3306/harry"
	    	username = "root"
	    	password = "root"
	    	maxActive = "10"
	    	maxIdle = "5"
		/>				
	</ds>
```

#### Provided实现
ConnectionPoolFactory的Provided实现的配置信息来自Provider，例如：
```
    <ds 
		module="com.logicbus.dbcp.impl.Provided" 
		provider="com.logicbus.dbcp.impl.Xml"
		xrc.master="java:///com/logicbus/dbcp/dbcp.xml#com.logicbus.dbcp.DataSource"
		xrc.secondary="java:///com/logicbus/dbcp/dbcp.xml#com.logicbus.dbcp.DataSource"
	/>
```

Provided通过provider属性所指定的Provider来提供信息。可以通过com.logicbus.dbcp.impl.DBCMProvider接口来定制自己的实现。

目前已完成的实现是com.logicbus.dbcp.impl.Xml，该实现从XML配置文档中读取配置信息。配置文件名从下列配置属性或环境变量读取：
- xrc.master 或 dbcp.xml.master 配置文件主地址，缺省值为：
```
    java:///com/logicbus/dbcp/dbcp.xml#com.logicbus.dbcp.DataSource
```
- xrc.secondary 或 dbcp.xml.secondary 备用地址，缺省值为：
```
    java:///com/logicbus/dbcp/dbcp.xml#com.logicbus.dbcp.DataSource
```

一个com.logicbus.dbcp.impl.Xml能识别的文件如下：
```
    <?xml version="1.0" encoding="utf-8"?>
    <ds>
        <dbcp
    	    name="Default3"
    	    driver="org.mariadb.jdbc.Driver"
    	    url="jdbc:mariadb://localhost:3306/harry"
    	    username="root"
    	    password="root"
    	    maxActive="10"
    	    maxIdle="5"
    	    maxWait="10000"
    	/>
    </ds>
```

#### DBCM
Inner和Provided的配置信息都是基于所谓的DBCM（DataBase Connection Model）。DBCM定义了一个连接池所必须的信息，包括：
- name 名称，使用者通过`名称`在DataSource中检索连接池
- driver JDBC驱动类
- url 数据库的URI，通常为http://<hot>:<port>/<server>
- username 数据库登录用户名
- password 数据库登录密码
- maxActive 最大的数据库连接数
- maxIdle 最大空闲的连接数
- maxWait 最大等待时间
- monitor 监控配置信息

### 使用数据库连接池

在anyLogicBus环境中，可以很方便的使用数据库连接池。通用的使用模式如下：
```java
    DataSource ds = DataSource.get();
    ConnectionPool pool = ds.getPool("<名称>");
    if (pool == null){
        //pool可能为空，注意做好异常处理
    }
    Connection conn = null;
    try {
        conn = pool.getConnection(5000);
        if (conn == null){
            //无法找到Connection
            //数据库无法连接，连接数耗尽或超出排队时间
        }
    }catch (Exception ex){
        //处理异常
    }finally{
        //连接池回收Connection
        //非常重要,否则连接无法释放，资源会耗尽
        pool.recycle(conn);
    }
```

### 数据库连接池的监控

anyLogicBus可以监控调用者获取数据库连接过程，监控数据包括：
- 获取连接的次数
- 获取连接耗费的总时长
- 获取连接耗费的最大时长
- 获取连接失败的次数
- 当前等待队列

anyLogicBus内置了服务来查询监控数据，URL如下：
```
http://<ip>:<port>/<context>/services/core/manager/DataSourceQuery?name=<数据源名称>
```

### 使用第三方的连接池

可以通过定制ConnectionPool和ConnectionPoolFactory来使用第三方连接池，例如可以通过JNDI来使用Tomcat的数据库连接池。

具体参见
- com.logicbus.dbcp.jndi.JNDIConnectionPool
- com.logicbus.dbcp.jndi.JNDIConnectionPoolFactory

