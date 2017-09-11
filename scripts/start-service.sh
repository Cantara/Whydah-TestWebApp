#!/bin/sh
nohup /usr/bin/java -DIAM_MODE=TEST -DIAM_CONFIG=/home/TestWebApp/testwebapp.TEST.properties -jar /home/TestWebApp/TestWebApp.jar
