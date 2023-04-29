#!/bin/bash

# java -cp bin src.Main $1 $2 $3
java -cp ".:./libs/gson-2.10.1.jar:.:./bin" src.Main $1 $2 $3