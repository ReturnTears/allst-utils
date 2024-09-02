# Linux
```text
1、查看所有内容
cat  syslog.log
cat  syslog.log | grep  '搜索关键日志文字'

2、查看文件的前100行
head -n 100  syslog.log

3、查看日志的末尾
tail   syslog.log
tail   -n   100  syslog.log
tail   -n   100  -f  syslog.log

4、翻页查看
less  syslog.log 
less  syslog.log   | grep  '搜索关键日志文字'

如果是压缩文件可以使用 zless   
zless   syslog.log.zip 

比较有用的，看看时间范围的日志，当我们知道错误的内容发生在哪个时间段时，又要看完整日志这种方式最好，不需要把日志下载下来看，特别是大日志。 
less  syslog.log   | grep  '2024-08-20 09:30'

几个日志文件一起看
tail -f /var/log/syslog.log  /var/log/messages.log
```
