#!/bin/sh

export ANT_OPTS=-Xmx256m

export MY_PROJECT_NAME=${dbfluteProjectName}

export DBFLUTE_HOME=../mydbflute/dbflute-${behavior.getProperty("dbflute.version")}
