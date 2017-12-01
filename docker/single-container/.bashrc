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
echo -e "\t*                                         *" 
echo -e "\t*  To start accumulo, run 'entrypoint.sh' *"
echo -e "\t*******************************************\e[21m\n"