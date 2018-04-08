package com.turel.spring.statemachine.tasks;

import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Optional;

@Component
public class StateMachineMonitor {
	private HashMap<String, WeakReference<StateMachine>> machines = new HashMap<>();

	public boolean isMachineRunning(final String id) {
		return Optional.ofNullable(machines.get(id))
				.map(WeakReference::get)
				.map(s -> !s.isComplete())
				.orElse(false);
	}

	public void addMachine(String id, StateMachine machine) {
		machines.put(id, new WeakReference<>(machine));
	}

	public Optional<StateMachine> getMachine(final String id) {
		return Optional.ofNullable(machines.get(id))
				.map(WeakReference::get);

	}
}
