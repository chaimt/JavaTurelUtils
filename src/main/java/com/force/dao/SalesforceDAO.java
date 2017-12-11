package com.force.dao;

import com.force.api.ForceApi;
import com.force.api.QueryResult;
import com.force.exception.NonUniqueResultException;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class SalesforceDAO {

	protected final ForceApi forceApi;

	public <T> Stream<T> find(String query, Class<T> type, String tableName) {
		List<T> records = new LinkedList<>();
		QueryResult<T> res = forceApi.query(query, type);
		while (!res.isDone()) {
			res = forceApi.queryMore(res.getNextRecordsUrl(), type);
			records.addAll(res.getRecords());
		}
		return records.stream();
	}

	public <T> Optional<T> findOne(String query, Class<T> type, String tableName) {
		QueryResult<T> res = forceApi.query(query, type);
		if (res.getRecords().size() > 1) {
			throw new NonUniqueResultException(query);
		}
		return res.getRecords().isEmpty() ? Optional.empty() : Optional.of(res.getRecords().get(0));
	}

	public void delete(String id, String tableName) {
		forceApi.deleteSObject(tableName, id);
	}

	public <T> void update(T sObject, String id, String tableName) {
		forceApi.updateSObject(tableName, id, sObject);
	}

	public <T> String create(T objectToCreate, String tableName) {
		return forceApi.createSObject(tableName, objectToCreate);
	}
}
