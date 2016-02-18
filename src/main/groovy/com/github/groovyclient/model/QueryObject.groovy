package com.github.groovyclient.model

import groovy.transform.TupleConstructor

@TupleConstructor(includeFields=true)
class QueryObject {
	String query
	String id

	@TupleConstructor(callSuper=true, includeSuperProperties=true, includeSuperFields=true)
	static class RunQueryObject extends QueryObject {
	}

	@TupleConstructor(callSuper=true, includeSuperProperties=true, includeSuperFields=true)
	static class TestQueryObject extends QueryObject {
	}
}
