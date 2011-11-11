#!/bin/bash
java MultiRunGameServer 4700 7 p1.log p2.log 'java AwesomeAI' 'java AwesomeAI' game.log 3 '.9 .9 1 1 1' board.txt 1>server.out 2>server.err
echo "Finished running, fool."
