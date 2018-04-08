package com.turel.spring.statemachine;

import com.turel.spring.statemachine.tasks.StageTask;
import com.turel.spring.statemachine.tasks.StateMachineConfigFactory;
import com.turel.spring.statemachine.tasks.TaskEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.state.State;

import java.util.*;

@Configuration
@EnableStateMachine
@Slf4j
public class SFMStateMachineConfiguration {

	@Autowired StageTask<TaskStates> dataATask;
	@Autowired StageTask<TaskStates> dataBTask;
	@Autowired StageTask<TaskStates> dataCTask;

	/**
	 * email on error
	 * @return stage
	 */
	@Bean
	public Action<TaskStates, TaskEvent> handleError() {
		return (StateContext<TaskStates, TaskEvent> context) -> {
			try {
				//send error email
				log.error(context.getException().toString());
			} finally {
				context.getStateMachine().stop();
			}
		};
	}

	public StageTask<TaskStates> endStep(TaskStates state) {
		return new StageTask<TaskStates>(state) {
			@Override
			public Message<TaskEvent> execute(StateContext<TaskStates, TaskEvent> context) {
				context.getStateMachine().stop();
				return null;
			}
		};
	}

	@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	@Bean("drawData")
	public StateMachine<TaskStates, TaskEvent> drawData() throws Exception {

		StateMachineConfigFactory<TaskStates> smf = new StateMachineConfigFactory<>(TaskStates.DataIDLE,
				TaskStates.DataFinish, handleError(), null, endStep(TaskStates.DataFinish));
		List<StageTask<TaskStates>> tasks = new ArrayList<>();
		tasks.add(dataATask);
		tasks.add(dataBTask);
		tasks.add(dataCTask);

		final StateMachineBuilder.Builder<TaskStates, TaskEvent> builder = smf.getConfig(tasks);

		builder.configureConfiguration()
				.withConfiguration()
				.autoStartup(true)
				.taskExecutor(new SyncTaskExecutor());

		return builder.build();
	}



	public static String stateToString(State state) {
		return Optional.ofNullable(state).map(State::getId).map(Object::toString).orElse("");
	}

}
