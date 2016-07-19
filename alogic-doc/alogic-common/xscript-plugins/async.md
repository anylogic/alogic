async
=====
async用于控制子语句异步执行。

### 实现模块

com.alogic.xscript.plugins.Asynchronized

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | timeout | 超时时间，缺省为1s |


### 案例

现有脚本:

```
	<script>
		<async timeout="10000">
		    <!--子语句1，执行5s-->
			<segment>
				<log msg="child 1 start."/>
				<sleep timeout="5000"/>
				<log msg="child 1 end."/>
			</segment>
			<!--子语句2，执行3s-->
			<segment>
				<log msg="child 2 start."/>
				<sleep timeout="3000"/>
				<log msg="child 2 end."/>
			</segment>			
		</async>
	</script>
```

上述脚本输出为：
```
[160719141332,732] [INFO] [com.alogic.xscript.Logiclet] child 1 start.
[160719141332,732] [INFO] [com.alogic.xscript.Logiclet] child 2 start.
[160719141335,734] [INFO] [com.alogic.xscript.Logiclet] child 2 end.
[160719141337,733] [INFO] [com.alogic.xscript.Logiclet] child 1 end.
```
