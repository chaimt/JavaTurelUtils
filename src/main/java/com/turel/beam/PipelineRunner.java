package com.turel.beam;

import com.turel.utils.CatchRunnable;
import com.turel.utils.LogUtils;
import io.vavr.Tuple2;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public interface PipelineRunner {
	class PipelineStatus{
		String status;
		String jobName;

		public String getStatus() {
			return status;
		}

		public String getJobName() {
			return jobName;
		}
	}

	String AlreadyRunning = "already_running";

	PipelineStatus runPipeline(String[] args);

	default boolean skipChecks(){
		return false;
	}

	default PipelineStatus runPipeline(String appPrefix, Logger LOG, String appName, DataFlowUtils.JobRunOptions options,
                                       String bqProject, String jobLabel, CatchRunnable pipelineRun) {
		LogUtils.appPrefix.set(appPrefix);
		LOG.info(LogUtils.prefixLog("start processing"));
		LOG.info(LogUtils.prefixLog("user: " + DataFlowUtils.getUserName()));

		Tuple2<String, String> jobNameMainSync = DataFlowUtils
				.buildJobName(bqProject, appName, options.getJobName(), jobLabel);
		options.setJobName(jobNameMainSync._2);
		LOG.info(LogUtils.prefixLog("jobName=" + options.getJobName()));
		LOG.info(LogUtils.prefixLog("check if job running"));
		PipelineStatus ps = new PipelineStatus();

        final String jobRunning = DataFlowUtils.canJobRun(options, jobNameMainSync._1, options.getQuitePeriod(), LOG).get();
		if (!options.getDevMode() && !skipChecks() && StringUtils.isNotEmpty(jobRunning)) {
			ps.status = AlreadyRunning;
			ps.jobName = jobRunning;
			return ps;
		}
		try {
			LOG.info(LogUtils.prefixLog("jobName=" + options.getJobName()));
			pipelineRun.run();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		ps.jobName = options.getJobName();
		return ps;

	}

}
