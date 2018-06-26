#!/bin/bash
set -x #echo on

exec > >(tee /tmp/start.log|logger -t user-data -s 2>/dev/console) 2>&1

HOSTNAME=`curl -s http://169.254.169.254/computeMetadata/v1beta1/instance/hostname`

SUMO_CONF_FILE="/etc/sumo.conf"
SUMO_CONF_COMP_TEMPLATE="/etc/sumo.conf.comp"

if [ -f ${SUMO_CONF_COMP_TEMPLATE} ] ; then
	sed -i 's/ //g' ${SUMO_CONF_COMP_TEMPLATE}
	mv ${SUMO_CONF_COMP_TEMPLATE} ${SUMO_CONF_FILE}
fi

sed -i "s|@@INSTANCE_ID@@|${HOSTNAME}|g" /opt/SumoCollector/config/sumologic_sources.json

/etc/init.d/collector start

java -Djava.security.egd=file:/dev/./urandom -jar /app.jar