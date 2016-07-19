descrypt
========
descrypt将指定变量的值解密，并设置到相应的变量中。

> 加密过程参见[encrypt](encrypt.md)


### 实现模块

com.alogic.xscript.plugins.Descrypt

### 配置参数

支持下列参数：

| 编号 | 代码 | 说明 |
| ---- | ---- | ---- |
| 1 | in | 输入变量名 |
| 2 | out | 输出变量名 |
| 3 | key | 解密密钥的变量名 ｜
| 4 | coder | 加密算法，支持:MD5,DES3,DES,AES,SHA1等,缺省为DES3 |

### 案例

参见[encrypt](encrypt.md)

