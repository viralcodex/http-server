#!/bin/bash
# Run script for simple Java project

echo """Select:
1 to run HTTPServer1
2 to run (legacy) TCPListenerMain
3 to run (legacy) UPDSenderMain
"""
read -r option
if [ "$option" == "1" ]; then
  echo "Running HTTP Server..."
  java -cp out com.bootdev.cmd.HTTPServerMain
elif [ "$option" == "2" ]; then
  echo "Running (legacy) TCPListenerMain..."
  java -cp out com.bootdev.legacy.tcplistener.TCPListenerMain | tee request.txt
elif [ "$option" == "3" ]; then
  echo "Running (legacy) UDPSenderMain..."
  java -cp out com.bootdev.legacy.udpsender.UDPSenderMain | tee updrequest.txt
else
  echo "Invalid Input..."
  exit 1
fi
