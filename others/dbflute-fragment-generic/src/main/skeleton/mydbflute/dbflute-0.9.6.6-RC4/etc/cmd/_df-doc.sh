#!/bin/sh

NATIVE_PROPERTIES_PATH=$1

sh $DBFLUTE_HOME/etc/cmd/_df-copy-properties.sh $NATIVE_PROPERTIES_PATH

sh $DBFLUTE_HOME/etc/cmd/_df-copy-extlib.sh

sh $DBFLUTE_HOME/ant/bin/ant -Ddfenv=$DBFLUTE_ENVIRONMENT_TYPE -f $DBFLUTE_HOME/build-torque.xml doc

sh $DBFLUTE_HOME/etc/cmd/_df-delete-extlib.sh
