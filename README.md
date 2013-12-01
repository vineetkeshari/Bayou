Bayou
=====

An implementation of the Bayou epidemic protocol for CS 380D

Vineet Keshari (EID vk3226) : Slip days (0 on this, 6 total)
Vijay Talluru (EID vt4225) : Slip days (0 on this, 4 total)

To run:
Go to Bayou/
$./run

Command-line interface:

Network Structure:
JOIN	X
    Joins a process X
LEAVE	X
    Retires a process X
CONNECT	X
    Client connects to server X
ISOLATE	X
    X is disconnected from the network
RECONNECT	X
    X is re-connected to the network
BREAK	X	Y
    Break communication between X and Y (two-way)
RECOVER	X	Y
    Recover broken communication between X and Y (two-way)

Commands:
(command goes to the currently connected server)
ADD	Song	Url
Remove	Song
EDIT	Song	Song(new)	Url(new)

Debug:
PRINTLOG	[X]
    Print all logs / log of process X
PRINTDB	[x]
    Print all DBs / DB of process X
DEBUG	X
    Print current state of process X

