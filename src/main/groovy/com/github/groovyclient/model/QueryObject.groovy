package com.github.groovyclient.model

import groovy.transform.ToString
import groovy.transform.TupleConstructor

import com.github.groovyclient.queryhandler.QueryHandler
import com.github.groovyclient.queryhandler.QueryHandler.DefaultQueryHandler

@ToString(includeNames=true)
class QueryObject {
	String name = ""
	String id = ""
	String link = ""

	QueryObject(name, id="", link="") {
		this.name = name;
		this.id = id
		this.link = link
	}

	def query(QueryType type=QueryType.get) {
		"${type}_$name/$id"
	}

	QueryHandler queryHandler() {
		return new DefaultQueryHandler(this)
	}

	@TupleConstructor(callSuper=true, includeSuperProperties=true, includeSuperFields=true)
	static class RunQueryObject extends QueryObject {
	}

	@TupleConstructor(callSuper=true, includeSuperProperties=true, includeSuperFields=true)
	static class TestQueryObject extends QueryObject {
	}
}
