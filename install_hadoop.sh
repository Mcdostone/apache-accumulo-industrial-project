#!/bin/bash

# All essential variables
HADOOP_USER=hadoop
HADOOP_DATA=/data
HADOOP_INSTALL=~/
HADOOP_URL_DOWNLOAD="http://apache.crihan.fr/dist/hadoop/common/hadoop-2.8.2/hadoop-2.8.2.tar.gz"

# Creation of user and the folder for data

check_mandatory_programs() {
    for PGM in "$@"; do
        if ! [ -x "$(command -v $PGM)" ]; then
            log fail "$PGM is not installed, please install it to continue the installation"
            exit 1
        else
            log info "$PGM is installed"
        fi
    done
}

check_mandatory_environnment_variable() {
    for VAR in "$@"; do
        if [ -z "$(printenv | grep $VAR)" ]; then
           log fail "$VAR is not configured, please configure this environnment variable to continue the installation"
           exit 1
        else
           log info "$VAR is configured"
        fi
    done
}

check_ssh_key() {   
    if [ "$#" -ne 1 ]; then
        log "fail" "Illegal number of parameters at check_ssh_key:$LINENO, expected 1 param, given $#"
        echo " - Param 1: the user name of the hadoop user"
        exit 1
    fi
    log info "Check if SSH keys exists for user $1, please enter password for $1"
    su - $1 -c "if [ ! -f /home/$1/.ssh/id_rsa.pub ]; then ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa; cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized; fi"
    log info "public RSA key is configured"
}


print_hadoop() 
{
    echo ""
    printf "%s\n" '  /\\\        /\\\     /\\\\\\\\\     /\\\\\\\\\\\\          /\\\\\            /\\\\\       /\\\\\\\\\\\\\           '
    printf "%s\n" '  \/\\\       \/\\\   /\\\\\\\\\\\\\  \/\\\////////\\\      /\\\///\\\        /\\\///\\\    \/\\\/////////\\\        '
    printf "%s\n" '   \/\\\       \/\\\  /\\\/////////\\\ \/\\\      \//\\\   /\\\/  \///\\\    /\\\/  \///\\\  \/\\\       \/\\\       '
    printf "%s\n" '    \/\\\\\\\\\\\\\\\ \/\\\       \/\\\ \/\\\       \/\\\  /\\\      \//\\\  /\\\      \//\\\ \/\\\\\\\\\\\\\/       '
    printf "%s\n" '     \/\\\/////////\\\ \/\\\\\\\\\\\\\\\ \/\\\       \/\\\ \/\\\       \/\\\ \/\\\       \/\\\ \/\\\/////////        '
    printf "%s\n" '      \/\\\       \/\\\ \/\\\/////////\\\ \/\\\       \/\\\ \//\\\      /\\\  \//\\\      /\\\  \/\\\                '
    printf "%s\n" '       \/\\\       \/\\\ \/\\\       \/\\\ \/\\\       /\\\   \///\\\  /\\\     \///\\\  /\\\    \/\\\               '
    printf "%s\n" '        \/\\\       \/\\\ \/\\\       \/\\\ \/\\\\\\\\\\\\/      \///\\\\\/        \///\\\\\/     \/\\\              '
    printf "%s\n" '         \///        \///  \///        \///  \////////////          \/////            \/////       \///              '
    echo ""
}

log()
{
    if [ "$#" -ne 2 ]; then
        echo "Illegal number of parameters, expected 2 parameters, given $#"
        echo "Parameter 1: Level of log (info|warning|error)"
        echo "Parameter 2: Message to log"
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

execute_critiq_command()
{
    COMMAND=$1
    shift
    eval $COMMAND $*
    if [ $? -ne 0 ]; then
        log "fail" "Cannot execute command '$COMMAND $*' (need sudo maybe?)"
        exit 1
    fi
}

create_hadoop_user() {
    if [ "$#" -ne 2 ]; then
        log "fail" "Illegal number of parameters at create_hadoop_user:$LINENO, expected 2 parameters, given $#"
        echo " - Param 1: the user name for the hadoop user"
        echo " - Param 2: the directory to create for HDFS"
        exit 1
    fi
    log "info" "Creating the 'hadoop' user"
    USERNAME=$1
    DIR_DATA=$2
    if id $1 >/dev/null 2>&1; then
        log "warn" "User '$USERNAME' exists"
    else
        execute_critiq_command "adduser" $USERNAME
        log "info" "Hadoop user created with success"
    fi
    execute_critiq_command mkdir "-p" $DIR_DATA
    log "info" "HDFS directory '$DIR_DATA' created with success"
    execute_critiq_command chown "$1:$1" $DIR_DATA
}



# Downloading the tar.gz file and installing hadoop
download_and_install_hadoop() {
    if [ "$#" -ne 2 ]; then
        echo "Illegal number of parameters at download_and_install_hadoop:$LINENO, expected 2 params, given $#"
        echo "Parameter 1: URL to download hadoop"
        echo "Parameter 1: Path to install hadoop"
        exit 1
    fi
    URL=$1
    INSTALL_DIR=$2
    FILENAME=$(basename $URL)
    log "info" "Downloading $FILENAME"
    wget -P /tmp $URL
    log "info" "Untar the archive /tmp/$FILENAME"
    tar -xf /tmp/$FILENAME -C /tmp
    log "info" "Installing hadoop at $INSTALL_DIR"
    #sudo chown hadoop:hadoop -R hadoop-2.7.3
}


print_hadoop
check_mandatory_programs javac git wget
check_mandatory_environnment_variable JAVA_HOME
check_ssh_key $HADOOP_USER
echo "end"
#create_hadoop_user hadoop /tmp/prout
#download_and_install_hadoop $HADOOP_URL_DOWNLOAD $HADOOP_INSTALL