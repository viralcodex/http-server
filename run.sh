#!/bin/bash
# Run script for simple Java project

echo "Select 1 to run TCPListenerMain and 2 for UPDSenderMain"
read -r option
if [ "$option" == "1" ]; then
  echo "Running TCPListenerMain..."
  java -cp out com.bootdev.tcplistener.TCPListenerMain
elif [ "$option" == "2" ]; then
  echo "Running UDPSenderMain..."
  java -cp out com.bootdev.udpsender.UDPSenderMain
else
  echo "Invalid Input..."
  exit 1
fi
