trim
====

trim用来设置变量，其取值是经过空格截断的。

### 实现模块

com.alogic.xscript.plugins.Trim

### 配置参数

支持下列参数:

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | id | 待设置的变量id |
| 2 | value | 该变量的值,变量值可计算 |

### 案例

现有下列脚本：
```xml
	<script>
        <set id="str" value="  asdasdasdDDasdasd13213"/>
        <log msg="str=${str}"/>

		<trim id="result" value="${str}"/>
        <log msg="result=${result}"/>

        <lowercase id="result" value="${str}"/>
        <log msg="result=${result}"/>

        <uppercase id="result" value="${str}"/>
        <log msg="result=${result}"/>

        <substr id="result" value="${str}" length="10"/>
        <log msg="result=${result}"/>

        <substr id="result" value="${str}" start="3" />
        <log msg="result=${result}"/>

        <substr id="result" value="${str}" start="3" length="10"/>
        <log msg="result=${result}"/>

        <substr id="result" value="${str}" start="3" length="1223"/>
        <log msg="result=${result}"/>

        <substr id="result" value="${str}" start="3" length="-1"/>
        <log msg="result=${result}"/>

        <substr id="result" value="${str}" start="-1" length="10"/>
        <log msg="result=${result}"/>
 
        <substr id="result" value="${str}" start="-1" length="123"/>
        <log msg="result=${result}"/>
   </script>
```

脚本的输出为:
```
	str=  asdasdasdDDasdasd13213
	result=asdasdasdDDasdasd13213
	result=  asdasdasdddasdasd13213
	result=  ASDASDASDDDASDASD13213
	result=  asdasdas
	result=sdasdasdDDasdasd13213
	result=sdasdasdDD
	result=sdasdasdDDasdasd13213
	result=sdasdasdDDasdasd13213
	result=  asdasdas
	result=  asdasdasdDDasdasd13213
```
