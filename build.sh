#!/bin/bash

rm -rf test/*/pip_user.json test/*/simple_user.json
javac -cp ".:./libs/gson-2.10.1.jar" -d bin src/*.java