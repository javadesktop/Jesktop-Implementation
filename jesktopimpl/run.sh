#! /bin/sh

java -classpath lib/picocontainer-1.1-beta-1.jar:lib/nanocontainer-1.0-beta-2.jar:lib/commons-cli-1.0.jar:lib/jesktop-api.jar:lib/jesktop-frimble.jar org.nanocontainer.main.Standalone -q -n -c composition.xml