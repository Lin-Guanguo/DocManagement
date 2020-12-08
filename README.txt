计算机zy1901 林观果
面向对象程序设计课 实验作业

基本功能已全部实现：
    GUI，数据库，网络，并发
    主目录下的DocManagementGUIClient.jar 与 DocManagementServer.jar
    已打包好依赖，可直接使用
    $ java -jar ./DocManagementGUIClient.jar
    服务端还需配置好MySql环境和账号

    客户端和服务器的配置在serverconfig和clientconfig文件夹内，客户端默认
    连接我个人的云服务器，上面常开者一个服务端程序，可以不用配置MySql环境
    直接测试客户端

下一步目标：
    GUI界面的下载放到后台线程进行，避免下载大文件堵塞GUI界面显示，增加进度条