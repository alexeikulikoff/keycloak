#!/bin/bash

mvn -f testsuite/utils/pom.xml exec:java -e -Pkeycloak-server


