#!/bin/sh

BATCH_HOME="`dirname ${r"$"}0`"
JAVA=${r"$"}JAVA_HOME/bin/java

LOCALCLASSPATH_HOME=${r"$"}BATCH_HOME/../..
pushd ${r"$"}BATCH_HOME/../../.. > /dev/null
WEBAPP=`pwd`
popd > /dev/null
for i in ${r"$"}LOCALCLASSPATH_HOME/lib/h2*.jar
do
  if [ "${r"$"}i" != "${r"$"}LOCALCLASSPATH_HOME/lib/h2*.jar" ] ; then
    if [ -z "${r"$"}LOCALCLASSPATH" ] ; then
      LOCALCLASSPATH=${r"$"}i
    else
      LOCALCLASSPATH=${r"$"}i:${r"$"}LOCALCLASSPATH
    fi
  fi
done

"${r"$"}JAVA" -classpath "${r"$"}LOCALCLASSPATH" org.h2.tools.Shell -url ${database.URL?replace("%WEBAPP%", "$WEBAPP")} -user ${database.user} ${r"$"}*

