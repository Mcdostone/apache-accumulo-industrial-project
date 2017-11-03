#!/bin/bash


#######################################
#       ALL ESSENTIAL VARIABLES       #
#               TO SETUP              #
#######################################
INSTALL_DIR=~/installs
SOFTWARE_NAME=hadoop
SOFTWARE_URL_DOWNLOAD="http://apache.crihan.fr/dist/hadoop/common/hadoop-2.8.2/hadoop-2.8.2.tar.gz"
SOFTWARE_INSTALL_DIR=$INSTALL_DIR/$SOFTWARE_NAME
HADOOP_STORAGE=$INSTALL_DIR/storage


check_mandatory_programs()
{
    for PGM in "$@"; do
        if ! [ -x "$(command -v $PGM)" ]; then
            log fail "$PGM is not installed, please install it to continue the installation"
            exit 1
        else
            log info "$PGM is installed"
        fi
    done
}

check_environnment_variables()
{
    for VAR in "$@"; do
        if [ -z "$(printenv | grep $VAR)" ]; then
           log fail "$VAR is not configured, please configure this environnment variable to continue the installation"
           exit 1
        else
           log info "$VAR is configured"
        fi
    done
}


print_software() 
{
    echo " __  __               __                            "
    echo "/\ \/\ \             /\ \                           "
    echo "\ \ \_\ \     __     \_\ \    ___     ___   _____   "
    echo " \ \  _  \  /'__\`\   /'_\` \  / __\`\  / __\`\/\ '__\`\ "
    echo "  \ \ \ \ \/\ \L\.\_/\ \L\ \/\ \L\ \/\ \L\ \ \ \L\ \\"
    echo "   \ \_\ \_\ \__/.\_\ \___,_\ \____/\ \____/\ \ ,__/"
    echo "    \/_/\/_/\/__/\/_/\/__,_ /\/___/  \/___/  \ \ \/ "
    echo "                                              \ \_\ "
    echo "                                               \/_/ "
}

log()
{
    if [ "$#" -ne 2 ]; then
        echo "Illegal number of parameters, expected 2 parameters, given $#"
        echo "Param 1: Level of log (info|warning|error)"
        echo "Param 2: Message to log"
        exit 1
    fi
    LEVEL=$(echo $1 | tr "[:lower:]" "[:upper:]")
    shift
    if [ "$LEVEL" = "INFO" ] ; then
        COLOR="\e[34m"
    fi
    if [ "$LEVEL" = "WARN" ] ; then
        COLOR="\e[33m"
    fi
    if [ "$LEVEL" = "FAIL" ] ; then
        COLOR="\e[31m"
    fi
    if [ -n "$COLOR"  ] ; then
        printf "\e[1m[${COLOR}%s\e[39m]\e[21m %s\n" "$LEVEL" "$*"
    fi
}

execute_critical_command()
{
    COMMAND=$1
    shift
    eval $COMMAND $*
    if [ $? -ne 0 ]; then
        log warn "Cannot execute command '$COMMAND $*' (need sudo maybe?)"
        eval sudo $COMMAND $*
        if [ $? -ne 0 ]; then
            log fail "Cannot execute command '$COMMAND $*', stop here"
            exit 1
        fi
    fi
}

check_ssh_key()
{   
    if [ "$#" -ne 1 ]; then
        log "fail" "Illegal number of parameters at check_ssh_key:$LINENO, expected 1 param, given $#"
        echo " - Param 1: the user name of the hadoop user"
        exit 1
    fi
    log info "Check if SSH keys exists for user $1, please enter password for $1"
    su - $1 -c "if [ ! -f /home/$1/.ssh/id_rsa.pub ]; then ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa; cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized; fi"
    if [ $? -ne 0 ]; then        
        log fail "Cannot check for SSH key"
        exit 1
    else
        log info "Public RSA key is configured"
    fi
}

