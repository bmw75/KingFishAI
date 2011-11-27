#!/bin/bash
java MultiRunGameServer 4700 7 p1.log p2.log 'java AwesomeAI' 'java AwesomeAI' game.log 10 '1.0 1.032151163492742 1.0 0.7051117 8.143152 1.0 1.0' 1>server.out 2>server.err
echo "Finished running, fool."
