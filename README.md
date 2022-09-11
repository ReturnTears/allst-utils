#allst utils

## note 短信发送
        
        消息发送使用的创蓝短信平台
                        (开发文档可以查看创蓝官方文档)
        
## push 消息推送
        消息推送使用的友盟平台
                        (开发文档可以查看Umeng官方文档)

## websocket 数据推送
        与其他服务或平台对接数据
                        SpringBoot本队WS有很好的支持
        

## Socket

## Http 


## Alibaba Cloud Toolkit

## Alibaba easyexecl

## 正则(Regex)
[Regex](Regex.md)

## IDEA2018.x转到IDEA2020.x遇到的问题
```
安装IntelliJ IDEA 2020.1.3以后运行不了的问题
因为本人之前用的是2018版的idea，现在想更新换代一下，所以从网上下载了一个idea2020版，但是安装流程走完以后，勾选运行idea，或者点击桌面的快捷方式，或者点击安装目录下面的.exe文件，都无法运行idea
IDEA2020新版的安装配置文件改变了，不在C盘的具体用户下面生成.IntelliJIdea2018的配置文件夹了，2020版本的配置
我的路径为：
C:\Users\admin\AppData\Roaming\JetBrains\IntelliJIdea2020.1
查看配置文件，发现idea64.exe.vmoptions文件中，多了一行：
-javaagent:D:\Program Files\JetBrains\IntelliJ IDEA 2018.3.6\bin\JetbrainsIdesCrack-4.2-release-sha1-3323d5d0b82e716609808090d3dc7cb3198b8c4b.jar
应该是之前的2018的配置文件未清理干净或者同时安装多个IDEA不同版本造成的，
删除掉这一行就可以正常启动了
```


