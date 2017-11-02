#!/bin/bash

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
    if [ $? -ne 0 ]; then        
        log fail "Cannot check for SSH key"
        exit 1
    else
        log info "public RSA key is configured"
    fi
}


print_hadoop() 
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
download_and_install_hadoop() 
{
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
    DIR_NAME=$(tar -xvf /tmp/$FILENAME -C /tmp | sed -e 's@/.*@@' | uniq)
    log "info" "Installing hadoop at $INSTALL_DIR"
    execute_critiq_command mv /tmp/$DIR_NAME $INSTALL_DIR
    EXPORT HADOOP_HOME=$2
    export PATH=$PATH:${HADOOP_HOME}/sbin:${HADOOP_HOME}/bin
}

print_tips_after_install()
{
    if [ "$#" -ne 1 ]; then
        echo "Illegal number of parameters at print_tips_after_install:$LINENO, expected 1 param, given $#"
        echo "Param 1: Path where hadoop is install (HADOOP_HOME)"
        exit 1
    fi
    DIR_INSTALL=$1
    printf "\n\n\e[1m\e[4mThe installation of Hadoop is finished soon. You have to configure now hadoop!\n\n\e[0m"
    echo " 1. Setup JAVA_HOME variable in hadoop"
    echo -e " In the file $DIR_INSTALL/etc/hadoop/hadoop-env.sh, look for the line 'export JAVA_HOME' and set to:\n"
    echo -e "\texport JAVA_HOME=$JAVA_HOME\n"

    echo " 2. Disable debug logs"
    echo -e " Hadoop generates a lot of debug logs, if you want to disable these behavior (same file as step 1)\n"
    echo -e "\texport HADOOP_OPTS=\"\$HADOOP_OPTS -XX:-PrintWarnings -Djava.net.preferIPv4Stack=true"

    echo -e "\n Cool, follow now this tutorial\n\thttps://www.digitalocean.com/community/tutorials/how-to-install-the-big-data-friendly-apache-accumulo-nosql-database-on-ubuntu-14-04#step-9-—-install-and-configure-hadoop"
}

print_version()
{
    print_hadoop
    echo ""
    echo "                                          Installer version 0.1"
    echo "Tested on Ubuntu 17.10"
    echo -e "This script is based on the digitalOcean's tutorial\nhttps://www.digitalocean.com/community/tutorials/how-to-install-the-big-data-friendly-apache-accumulo-nosql-database-on-ubuntu-14-04\n"
}

print_help()
{
    echo -e " \e[1mUSAGE\e[0m"
	echo -e "\tThis script enables to install quicklt Apache Hadoop on a machine. Your only role as a user is to setup variables of scripts to fit your needs."

    echo -e "\n \e[1mFUNCTIONS\e[0m"
    echo -e "\tThis script is composed of a set of functions:"
	echo -e "\t\tcheck_mandatory_programs                 check if all programs are installed in order to continue the installation"
    echo -e "\t\tcheck_mandatory_environnment_variable    check if all env variables are configured"
    echo -e "\t\tcheck_ssh_key                            check if SSH key are generated for the hadoop user (and generate it if needed)"
    echo -e "\t\tcreate_hadoop_user                       Create the hadoop user"
    echo -e "\t\tdownload_and_install_hadoop              Download and install Hadoop in the correct directory"
    echo -e "\t\tprint_tips_after_install                 Print some tips for the end of installation"
    echo -e "\n \e[1mAUTHOR\e[0m"
    echo -e "\t- Yann Prono"
}

# All essential variables
INSTALL_DIR=~/installs
HADOOP_USER=hadoop
HADOOP_DATA=/data
HADOOP_INSTALL=$INSTALL_DIR/hadoop
HADOOP_URL_DOWNLOAD="http://apache.crihan.fr/dist/hadoop/common/hadoop-2.8.2/hadoop-2.8.2.tar.gz"


if [ $# -eq 0 ] ; then
    print_hadoop
    echo ""
    read -r -p "Have you configured all variables of the script (line 180) ? [y/n] " response
	if [[ $response == y* ]] || [[ $response == Y* ]]; then
        log info "Start the installation"
        mkdir -p $INSTALL_DIR
        check_mandatory_programs java git ssh rsync wget
        check_mandatory_environnment_variable JAVA_HOME
        create_hadoop_user hadoop /tmp/prout
        check_ssh_key $HADOOP_USER
        download_and_install_hadoop $HADOOP_URL_DOWNLOAD $HADOOP_INSTALL
        print_tips_after_install $HADOOP_INSTALL
    fi
else 
    while getopts "vh" OPT; do
        case "$OPT" in
            v)
                print_version
                ;;
            h)
                print_help
                ;;
        esac
    done
fi




