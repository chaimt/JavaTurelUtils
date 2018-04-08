package com.turel.spring.statemachine;

import com.turel.spring.statemachine.tasks.StageTask;
import com.turel.spring.statemachine.tasks.TaskEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;

@Configuration
@Slf4j
public class TaskActions {

    @Bean
    public StageTask<TaskStates> dataATask() {
        return new StageTask<TaskStates>(TaskStates.DataA) {
            @Override
            public Message<TaskEvent> execute(StateContext<TaskStates, TaskEvent> context) {
                log.info("run dataATask");
                return context.getMessage();
            }
        };
    }

    @Bean
    public StageTask<TaskStates> dataBTask() {
        return new StageTask<TaskStates>(TaskStates.DataB) {
            @Override
            public Message<TaskEvent> execute(StateContext<TaskStates, TaskEvent> context) {
                log.info("run dataBTask");
                return context.getMessage();
            }
        };
    }

    @Bean
    public StageTask<TaskStates> dataCTask() {
        return new StageTask<TaskStates>(TaskStates.DataC) {
            @Override
            public Message<TaskEvent> execute(StateContext<TaskStates, TaskEvent> context) {
                log.info("run dataCTask");
                return context.getMessage();
            }
        };
    }

}
