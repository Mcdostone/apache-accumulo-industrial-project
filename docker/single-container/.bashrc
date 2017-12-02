PS1='${debian_chroot:+($debian_chroot)}\[\033[01;32m\]\u@\h\[\033[00m\]:\[\033[01;34m\]\w\[\033[00m\]\$ '
alias ls='ls --color=auto'
alias reminder='bash ~/.bashrc'

echo " ______                                              ___             "
echo "/\  _  \                                            /\_ \            "
echo "\ \ \L\ \    ___    ___   __  __    ___ ___   __  __\//\ \     ___   "
echo " \ \  __ \  /'___\ /'___\/\ \/\ \ /' __\` __\`\/\ \/\ \ \ \ \   / __\`\ "
echo "  \ \ \/\ \/\ \__//\ \__/\ \ \_\ \/\ \/\ \/\ \ \ \_\ \ \_\ \_/\ \L\ \\"
echo "   \ \_\ \_\ \____\ \____\\ \____/\ \_\ \_\ \_\ \____/ /\____\ \____/"
echo "    \/_/\/_/\/____/\/____/ \/___/  \/_/\/_/\/_/\/___/  \/____/\/___/ "
echo -e "\n                    Single container (Zookeeper + Hadoop + Accumulo)\n"
echo -e "\t\e[1m*******************************************"
printf "\t*  Instance name: %-23s *\n" $INSTANCE 
printf "\t*  Password: %-28s *\n" $ACCUMULO_PASSWORD 
printf "\t*  Default table: %-23s *\n" $ACCUMULO_DEFAULT_TABLE 
if nc -z localhost 50070 ; then
    printf "\t*  %-38s *\n" "http://localhost:50070"
else
    printf "\t*  %-38s *\n" "Hadoop overview not available"
fi
if nc -z localhost 9995 ; then
    printf "\t*  %-38s *\n" "http://localhost:9995"
else
    printf "\t*  %-38s *\n" "Accumulo overview not available"
fi
echo -e "\t*                                         *" 
echo -e "\t*  To start accumulo, run 'entrypoint.sh' *"
echo -e "\t*******************************************\e[21m\n"
