#!/bin/bash
java GameServer 4700 7 p1.log p2.log 'java AwesomeAI 1 1 1 1 1' 'java GreedyAI' game.log board.txt 1>server.out 2>server.err
echo "Finished running, fool."
