FROM gcr.io/ordinal-ember-163410/appengine-sumo-base
VOLUME /tmp
COPY migration-dataflow-appengine-1.0.0-SNAPSHOT.jar app.jar

ENV SUMOLOGIC_BASEDIR /opt/SumoCollector
ENV SUMOLOGIC_CONFDIR ${SUMOLOGIC_BASEDIR}/config
COPY sumologic_sources.json ${SUMOLOGIC_CONFDIR}/sumologic_sources.json

#++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
# INSTALL gcp credentials
#++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
RUN mkdir -p /opt/cto/migration-dataflow/log && mkdir -p /opt/cto/migration-dataflow/gcp/log

ENV DOCKER_BASEDIR /opt/docker
ENV DOCKER_LOGDIR  ${DOCKER_BASEDIR}/log
ENV DOCKER_SCRIPTS_DIR  ${DOCKER_BASEDIR}/scripts

CMD bash -x ${DOCKER_SCRIPTS_DIR}/startup.sh | tee -a ${DOCKER_LOGDIR}/startup.log