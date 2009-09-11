#!/bin/sh

BATCH_HOME="`dirname $0`"
JAVA=$JAVA_HOME/bin/java

if [  -d $BATCH_HOME/lib ] ; then
    LOCALCLASSPATH_HOME=$BATCH_HOME
else
    LOCALCLASSPATH_HOME=$BATCH_HOME/..
fi
for i in $BATCH_HOME/lib0/*.jar
do
  if [ "$i" != "$BATCH_HOME/lib0/*.jar" ] ; then
    if [ -z "$LOCALCLASSPATH" ] ; then
      LOCALCLASSPATH=$i
    else
      LOCALCLASSPATH=$i:$LOCALCLASSPATH
    fi
  fi
done
for i in $LOCALCLASSPATH_HOME/lib/*.jar
do
  if [ "$i" != "$LOCALCLASSPATH_HOME/lib/*.jar" ] ; then
    if [ -z "$LOCALCLASSPATH" ] ; then
      LOCALCLASSPATH=$i
    else
      LOCALCLASSPATH=$i:$LOCALCLASSPATH
    fi
  fi
done
LOCALCLASSPATH=$BATCH_HOME/classes0:$LOCALCLASSPATH_HOME/classes:$LOCALCLASSPATH

"$JAVA" -classpath "$LOCALCLASSPATH" org.seasar.ymir.batch.Bootstrap $*

