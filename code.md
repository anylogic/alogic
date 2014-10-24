code
====

### 服务返回代码

#### 服务成功代码
 - core.ok 服务调用成功

#### 框架的错误
 - core.service_paused 服务处于暂停状态
 - core.time_out 服务调用超时
 - core.fatalerror 服务异常，看具体信息
 - core.service_not_found 没有找到服务定义
 - core.error_module 无法实例化服务实现模块，通常是因为类名配置错误，或者无法找到类的实现
 - core.service_busy 预设线程数不够，服务队列中所有实例都忙
 - core.error_remote_module 远程的库文件无法加载，或者URL配置错误
 - core.instance_create_error 无法创建类的实例

#### 客户端的错误
 - client.permission_denied 无权访问本服务
 - client.args_not_found 缺省服务参数
 - user.data_not_found 没有找到相应的数据
 
#### 客户端API的错误
 - client.no_remote_home 没有设置远程服务根路径
 - client.error_url 客户端生成的URL格式错误
 - client.invoke_error 服务调用错误