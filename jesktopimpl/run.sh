#!/bin/bash

# NanoContainer Booter script v 1.0-RC-3
# www.nanocontainer.org

EXEC="$JAVA_HOME/bin/java -Djava.security.manager -Djava.security.policy=file:lib/booter.policy -jar lib/nanocontainer-booter-1.0-RC-3.jar -q -n -c composition.xml"
echo $EXEC
$EXEC


