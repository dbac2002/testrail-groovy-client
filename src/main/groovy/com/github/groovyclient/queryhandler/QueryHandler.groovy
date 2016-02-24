package com.github.groovyclient.queryhandler

import groovy.transform.TupleConstructor

import com.github.groovyclient.EnhancedTestRailClient
import com.github.groovyclient.model.QueryObject

@TupleConstructor
abstract class QueryHandler {
	QueryObject baseQuery

	abstract def execute(EnhancedTestRailClient client, QueryObject q1 = null, QueryObject q2 = null)

	@TupleConstructor(callSuper=true, includeSuperProperties=true, includeSuperFields=true)
	public static class DefaultQueryHandler extends QueryHandler {
		def execute(EnhancedTestRailClient client, QueryObject q1, QueryObject q2) {
			client.sendGet(baseQuery.query())
		}
	}

	@TupleConstructor(callSuper=true, includeSuperProperties=true, includeSuperFields=true)
	public static class DelegateQueryHandler extends DefaultQueryHandler {
		def execute(EnhancedTestRailClient client, QueryObject q1, QueryObject q2) {
			q1.queryHandler().execute(client, q1, q2)
		}
	}

	@TupleConstructor(callSuper=true, includeSuperProperties=true, includeSuperFields=true)
	public static class FilterQueryHandler extends DefaultQueryHandler {
		def filterProperty
		def attribute = 'name'

		def execute(EnhancedTestRailClient client, QueryObject q1, QueryObject q2) {
			def all = super.execute(client, q1, q2)
			all.find { element ->
				element[attribute] == filterProperty
			}
		}
	}

	@TupleConstructor(callSuper=true, includeSuperProperties=true, includeSuperFields=true)
	public static class RunQueryHandler extends FilterQueryHandler {
		QueryObject projects

		def execute(EnhancedTestRailClient client, QueryObject q1, QueryObject q2) {
			def projects = projects.queryHandler().execute(client)
			projects.findResult { p ->
				doExecute(client, p.id, q1, q2)
			}
		}

		def doExecute(EnhancedTestRailClient client, Object id, QueryObject q1, QueryObject q2) {
			baseQuery.id = "$id"
			super.execute(client, q1, q2)
		}
	}

	@TupleConstructor(callSuper=true, includeSuperProperties=true, includeSuperFields=true)
	public static class TestOverviewHandler extends QueryHandler {
		String option

		def execute(EnhancedTestRailClient client, QueryObject user, QueryObject run) {
			if (run != null) {
				baseQuery.id = run.id ? run.id : run.queryHandler().execute(client, user, run).id

				def query = baseQuery.query()

				if (option) {
					def statuses = new QueryObject('statuses').queryHandler().execute(client, user, run)
					def id = statuses.find { it['name'] == option }.id
					query += "&status_id=$id"
				}

				def allTestsForRun = client.sendGet(query)
				if (user.id.isNumber()) {
					allTestsForRun.findAll(filterByUser.curry(user.id.toLong()))
				}
				else {
					def userObject = user.id ? user : user.queryHandler().execute(client)
					allTestsForRun.findAll(filterByUser.curry(userObject.id as Long))
				}
			}
		}

		def filterByStatus = { statusId, entry ->
			entry['status_id'] == statusId
		}

		def filterByUser = { userId, entry ->
			entry['assignedto_id'] != null && userId == entry['assignedto_id']
		}
	}

	@TupleConstructor(callSuper=true, includeSuperProperties=true, includeSuperFields=true)
	public static class ProgressStatusHandler extends DefaultQueryHandler {
		def execute(EnhancedTestRailClient client, QueryObject q1, QueryObject q2) {
			def result = q1.queryHandler().execute(client, q1, q2)
			def allCounts = result.collect() {
				if (it.key =~ /.+_count/) {
					it.key
				}
			}
			def onlyCounts = result.subMap(allCounts)
			double total = 0
			onlyCounts.each  { total += it.value }
			double passedPercentage = onlyCounts['passed_count'] / total
			onlyCounts << ['passed_percentage' : passedPercentage * 100 as int]
		}
	}

	@TupleConstructor(callSuper=true, includeSuperProperties=true, includeSuperFields=true)
	public static class TestStatusHandler extends DefaultQueryHandler {
		def execute(EnhancedTestRailClient client, QueryObject q1, QueryObject q2) {
			def testResult = q1.queryHandler().execute(client, q1, q2)
			def statuses = super.execute(client, q1, q2)
			statuses.find { status ->
				status['id'] == testResult['status_id']
			}
		}
	}
}