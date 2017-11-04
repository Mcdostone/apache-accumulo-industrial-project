# Scripts

This folder contains 3 scripts in order to have an fonctionnal machine quickly!

## Usage

### Setup the variables

The first step is to configure variables of each script 
depending on how you want to install Accumulo, Hadoop and Zookeeper.

*Example of configuration for Apache hadoop (install_hadoop:8)*:
    
    INSTALL_DIR=~/installs
    SOFTWARE_NAME=hadoop
    SOFTWARE_URL_DOWNLOAD="http://apache.crihan.fr/dist/hadoop/common/hadoop-2.8.2/hadoop-2.8.2.tar.gz"
    SOFTWARE_INSTALL_DIR=$INSTALL_DIR/$SOFTWARE_NAME
    HADOOP_STORAGE=$INSTALL_DIR/storage


### Run the scripts
    cd scripts/
    chmod +x log.sh         # Apply this command for all others scripts
    ./install_hadoop.sh
    ./install_zookeeper.sh
    ./install_accumulo.sh


### Start Accumulo

First of all, you have to format the HDFS nameNode and init an instance for accumulo: 

    ./init_hdfs.sh
    $ACCUMULO_HOME/bin/accumulo init

After that, run Hadoop, Zookeeper and accumulo. For the first time you run the script, it will ask you to define an instance name for accumulo.


    ./start-accumulo.sh


### Stop Accumulo
You have the possibility to stop all services running with accumulo:

    ./stop-accumulo.sh


### Troubleshooting
After the end of each script, you have to execute some commands in order to finalize the installation yourself (setup environment variables, edit config files ...)

If you have some trouble, you can read the help:

    ./install_X.sh -h