create_hadoop_user()
{
    if [ "$#" -ne 1 ]; then
        log "fail" "Illegal number of parameters at create_hadoop_user:$LINENO, expected 1 paramS, given $#"
        echo " - Param 1: the directory to create for HDFS (naming and data storage)"
        exit 1
    fi
    DATA_DIR=$1
    read -r -p "Do you want to create a dedicated user for hadoop? [y/n] " response
	if [[ $response == y* ]] || [[ $response == Y* ]]; then
    log "info" "Creating the 'hadoop' user"
        USERNAME="hadoop"    
        if id $1 >/dev/null 2>&1; then
            log "warn" "User '$USERNAME' exists"
        else
            execute_critical_command "adduser" $USERNAME
            log "info" "Hadoop user created with success"
        fi
    else
        USERNAME=$USER
    fi
    log "info" "Data directory for hadoop is $DATA_DIR"
    execute_critical_command mkdir "-p" $DATA_DIR
    execute_critical_command mkdir "-p" $DATA_DIR/name
    execute_critical_command mkdir "-p" $DATA_DIR/data
    log "info" "HDFS directory '$DATA_DIR' created with success"
    execute_critical_command chown "$USERNAME:$USERNAME" $DATA_DIR
    check_ssh_key $USERNAME
}

before_installing()
{
    log info 'Before installing'
    check_mandatory_programs java git ssh rsync wget sshd
    check_environnment_variables JAVA_HOME
    create_hadoop_user $HADOOP_STORAGE
}

download_and_install() 
{
    if [ "$#" -ne 2 ]; then
        echo "Illegal number of parameters at download_and_install:$LINENO, expected 2 params, given $#"
        echo "Param 1: URL to download the software"
        echo "Param 2: Path to install the software"
        exit 1
    fi
        URL=$1
        INSTALL_DIR=$2
    if [ -d $INSTALL_DIR ]; then 
        log warn "Skip installation, already installed at '$INSTALL_DIR'"
    else
        FILENAME=$(basename $URL)
        log "info" "Downloading $FILENAME"
        wget -cP /tmp $URL
        log "info" "Untar the archive /tmp/$FILENAME"
        DIR_NAME=$(tar -xvf /tmp/$FILENAME -C /tmp | sed -e 's@/.*@@' | uniq)
        log "info" "Installing at $INSTALL_DIR"
        execute_critical_command mv /tmp/$DIR_NAME $INSTALL_DIR
    fi
}


after_installing()
{
    if [ "$#" -ne 2 ]; then
        echo "Illegal number of parameters at after_installing:$LINENO, expected 2 params, given $#"
        echo "Param 1: Home directory where hadoop is install (HADOOP_HOME)"
        echo "Param 2: Path where data are stored"
        exit 1
    fi
    HADOOP_DIR=$1
    DATA_DIR=$2
    printf "\n\n\e[1m\e[4mThe installation of Hadoop is finished soon. You have to configure it now!\n\n\e[0m"
    
    echo " 1. Setup environnment variables"
    echo -e "\texport HADOOP_HOME=$HADOOP_DIR"
    echo -e "\texport PATH=\$PATH:${HADOOP_DIR}/sbin:${HADOOP_DIR}/bin\n\n"
    
    echo " 2. Setup JAVA_HOME variable in hadoop"
    echo -e " In the file $HADOOP_DIR/etc/hadoop/hadoop-env.sh, look for the line 'export JAVA_HOME' and set to:\n"
    echo -e "\texport JAVA_HOME=$JAVA_HOME\n\n"

    echo " 3. Disable debug logs"
    echo -e " Hadoop generates a lot of debug logs, if you want to disable these behavior (same file as step 1)\n"
    echo -e "\texport HADOOP_OPTS=\"\$HADOOP_OPTS -XX:-PrintWarnings -Djava.net.preferIPv4Stack=true\n\n"

    echo " 4. Setup core-site.xml"
    echo -e " Modify the file $HADOOP_DIR/etc/hadoop/core-site.xml to configure the namenode's hostname and port:\n"
    echo -e "\t<property>"
    echo -e "\t\t <name>fs.defaultFS</name>"
    echo -e "\t\t<value>hdfs://localhost:9000</value>"
    echo -e "\t</property>\n\n"

    echo " 5. Setup hdfs-site.xml"
    echo -e " Modify the file $HADOOP_DIR/etc/hadoop/hdfs-site.xml to configure the 3 properties:\n"
    echo -e "\t<configuration>
\t\t<property>
\t\t\t<name>dfs.replication</name>
\t\t\t<value>1</value>
\t\t</property>
\t\t<property>
\t\t\t<name>dfs.name.dir</name>
\t\t\t<value>$DATA_DIR/name</value>
\t\t</property>
\t\t<property>
\t\t\t<name>dfs.data.dir</name>
\t\t\t<value>$DATA_DIR/data</value>
\t\t</property>
\t</configuration>\n\n"

    echo " 6. Setup mapred-site.xml"
    echo -e " Modify the file $HADOOP_DIR/etc/hadoop/mapred-site.xml to configure the hostname and port number on which the MapReduce job tracker runs:\n"
    echo -e "\t<property>"
    echo -e "\t\t<name>mapred.job.tracker</name>"
    echo -e "\t\t<value>localhost:9001</value>"
    echo -e "\t</property>\n\n"
    
    echo " 7. Initialize and start the nameNode"
    echo -e "\thdfs namenode -format"
    echo -e "\tstart-dfs.sh\n"
}

