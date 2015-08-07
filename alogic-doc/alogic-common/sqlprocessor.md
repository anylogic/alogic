## SQL预处理器

SQL预处理器用于生成动态SQL语句，处理Mysql下的变量绑定问题，类似于MyBatis的[Dynamic SQL](http://mybatis.github.io/mybatis-3/dynamic-sql.html)。

和MyBatis的Dynamic SQL的不同之处在于，SQL预处理器没有采用XML语法，而是采用了更轻量的内嵌脚本语法。

### 更新历史
- alogic-1.6.3.37 [20150807 duanyy] 新增本文档。

### 问题出在哪儿？

假设有一张用户表(User),包含下列信息：用户id(user_id),姓名(name),手机(mobile),地址(address)。

现需要编写更新用户信息的服务，那我们可以分解成6条需求：

* 修改姓名
```
	update user set name=? where user_id=?
```
* 修改手机
```
	update user set mobile=? where user_id=?
```
* 修改地址
```
	update user set address=? where user_id=?
```
* 修改姓名和手机
```
	update user set name=?,mobile=? where user_id=?
```
* 修改姓名和地址
```
	update user set name=?,address=? where user_id=?
```
* 修改手机和地址
```
	update user set mobile=?,address=? where user_id=?
```
* 修改姓名、手机和地址
```
	update user set name=?,mobile=?,address=? where user_id=?
```

我们一定要提供6个服务，6个SQL语句么？

聪明人当然想到采用SQL拼装来完成，例如：
```
	String name = getArgument("name","",ctx);
	String mobile = getArgument("mobile","",ctx);
	String address = getArgument("address","",ctx);
	
	String sql = "update user set user_id = ? ";
	if (name != null && name.length() >0){
		sql += ",name=" + name;
	}
	if (mobile != null && mobile.length() >0){
		sql += ",mobile=" + mobile;
	}
	if (address != null && address.length() >0){
		sql += ",address=" + address;
	}	
	
	sql += " where user_id = ?";	
```
资深人员立刻会发现，上面的做法会导致SQL注入的安全隐患。

SQL预处理器就是用于解决上述问题。

### 动态SQL

我们先看看基于SQL预处理器的解决方案：

```
	update 
		user
	set
		user_id = #{bind('id')}	
		#{not_nvl(name ,',name='+bind('name'))}
		#{not_nvl(mobile ,',mobile='+bind('mobile'))}
		#{not_nvl(address ,',address='+bind('address'))}
	where 
		user_id = #{bind('id')}	
		
```

先来分析一下上述语法，上面代码长得很像标准SQL语句，其中的区别在于嵌入了#{}脚本。没错，#{}标记内的内容就是处理脚本。

这里的脚本其实是公式解析器(alogic-common中的com.anysoft.formula)所支持的公式，公式所计算出来的值将填入SQL相应的位置。

在上面例子中的脚本中，关键的语法为：

* **bind('id')**

绑定指定id的参数值，输出MYSQL的绑定语法?，并将该值放进SQL语句绑定对象列表。

* **not_nvl(test,do)**

如果test表达式的内容为空，则输出do表达式的内容。

如果我们回顾前面的需求：修改姓名和地址。那么上述SQL语句预处理之后的结果为：

```
	update user
	set
		user_id = ?
		,name=?
		,address=?
	where 
		user_id = ?
```

并输出绑定对象列表，绑定对象列表的内容为：
```
	[0]=user_id取值
	[1]=name的取值
	[2]=address的取值
	[3]=user_id取值
```

### 如何开发

了解上面的原理之后，我们看看如何开发。

首先，使用预编译器编译SQL语句：
```
	import com.logicbus.dbcp.processor.Preprocessor;
	
	Preprocessor processor = new Preprocessor(sql);
```
或者
```
	Preprocessor processor = new Preprocessor();
	processor.compile(sql);
```

接着，准备好变量集和绑定对象列表：
```
	Properties p;
	List<Object> data = new ArrayList<Object>();
```
上面的变量集可以自行创建，也可以直接应用前端传入，例如alogic服务框架中的Context可以直接使用。变量集中包含了SQL语句中所需要的变量，例如前面例子中的name,mobile,address等

上面的绑定对象列表是一个空的List，以便存储预处理之后的MYSQL绑定变量。

最后，进行预处理：
```
	String sql = processor.process(p, data);
```
上面得到的SQL就是预处理的结果SQL语句，同时绑定对象会存入data中。有了SQL语句和data,就可以通过jdbc调用SQL语句。
例如：
```
	import com.logicbus.dbcp.sql.DBTools;
	
	DBTools.update(conn,sql,data);
```

> 
> 上述过程中，可以一次编译(compile),多次处理(process).
> 
