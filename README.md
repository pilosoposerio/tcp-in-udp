# TCP Simulation using UDP
TCP is mimicked using UDP.

# Author
POSERIO, CLINTON E. | 2012-27803 | CMSC137 B-1L


# Specifications
The simulation should implement: 
[1] UDP message body that includes headers that are lacking from TCP.
    - Sequence/Synchronization Number
    - Acknowledgement Number 
    - Acknowledgement Bit 
    - Synchronization Bit 
    - Finish Bit 
    - Window Size
[2] threeway handshake connection
[3] timeout and resend of TCP packet
[4] window sliding mechanism on both source and destination
[5] acknowledgement of fully received packets
[6] correct use of synchronization numbers and acknowledgement numbers
[7] fourway handshake disconnection
[8] Also the following:
    a.) To mimick dropping of packets, it should randomly drop packets given variable of 0%, 25%, 50%, and 75%.
    b.) Both client and server should have different port numbers but should work on IP address: 127.0.0.1
    c.) To mimick latency, Both server and client should only show results of receiving packets after 2 seconds delay.
    d.) Network timeout and resending should be pegged on 4 seconds.
    e.) Waiting time after disconnection should be pegged at 10 seconds. 
    f.) It should show that both client and server has already disconnected after 10 seconds.

# Compile
On the terminal, enter 'javac *.java'

# Run Server
Enter 'java Server <port>'
(NOTE: Only port 80 or 8080 works here on cloud9. idky :( )

# Run Client
Enter 'java Client <port>'
(NOTE: Only port 80 or 8080 works here on cloud9. idky :( )
