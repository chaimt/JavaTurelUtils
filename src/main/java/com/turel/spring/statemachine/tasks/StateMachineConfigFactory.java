package com.turel.spring.statemachine.tasks;

import com.turel.utils.ObjectWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.config.configurers.StateConfigurer;

import java.util.List;

/**
 * Factory to connect all tasks with same event
 * @param <S>
 */
@RequiredArgsConstructor
public class StateMachineConfigFactory<S extends Enum> {
	final S initial;
	final S end;
	final Action<S, TaskEvent> error;
	final StageTask<S> startStep;
	final StageTask<S> endStep;


	/**
	 *
	 * @param tasks
	 * @return
	 * @throws Exception
	 */
	public StateMachineBuilder.Builder<S, TaskEvent> getConfig(List<StageTask<S>> tasks) throws Exception {
		if (startStep != null)
			tasks.add(0, startStep);
		if (endStep != null)
			tasks.add(endStep);
		final StateMachineBuilder.Builder<S, TaskEvent> builder = StateMachineBuilder.builder();
		final StateConfigurer<S, TaskEvent> configurer = builder.configureStates()
				.withStates()
				.initial(initial)
				.end(this.end);

		final ObjectWrapper<StageTask<S>> lastTask = new ObjectWrapper<>();
		final ObjectWrapper<StageTask<S>> finallyTask = new ObjectWrapper<>();
		tasks.forEach(task -> {
			try {
				if (task.isFinally && finallyTask.isNull()) {
					finallyTask.setValue(task);
				}
				configurer
						.stateDo(task.state, task.actionExecute(), error);
				if (lastTask.isNull()) {
					builder.configureTransitions().withExternal().source(initial).target(task.state).event(TaskEvent.Next);
				} else {
					builder.configureTransitions().withExternal().source(lastTask.getValue().state).target(task.state)
							.event(TaskEvent.Next);
				}
				if (!lastTask.isNull() && lastTask.getValue().isFinally && task.isFinally) {
					builder.configureTransitions().withExternal().source(lastTask.getValue().state).target(task.state)
							.event(TaskEvent.Finally);
				}
				lastTask.setValue(task);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});

		//attach finally
		if (!finallyTask.isNull()) {
			final ObjectWrapper<StageTask<S>> lastNotFinallyTask = new ObjectWrapper<>();
			tasks.stream().filter(t -> !t.isFinally() && t.state != end).forEach(task -> {
				try {
					builder.configureTransitions().withExternal().source(task.state).target(finallyTask.getValue().state)
							.event(TaskEvent.Finally);
					lastNotFinallyTask.setValue(task);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		}

		return builder;
	}

}
