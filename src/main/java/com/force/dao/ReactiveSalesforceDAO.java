package com.force.dao;

import com.force.api.ForceApi;
import com.force.api.QueryResult;
import com.force.exception.NonUniqueResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class ReactiveSalesforceDAO {

	protected final ForceApi forceApi;

	/**
	 * get number of records in table given select
	 * @param query
	 * @param tableName
	 * @return
	 */
	public Mono<Integer> findAggregate(String query, String tableName) {
		return Mono.create(monoSink -> {
			try {
				QueryResult<Map> res = forceApi.query(query);
				if (res.getRecords().size() > 1) {
					throw new NonUniqueResultException(query);
				}
				Integer resValue = (Integer) res.getRecords().get(0).get("expr0");
				monoSink.success(resValue);
			} catch (Throwable t) {
				monoSink.error(t);
			}
		});
	}

	public <T> Flux<T> find(String query, Class<T> type, String tableName) {
		return Flux.create(fluxSink -> {
			try {
				int count = 0;
				QueryResult<T> res = forceApi.query(query, type);
				for (T record : res.getRecords()) {
					fluxSink.next(record);
					count++;
				}
				while (!res.isDone() && !fluxSink.isCancelled()) {
					res = forceApi.queryMore(res.getNextRecordsUrl(), type);
					for (T record : res.getRecords()) {
						fluxSink.next(record);
						count++;
					}
				}
				fluxSink.complete();
			} catch (Throwable t) {
				fluxSink.error(t);
			}
		});
	}

	public <T> Mono<Optional<T>> findOne(String query, Class<T> type, String tableName) {
		return Mono.create(monoSink -> {
			try {
				QueryResult<T> res = forceApi.query(query, type);
				if (res.getRecords().size() > 1) {
					throw new NonUniqueResultException(query);
				}
				monoSink.success(res.getRecords().isEmpty() ? Optional.empty() : Optional.of(res.getRecords().get(0)));
			} catch (Throwable t) {
				monoSink.error(t);
			}
		});
	}

	public Mono<Void> delete(String id, String tableName) {
		return Mono.create(monoSink -> {
			try {
				forceApi.deleteSObject(tableName, id);
				monoSink.success();
			} catch (Throwable t) {
				monoSink.error(t);
			}
		});
	}

	public <T> Mono<Void> update(T sObject, String id, String tableName) {
		return Mono.create(monoSink -> {
			try {
				forceApi.updateSObject(tableName, id, sObject);
				monoSink.success();
			} catch (Throwable t) {
				monoSink.error(t);
			}
		});
	}

	public <T> Mono<String> create(T objectToCreate, String tableName) {
		return Mono.create(monoSink -> {
			try {
				String id = forceApi.createSObject(tableName, objectToCreate);
				monoSink.success(id);
			} catch (Throwable t) {
				monoSink.error(t);
			}
		});
	}

}
