#!/bin/bash
java GameServer 4700 7 p1.log p2.log 'java AwesomeAI 1.0 1.032151163492742 1.0 0.7051117 8.143152 1.0 1.0' 'java AwesomeAI 1 2 1 1 9 1 1' game.log 1>server.out 2>server.err
echo "Finished running, fool."
