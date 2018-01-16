Result Code
===========

### 服务成功代码

 - core.ok 本次服务调用成功

### alogic的错误(编码范围1000-2999)

#### 服务端的错误(1000-1999)
 - core.e1000 本项功能暂不支持
 - core.e1001 上下文环境错误
 - core.e1002 实例化类型错误
 - core.e1003 无法找到配置数据或配置数据错误
 - core.e1004 底层IO错误
 - core.e1005 指定的编码不支持
 - core.e1006 线程被中断
 - core.e1007 URL格式错误
 - core.e1008 本地文件系统文件不存在
 - core.e1009 服务暂不开放
 - core.e1010 未授权的访问
 - core.e1011 服务超时
 - core.e1012 未知错误
 - core.e1013 无法从连接池获取对象
 - core.e1014 无法找到服务配置
 - core.e1015 上传文件错误
 - core.e1016 文件已经存在
 - core.e1017 文件不能写入
 
 - core.e1100 队列已满(alogic-doer)
 - core.e1101 对象状态错误(alogic-doer)
 - core.e1200 当前应用的验证器无效(alogic-auth)
 - core.e1300 SQL语句错误(alogic-dbcp)
 - core.e1400 SSH验证失败(alogic-vfs)
 - core.e1401 SSH修改密码失败(alogic-vfs)
 - core.e1500 ZK服务器没有连接(alogic-zk)
 - core.e1501 ZK异常(alogic-zk)
 - core.e1600 无法找到可用的后端节点(alogic-rpc)
 - core.e1601 网路读写超时(alogic-rpc)
 - core.e1602 发起连接超时(alogic-rpc)
 - core.e1603 连接被对方拒绝(alogic-rpc)
 - core.e1604 对方无响应(alogic-rpc)
 - core.e1605 http状态码不为200(alogic-rpc)
 - core.e1606 服务返回码不为core.ok(alogic-rpc)
 - core.e1606 rpc调用错误(alogic-rpc)
 - core.e1700 未知的redis应答类型(alogic-kvalue)
 - core.e1701 对方关闭了连接(alogic-kvalue)
 - core.e1702 无有效的redis连接(alogic-kvalue)
 - core.e1703 redis服务器返回错误信息(alogic-kvalue)
 
#### 客户端的错误(2000-2999)

 - clnt.e2000 客户端在调用服务时，没有输入指定的参数
 - clnt.e2001 用户不存在或密码错误
 - clnt.e2002 客户端输入的验证码错误
 - clnt.e2003 客户端输入的验证码已过期
 - clnt.e2004 无法找到客户端的密钥信息
 - clnt.e2005 签名无法通过验证
 - clnt.e2006 时间戳已过期
 - clnt.e2007 无法找到客户端所需的数据
 - clnt.e2008 指定的id已被使用
 - clnt.e2009 输入参数格式错误


### 应用的错误(编码范围3000-6999)

由应用自定义.
