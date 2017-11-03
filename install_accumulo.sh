#!/bin/bash


#######################################
#       ALL ESSENTIAL VARIABLES       #
#               TO SETUP              #
#######################################
INSTALL_DIR=~/installs
SOFTWARE_NAME=accumulo
SOFTWARE_URL_DOWNLOAD="http://apache.mediamirrors.org/accumulo/1.8.1/accumulo-1.8.1-bin.tar.gz"
SOFTWARE_INSTALL_DIR=$INSTALL_DIR/$SOFTWARE_NAME


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
    echo " ______                                              ___             ";
    echo "/\  _  \                                            /\_ \            ";
    echo "\ \ \L\ \    ___    ___   __  __    ___ ___   __  __\//\ \     ___   ";
    echo " \ \  __ \  /'___\ /'___\/\ \/\ \ /' __\` __\`\/\ \/\ \ \ \ \   / __\`\ ";
    echo "  \ \ \/\ \/\ \__//\ \__/\ \ \_\ \/\ \/\ \/\ \ \ \_\ \ \_\ \_/\ \L\ \\";
    echo "   \ \_\ \_\ \____\ \____\\ \____/\ \_\ \_\ \_\ \____/ /\____\ \____/";
    echo "    \/_/\/_/\/____/\/____/ \/___/  \/_/\/_/\/_/\/___/  \/____/\/___/ ";
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


before_installing()
{
    check_mandatory_programs wget java hdfs zkServer.sh
    check_environnment_variables JAVA_HOME HADOOP_HOME ZOOKEEPER_HOME
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
    printf "\n\n\e[1m\e[4mThe installation of $SOFTWARE_NAME is finished soon.\n\n\e[0m\n"

    echo " 1. Setup the configuration"
    echo -e " Accumulo comes with sample configurations for servers with various memory sizes: 512 MB, 1 GB, 2 GB and 3 GB. Choose one:"
    echo -e "\t 512 MB  [1]"
    echo -e "\t 1 GB    [2]"
    echo -e "\t 2 GB    [3]"
    echo -e "\t 3 GB    [4]"
    read -r -p "> " response
    FOLDER_CONF=512MB
    case "$response" in
        1)
            FOLDER_CONF=512MB
            ;;
        2)
            FOLDER_CONF=1GB
            ;;
        3)
            FOLDER_CONF=2GB
            ;;
        4)
            FOLDER_CONF=3GB
            ;;
    esac
    log info "Prepare configuration with $FOLDER_CONF"
    cp $SOFTWARE_INSTALL_DIR/conf/examples/$FOLDER_CONF/standalone/* $SOFTWARE_INSTALL_DIR/conf/

    echo -e "\n 2. Setup accumulo-env.sh"
    echo -e " In $SOFTWARE_INSTALL_DIR/conf/accumulo-env.sh, change the following value:\n"
    echo -e "\texport ACCUMULO_MONITOR_BIND_ALL=\"true\""

    echo -e "\n 3. Setup accumulo-site.xml"
    echo -e " Accumulo's worker processes communicate with each other using a secret key. This should be changed to a string which is secure."
    echo -e " In $SOFTWARE_INSTALL_DIR/conf/accumulo-site.xml, change the following value:\n"
    echo -e "\t<property>"
    echo -e "\t\t<name>instance.secret</name>"
    echo -e "\t\t<value>SECRETTOKEN684564&#65FSDJ</value>"
    echo -e "\t</property>\n"

    echo -e " After that, add a new property in the same file:"
    echo -e "\t<property>"
    echo -e "\t\t<name>instance.volumes</name>"
    echo -e "\t\t<value>hdfs://localhost:9000/accumulo</value>"
    echo -e "\t</property>"

    echo -e "\n The last step is to define a secured password:"
    echo -e "\t<property>"
    echo -e "\t\t<name>trace.token.property.password</name>"
    echo -e "\t\t<value>AWEsOM3Pa22w0rd</value>"
    echo -e "\t</property>"

    echo -e "\n 4. Initialize Accumulo"
    echo -e "\t$SOFTWARE_INSTALL_DIR/bin/accumulo init"
    echo -e "\t# Define an instance name and enter the password defined previously"
    echo -e "\t$SOFTWARE_INSTALL_DIR/bin/start-all.sh"
}

print_version()
{
    print_software
    echo ""
    echo "                                          Installer version 0.1"
    FILENAME=$(basename $SOFTWARE_URL_DOWNLOAD .tar.gz)
    echo "Tested on Ubuntu 17.10 for $FILENAME"

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
        after_installing
    fi
else 
    while getopts "abhv" OPT; do
        case "$OPT" in
            a)
                after_installing
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




