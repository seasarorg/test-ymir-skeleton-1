@echo off

set ANT_OPTS=-Xmx256M

set MY_PROJECT_NAME=${dbfluteProjectName}

set DBFLUTE_HOME=..\mydbflute\dbflute-${behavior.getProperty("dbflute.version")}
