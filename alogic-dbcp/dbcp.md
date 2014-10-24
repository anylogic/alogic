
数据库连接池开发文档
====================

@[数据库连接池|开发文档]

### 介绍

数据库连接池作为anyLogicBus的公共设施，为服务提供便捷的数据库连接环境，位于com.logicbus.datasource，自1.0.6之后开始提供。

### 配置

数据源是数据库连接池的基本配置单位，一个数据源需要配置的信息包括：
- name 数据源名称，使用者通过`数据源名称`检索数据源
- driver JDBC驱动类
- url 数据库的URI，通常为http://<hot>:<port>/<server>
- username 数据库登录用户名
- password 数据库登录密码
- maxActive 最大的数据库连接数
- maxIdle 最大空闲的连接数，目前实现为初始连接数
- monitor 监控配置信息

数据连接池可以配置一个或多个数据源，通常配置在一个XML文件中，例如：
```xml
    <?xml version = "1.0" encoding = "utf-8"?>
    <dbcp>
        <connection
	        name="Default"
	        driver="org.mariadb.jdbc.Driver"
	        url="jdbc:mariadb://localhost:3306/harry"
	        username="root"
	        password="root"
	        maxActive="10"
	        maxIdle="5"
	        monitor="step=60;times_rras=SUM:60:720,SUM:3600:720;duration_rras=SUM:60:720,SUM:3600:720;error_rras=SUM:60:720,SUM:3600:720;maxduration_rras=MAX:60:720,SUM:3600:720;queue_rras=MAX:60:720,SUM:3600:720"  
	    />
    </dbcp>
```
上面是内置的一个配置文件，地址为:
```
    java:///com/logicbus/datasource/dbcp.xml#com.logicbu.datasource.XmlResourceConnectionPool
```

数据库连接池采用插件设计，首先在环境变量中搜索变量dbcp.module,该变量值为数据库连接池的实现类名，缺省值为com.logicbu.datasource.XmlResourceConnectionPool。

XmlResourceConnectionPool会读取上述的配置文件，配置文件的地址通过变量dbcp.master和dbcp.secondary来指定。

### 使用数据库连接池

在anyLogicBus环境中，可以很方便的使用数据库连接池。通用的使用模式如下：
```java
    ConnectionPool pool = ConnectionPoolFactory.getPool();
    Connection conn = null;
    try {
        conn = pool.getConnection("<名称>",5000);
        if (conn == null){
            //无法找到Connection
            //数据库无法连接，连接数耗尽或超出排队时间
        }
    }catch (Exception ex){
        //处理异常
    }finally{
        //非常重要,否则连接无法释放，资源会耗尽
        SQLTools.close(conn);
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

缺省实现XmlResourceConnectionPool是anyLogicBus自己实现的连接池。实际上，还可以封装第三方的连接池。

例如TomcatConnectionPool实现了基于Tomcat的连接池，代码如下：
```java
    /**
    * Tomcat的数据库连接池
    * 
    * @author duanyy
    * @since 1.0.6
    */
    public class TomcatConnectionPool implements ConnectionPool {

	    public TomcatConnectionPool(Properties props) {
	    }

	    @Override
	    public Connection getConnection(String name,long timeout) {
    		Context jndiCntx = null;
	    	try {
		    	jndiCntx = (Context) (new InitialContext()).lookup("java:comp/env");
    			DataSource ds = (DataSource) jndiCntx.lookup(name);
			    return ds.getConnection();
		    } catch (NamingException e) {
    			e.printStackTrace();
	    	} catch (Throwable t) {
		    	t.printStackTrace();
		    }
		    return null;
	    }

	    @Override
	    public void recycle(Connection conn) {
    		try {
	    		conn.close();
		    } catch (SQLException e) {
			    e.printStackTrace();
		    }
	    }

	    @Override
	    public NamedDataSource getDataSource(String name) {
    		return null;
	    }
    }
```

如要使用tomcat的连接库，你只需设置dbcp.module=com.logicbus.datasource.TomcatConnectionPool,已经基于连接池的代码无需修改。
