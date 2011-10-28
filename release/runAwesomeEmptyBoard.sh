#!/bin/bash
java GameServer 4700 7 p1.log p2.log 'java AwesomeAI' 'java GreedyAI' game.log 1>server.out 2>server.err
echo "Finished running, fool."
