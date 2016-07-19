log
===
log用于输出日志，日志的输出信息包括：
- 信息,可通过模版输出
- 日志的级别，分为三个级别：error,warn,info；
- 活动，每一条日志属于某一个活动
- 进度，有的日志表达的是一种处理进度信息

### 实现模块

com.alogic.xscript.plugins.Log

### 配置参数

支持下列参数:

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | msg | 输出信息的模版，支持变量计算 |
| 2 | level | 日志级别，建议为:error,warn,info；缺省为info |
| 3 | progress | 处理进度，它是一个整型值（从-2至10001,-2代表非进度,-1代表还没开始,10001 代表已经完成,0-10000 代表以10000为基数的百分比），缺省为-2；｜
| 4 | activity | 活动id，可以指定活动id,缺省为xmltag |

### 案例

现有下列脚本：
```xml
	<script>
        <set id="name" value="alogic"/>
        <log msg="name is ${name}"/>
        <log msg="name is ${name}" level="error"/>		
	</script>
```
后端的日志输出为:
```
[160719112000,599] [INFO] [com.alogic.xscript.Logiclet] name is alogic
[160719112000,599] [ERROR] [com.alogic.xscript.Logiclet] name is alogic
```