print_version()
{
    print_software
    echo ""
    echo "                                          Installer version 0.1"
    FILENAME=$(basename $SOFTWARE_URL_DOWNLOAD .tar.gz)
    echo "Tested on Ubuntu 17.10 for $FILENAME"
    echo -e "This script is based on the digitalOcean's tutorial\nhttps://www.digitalocean.com/community/tutorials/how-to-install-the-big-data-friendly-apache-accumulo-nosql-database-on-ubuntu-14-04\n"
}

print_help()
{
    echo -e "\e[1m$0\e[0m\n"
    echo -e "\e[1mUSAGE\e[0m"
	echo -e "\tThis script enables to install $SOFTWARE_NAME on your machine. Your role as a user is to setup variables of scripts to fit your needs (at the beginning of script)"

    echo -e "\n\e[1mDESCRIPTION\e[0m"
    echo -e "\t\e[1m-b\e[0m"
    echo -e "\t\tRun only the function \e[2mbefore_installing\e[0m"
    echo -e "\t\e[1m-a\e[0m"
    echo -e "\t\tRun only the function \e[2mafter_installing\e[0m"

    echo -e "\t\e[1m-v\e[0m"
    echo -e "\t\tDisplay the version and exit"
    echo -e "\t\e[1m-h\e[0m"
    echo -e "\t\tDisplay the help and exit"

    echo -e "\n\e[1mFUNCTIONS\e[0m"
    echo -e "\tThis script is composed of a set of functions:"
	echo -e "\t\e[1mbefore_installing\e[0m"
    echo -e "\t\tThis is the first function called at startup of this script, you have to implement this function"
    echo -e "\t\e[1mdownload_and_install\e[0m"
    echo -e "\t\tDownload and install the software in the correct directory"
    echo -e "\t\e[1mafter_installing\e[0m"
    echo -e "\t\tFunction called after the installation"
    echo -e "\t\e[1mcheck_mandatory_programs(pgms...)\e[0m"
    echo -e "\t\tCheck if all mandatory programs are installed on the machine"
    echo -e "\t\e[1mcheck_environnment_variables(vars...)\e[0m"
    echo -e "\t\tCheck if all environment variables are correctly configured"
    
    echo -e "\t\e[1mexecute_critical_command(cmd)\e[0m"
    echo -e "\t\tExecutes a critical command. If the first try fails, the command is executed again with sudo"
    echo -e "\t\e[1mlog(level=info|warn|fail, msg)\e[0m"
    echo -e "\t\tSimple logger for this script"
    echo -e "\t\e[1mprint_software\e[0m"
    echo -e "\t\tPrint ASCII text"
    echo -e "\t\e[1mprint_version\e[0m"
    echo -e "\t\tPrint the version of this script installer"
    echo -e "\t\e[1mprint_help\e[0m"
    echo -e "\t\tPrint this help"
    
    echo -e "\n\e[1mAUTHOR\e[0m"
    echo -e "\tYann Prono, mcdostone"
}


if [ $# -eq 0 ] ; then
    print_software
    echo ""
    read -r -p "Have you configured all variables of the script (line 8) ? [y/n] " response
	if [[ $response == y* ]] || [[ $response == Y* ]]; then
        log info "Start the installation"
        before_installing
        download_and_install $SOFTWARE_URL_DOWNLOAD $SOFTWARE_INSTALL_DIR
        after_installing $SOFTWARE_INSTALL_DIR $HADOOP_STORAGE
    fi
else 
    while getopts "abhv" OPT; do
        case "$OPT" in
            a)
                after_installing $SOFTWARE_INSTALL_DIR $HADOOP_STORAGE
                ;;
            b)
                before_installing
                ;;
            h)
                print_help
                ;;
            v)
                print_version
                ;;
        esac
    done
fi




