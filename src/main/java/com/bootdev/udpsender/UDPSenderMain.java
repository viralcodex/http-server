package com.bootdev.udpsender;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class UDPSenderMain {
    public static void main(String[] args) {
        InetSocketAddress socketAddress = new InetSocketAddress("localhost", 42069);

        try (DatagramSocket socket = new DatagramSocket())
        {
            // Don't use connect() for UDP - keep it connectionless
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            while(true)
            {
                System.out.print(">");
                String line = bufferedReader.readLine();

                if(line == null)
                {
                    break;
                }
                //get the data from the line
                byte[] data = line.getBytes(StandardCharsets.UTF_8);

                // Pass destination address in the packet constructor
                DatagramPacket packet = new DatagramPacket(data, data.length, socketAddress);
                socket.send(packet);
            }

        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
