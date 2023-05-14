#!/bin/bash
# --------------------------------------------------------------
# Usage
# --------------------------------------------------------------

echo "Usage:"
echo "add_sub.sh"
echo
echo "Example:"
echo "add_sub_json.sh"
echo 
echo "subscribers.json:"
echo '{"subscribers":
    [
        {"id":"001010000000020", "msisdn": "00000020", "domain": "ims.mnc001.mcc001.3ggnetwork.org", "k": "fec86ba6eb707ed08905757b1bb44b8f", "amf": "8000", "op": "00000000000000000000000000000000", "opc": "c42449363bbad02b66d16bc975d77cc1"},
        {"i d":"001010000000021", "msisdn": "00000021", "domain": "ims.mnc001.mcc001.3ggnetwork.org", "k": "fec86ba6eb707ed08905757b1bb44b8f", "amf": "8000", "op": "00000000000000000000000000000000", "opc": "c42449363bbad02b66d16bc975d77cc1"},
        {"id":"001010000000022", "msisdn": "00000022", "domain": "ims.mnc001.mcc001.3ggnetwork.org", "k": "fec86ba6eb707ed08905757b1bb44b8f", "amf": "8000", "op": "00000000000000000000000000000000", "opc": "c42449363bbad02b66d16bc975d77cc1"}
    ]
}'
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

$JAVA_HOME/bin/java -cp $CLASSPATH de.fhg.fokus.hss.db.AddSubsJsonFHoSS $1 $2 $3 $4 $5 $6 $7 $8 $9
