#!/bin/bash

mvn -Pdistribution -pl distribution/server-dist -am -Dmaven.test.skip clean install


