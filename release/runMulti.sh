#!/bin/bash
java MultiRunGameServer 4700 7 p1.log p2.log 'java AwesomeAI' 'java GreedyAI' game.log 5 '1 1 1 1 1' board.txt 1>server.out 2>server.err
echo "Finished running, fool."
