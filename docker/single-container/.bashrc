PS1='${debian_chroot:+($debian_chroot)}\[\033[01;32m\]\u@\h\[\033[00m\]:\[\033[01;34m\]\w\[\033[00m\]\$ '
alias ls='ls --color=auto'
alias reminder='bash ~/.bashrc'
export CLASSPATH=$(for i in ${ACCUMULO_HOME}/lib/*.jar ; do echo -n $i: ; done)
export CLASSPATH=$CLASSPATH:$(for i in ${ACCUMULO_HOME}/lib/ext/*.jar ; do echo -n $i: ; done)
export CLASSPATH=$CLASSPATH:$(for i in $(find $HADOOP_HOME/share/hadoop/ -name "*.jar") ; do echo -n $i: ; done)
export CLASSPATH=$(echo $CLASSPATH | sed "s#:/opt/hadoop-2.9.0/share/hadoop/common/lib/slf4j-log4j12-1.7.25.jar##")
export CLASSPATH=$(echo $CLASSPATH | sed "s#:/opt/hadoop-2.9.0/share/hadoop/httpfs/tomcat/webapps/webhdfs/WEB-INF/lib/slf4j-log4j12-1.7.25.jar##")
export CLASSPATH=$(echo $CLASSPATH | sed "s#:/opt/hadoop-2.9.0/share/hadoop/kms/tomcat/webapps/kms/WEB-INF/lib/slf4j-log4j12-1.7.25.jar##")
reminder.sh