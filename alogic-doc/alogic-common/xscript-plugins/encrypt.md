encrypt
=======
encrypt将指定变量的值加密，并设置到相应的变量中。

> 解密过程参见[descrypt](descrypt.md)


### 实现模块

com.alogic.xscript.plugins.Encrypt

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | in | 输入值，可计算 |
| 2 | out | 输出变量名，可计算 |
| 3 | key | 加密的密钥，可计算 ｜
| 4 | coder | 加密算法，支持:MD5,DES3,DES,AES,SHA1等,缺省为DES3 |

### 案例

现有脚本:

```
	<script>
		<!--userId是密钥-->
        <set id="userId" value="alogic"/>
        <!--pwd是明文密码-->
        <set id="pwd" value="alogic123_"/>
        
        <!--通过DES3加密-->
        <encrypt out="pwdEncrypted" in="${pwd}" key="${userId}" coder="DES3"/>
        <log msg="pwdEncrypted=${pwdEncrypted}"/>
        
        <!--通过DES3解密-->
        <descrypt out="pwd" in="${pwdEncrypted}" key="${userId}" coder="DES3"/>
        <log msg="pwd=${pwd}"/>
        
        <!--通过AES加密-->
        <encrypt out="pwdEncrypted" in="${pwd}" key="${userId}" coder="AES"/>
        <log msg="pwdEncrypted=${pwdEncrypted}"/>
        
        <!--通过AES解密-->
        <descrypt out="pwd" in="${pwdEncrypted}" key="${userId}" coder="AES"/>
        <log msg="pwd=${pwd}"/>
        
        <!--通过SHA1加密-->
        <encrypt out="pwdEncrypted" in="${pwd}" key="${userId}" coder="SHA1"/>
        <log msg="pwdEncrypted=${pwdEncrypted}"/>
        
        <!--通过SHA1解密-->
        <descrypt out="pwd" in="${pwdEncrypted}" key="${userId}" coder="SHA1"/>
        <log msg="pwd=${pwd}"/>
        
        <!--通过MD5加密-->
        <encrypt out="pwdEncrypted" in="${pwd}" key="${userId}" coder="MD5"/>
        <log msg="pwdEncrypted=${pwdEncrypted}"/>	
	</script>
```
上述脚本的输出为：
```
	pwdEncrypted=RFNoJZDT+YxUXUlN/fD3iA==
	pwd=alogic123_
	pwdEncrypted=TKbFIoWAQLiSBTp+HO6VLg==
	pwd=alogic123_
	pwdEncrypted=Cxzx9OJLbw+ZXe0Gn7xoSohv+GU=
	pwd=alogic123_
	pwdEncrypted=2cMnvnFFLkvOEZ4uMtCX5g==
```


