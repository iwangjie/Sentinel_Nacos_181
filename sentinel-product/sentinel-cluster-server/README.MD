## 启动命令
java -Dserver.port=8080 -Dnacos.addr=192.168.40.70:8848 -Dnacos.namespaces=dev,test -jar sentinel-cluster-server.jar

###  开发测试环境
nohup java -Dserver.port=8080 -Dnacos.addr=192.168.40.70:8848 -Dnacos.namespaces=dev,test -jar sentinel-cluster-server.jar > sentinel-cluster-server-dev.log 2>&1 &

