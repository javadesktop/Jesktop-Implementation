#! /bin/sh

java -classpath lib/picocontainer-1.2-RC-2.jar:lib/hidden/nanocontainer-1.0-RC-3.jar:lib/hidden/commons-cli-1.0.jar:lib/jesktop-api.jar:lib/jesktop-frimble.jar org.nanocontainer.Standalone -q -n -c composition.xml
