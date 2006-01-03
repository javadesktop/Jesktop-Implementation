#!/bin/bash

# NanoContainer Booter script v 1.0-RC-3
# www.nanocontainer.org

EXEC="$JAVA_HOME/bin/java -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -Djava.security.manager -Djava.security.policy=file:lib/booter.policy -jar lib/nanocontainer-booter-1.0-RC-3.jar -c composition.groovy"
echo $EXEC
$EXEC

# -q -n -c composition.groovy