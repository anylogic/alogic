uuid
====
uuid用于设置一个随机字符串。

### 实现模块

com.alogic.xscript.plugins.UUid

### 配置参数

支持下列参数:

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | id | 待设置的变量id |
| 2 | length | 字符串长度，缺省情况下为32位uuid,格式为:########-####-####-####-######## |
| 3 | redix | 基数,定义了字符串组成，具体见案例 | 

### 案例

现有下列脚本：

```xml
	<script>
		<!-- 32为uuid，格式为:########-####-####-####-######## -->
        <uuid id="id1"/>
        <log msg="id1=${id1}"/>
        
        <!--length为负数，效果同id1-->
        <uuid id="id2" length="-1"/>
        <log msg="id2=${id2}"/>
        
        <!--20位字符串，字符串包含数字，大写字母，小写字母-->
        <uuid id="id3" length="20"/>
        <log msg="id3=${id3}"/>
        
        <!-- 15位字符串，字符串包含数字 -->
        <uuid id="id4" length="15" redix="10"/>
        <log msg="id4=${id4}"/>
        
        <!-- 16位字符串，字符串包含数字，大写字母 -->
        <uuid id="id5" length="16" redix="36"/>
        <log msg="id5=${id5}"/>
        
        <!-- 16位字符串，字符串包含数字，大写字母，小写字母 -->
        <uuid id="id6" length="10" redix="62"/>
        <log msg="id6=${id6}"/>
	</script>	
```

脚本的输出如下：
```
id1=A9A544EC-3597-4CBA-9A18-D41B58E42AA1
id2=F7F69D64-B4C1-4FBE-B7BB-B7AC4D51A30F
id3=vreDjC2DGAh4tAzLf3qh
id4=429710442058742
id5=AAMEFV5ED92QBB9J
id6=JeExEaUNV9
```

