package com.turel.spring.statemachine.tasks;

import com.turel.spring.statemachine.CustomAction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

/**
 * Generic Stage for task based operations
 * @param <S>
 */
@Getter
@Setter
@RequiredArgsConstructor
@Slf4j
abstract public class StageTask<S> extends CustomAction<TaskEvent> {
	final S state;
	final boolean isFinally;

	public StageTask(S state) {
		this.state = state;
		isFinally = false;
	}

	public Action<S, TaskEvent> actionExecute() {
		return (StateContext<S, TaskEvent> context) -> {
			try {
				final Message<TaskEvent> message = execute(context);
				if (message != null)
					context.getStateMachine().sendEvent(clone(message, TaskEvent.Next));
			} catch (Exception e) {
				log.error(e.getMessage());
				if (isFinally)
					throw e;
				if (!context.getStateMachine().sendEvent(clone(context.getMessage(), TaskEvent.Finally)))
					throw e;
			}
		};
	}

	public abstract Message<TaskEvent> execute(StateContext<S, TaskEvent> context);

}
