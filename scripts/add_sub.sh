#!/bin/bash
# --------------------------------------------------------------
# Usage
# --------------------------------------------------------------

echo "Usage:"
echo "add_sub.sh <id> <k> <amf> <op> <opc> <domain> <msisdn>"
echo
echo "Example:"
echo "add_sub.sh 001010000000001 00000001 ims.mnc001.mcc001.3ggnetwork.org fec86ba6eb707ed08905757b1bb44b8f 8000 00000000000000000000000000000000 c42449363bbad02b66d16bc975d77cc1"
echo

# --------------------------------------------------------------
# Include JAR Files
# --------------------------------------------------------------

cd /opt/OpenIMSCore/FHoSS/deploy
JAVA_HOME="/usr/lib/jvm/jdk1.7.0_80"
CLASSPATH="/usr/lib/jvm/jdk1.7.0_80/jre/lib/"
echo "Building Classpath"
CLASSPATH=$CLASSPATH:log4j.properties:.
for i in lib/*.jar; do CLASSPATH="$i":"$CLASSPATH"; done
echo "Classpath is $CLASSPATH."

# --------------------------------------------------------------
# Start-up
# --------------------------------------------------------------

$JAVA_HOME/bin/java -cp $CLASSPATH de.fhg.fokus.hss.db.AddSubsFHoSS $1 $2 $3 $4 $5 $6 $7 $8 $9
