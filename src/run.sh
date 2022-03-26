#!/usr/bin/env bash
# 判断是否存在 env.sh 文件，如果存在则让它生效，这里的 env.sh 是可以写一些环境变量进去的
if [ -f $(pwd $0)/../env.sh ]; then
  echo "source $(pwd $0)/../env.sh"
  source $(pwd $0)/../env.sh
fi
DEPLOY_DIR=$(pwd)
# 打包的压缩包根目录 config 文件夹
CONF_DIR=$DEPLOY_DIR/config
# 打包的压缩包根目录 lib 文件夹
LIB_DIR=$DEPLOY_DIR/lib
# 打包的压缩包根目录 bin 文件夹
BIN_DIR=$DEPLOY_DIR/bin
JAR_NAME=$(ls -lt $BIN_DIR | grep .jar$ | head -n 1 | awk '{print $9}')
echo "Found Jar file '$JAR_NAME', we are starting it"
PIDS=$(ps -f | grep java | grep "$CONF_DIR" | awk '{print $2}')
if [ "$1" = "status" ]; then
  if [ -n "$PIDS" ]; then
    echo "The $ARTIFACTID is running...!"
    echo "PID: $PIDS"
    exit 0
  else
    echo "The $ARTIFACTID is stopped"
    exit 0
  fi
fi
if [ -n "$PIDS" ]; then
  echo "ERROR: The $ARTIFACTID already started!"
  echo "PID: $PIDS"
  exit 1
fi
if [ -n "$2" ]; then
  SERVER_PORT_COUNT=$(netstat -tln | grep $2 | wc -l)
  if [ $SERVER_PORT_COUNT -gt 0 ]; then
    echo "ERROR: The $ARTIFACTID port $2 already used!"
    exit 1
  fi
fi
# 日志输出目录
LOGS_DIR=$DEPLOY_DIR/logs
if [ ! -d $LOGS_DIR ]; then
  mkdir $LOGS_DIR
fi
STDOUT_FILE=$LOGS_DIR/stdout.log
JAVA_OPTS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true "
JAVA_DEBUG_OPTS=""
if [ "$1" = "debug" ]; then
  JAVA_DEBUG_OPTS=" -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n "
fi
JAVA_JMX_OPTS=""
if [ "$1" = "jmx" ]; then
  JAVA_JMX_OPTS=" -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false "
fi
JAVA_MEM_OPTS=""
BITS=$(java -version 2>&1 | grep -i 64-bit)
if [ -n "$BITS" ]; then
  JAVA_MEM_OPTS=" -server -Xms$XMS_VALUE -Xmx$XMX_VALUE -Xmn$XMN_VALUE -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 "
else
  JAVA_MEM_OPTS=" -server -Xms$XMS_VALUE -Xmx$XMX_VALUE -XX:SurvivorRatio=2 -XX:+UseParallelGC "
fi
CONFIG_FILES=" -Dlogging.path=$LOGS_DIR -Dspring.config.location=$CONF_DIR/ "
LIB_JARS=$(ls $LIB_DIR | grep .jar | grep -v $ARTIFACTID | awk '{print "'$LIB_DIR'/"$0}' | tr "\n" ":")
#JAVA_EXT_JARS=$(ls $JAVA_HOME/jre/lib/ext | grep .jar | awk '{print "'$JAVA_HOME/jre/lib/ext'/"$0}' | tr "\n" ":")
#JAVA_JARS=$(ls $JAVA_HOME/jre/lib | grep .jar | awk '{print "'$JAVA_HOME/jre/lib'/"$0}' | tr "\n" ":")
#EXT_DIRS="-Djava.ext.dirs=$LIB_DIR:$JAVA_HOME/jre/lib/ext:$JAVA_HOME/jre/lib"
CLASS_PATH_VALUE=" -classpath $BIN_DIR/$JAR_NAME:$LIB_JARS "

# 如果用到了 Apollo 配置中心，则打开下一行的注释内容
# APOLLO_CONFIG_VALUE=" -Denv=$APOLLO_ENV -Dapollo.configService=$APOLLO_CONFIG_SERVICE "

# -Dspring.devtools.restart.enabled=false is to prevent the project from using devtools to cause unlimited restart in Columbus environment
JAVA_SYSTEM_PROPERTY=" -Dspring.devtools.restart.enabled=false "
if [ "$ENVIRONMENT" = "K8S" ]; then
  # 如果是 kubernetes 部署服务的话，就可以通过传入环境变量 ENVIRONMENT = K8S，执行这段启动命令
  echo "K8S deploy success ~ "
  # 如果使用的 Apollo 配置中心，就加上 $APOLLO_CONFIG_VALUE 这段
  echo "exec java $JAVA_SYSTEM_PROPERTY $JAVA_OPTS $JAVA_MEM_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS $CONFIG_FILES $CLASS_PATH_VALUE $MAIN_CLASS"
  exec java $JAVA_SYSTEM_PROPERTY $JAVA_OPTS $JAVA_MEM_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS $CONFIG_FILES $CLASS_PATH_VALUE $MAIN_CLASS
else
  # 如果是普通 Linux 环境部署服务的话，就会走这段
  echo "Linux deploy success ~ "
  # 如果使用的 Apollo 配置中心，就加上 $APOLLO_CONFIG_VALUE 这段
  echo "nohup java $JAVA_SYSTEM_PROPERTY $JAVA_OPTS $JAVA_MEM_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS $CONFIG_FILES $CLASS_PATH_VALUE $MAIN_CLASS > $STDOUT_FILE 2>&1 &"
  nohup java $JAVA_SYSTEM_PROPERTY $JAVA_OPTS $JAVA_MEM_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS $CONFIG_FILES $CLASS_PATH_VALUE $MAIN_CLASS >$STDOUT_FILE 2>&1 &
  PIDS=$(ps -f | grep java | grep "$DEPLOY_DIR" | awk '{print $2}')
  echo "PID: $PIDS"
  echo "STDOUT: $STDOUT_FILE"
fi