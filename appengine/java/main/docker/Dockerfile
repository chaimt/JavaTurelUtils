FROM gcr.io/ordinal-ember-163410/appengine-sumo-base
VOLUME /tmp
ADD migration-dataflow-appengine-1.0.0-SNAPSHOT.jar app.jar

ENV SUMOLOGIC_BASEDIR /opt/SumoCollector
ENV SUMOLOGIC_CONFDIR ${SUMOLOGIC_BASEDIR}/config
ADD sumologic_sources.json ${SUMOLOGIC_CONFDIR}/sumologic_sources.json

#++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
# INSTALL gcp credentials
#++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
RUN mkdir -p /opt/company/cto/migration-dataflow/log

ENV DOCKER_BASEDIR /opt/docker
ENV DOCKER_LOGDIR  ${DOCKER_BASEDIR}/log
ENV DOCKER_SCRIPTS_DIR  ${DOCKER_BASEDIR}/scripts

CMD bash -x ${DOCKER_SCRIPTS_DIR}/startup.sh | tee -a ${DOCKER_LOGDIR}/startup.log