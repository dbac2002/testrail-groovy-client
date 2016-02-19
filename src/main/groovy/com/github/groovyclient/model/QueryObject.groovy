package com.github.groovyclient.model

import groovy.transform.ToString
import groovy.transform.TupleConstructor

@ToString(includeNames=true)
class QueryObject {
	String name
	String id

	QueryObject(name, id="") {
		this.name = name;
		this.id = id
	}

	def query(QueryType type) {
		"${type}_$name/$id"
	}

	@TupleConstructor(callSuper=true, includeSuperProperties=true, includeSuperFields=true)
	static class RunQueryObject extends QueryObject {
	}

	@TupleConstructor(callSuper=true, includeSuperProperties=true, includeSuperFields=true)
	static class TestQueryObject extends QueryObject {
	}
}
