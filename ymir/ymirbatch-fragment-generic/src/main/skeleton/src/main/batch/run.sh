#!/bin/sh

BATCH_HOME="`dirname $0`"
JAVA=$JAVA_HOME/bin/java

for i in $BATCH_HOME/lib/*.jar
do
  if [ "$i" != "$BATCH_HOME/lib/*.jar" ] ; then
    if [ -z "$LOCALCLASSPATH" ] ; then
      LOCALCLASSPATH=$i
    else
      LOCALCLASSPATH=$i:$LOCALCLASSPATH
    fi
  fi
done
LOCALCLASSPATH=$BATCH_HOME/classes:$LOCALCLASSPATH

"$JAVA" -classpath "$LOCALCLASSPATH" org.seasar.ymir.batch.Bootstrap $*

