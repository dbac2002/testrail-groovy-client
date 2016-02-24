package com.github.groovyclient.queryhandler

import groovy.transform.TupleConstructor

import com.github.groovyclient.EnhancedTestRailClient
import com.github.groovyclient.model.QueryObject



interface QueryHandler {
	def execute(EnhancedTestRailClient client, List<QueryObject> queryObject)

	public static class UserNameHandler implements QueryHandler {
		def execute(EnhancedTestRailClient client, List<QueryObject> queryObject) {
			def allUsers = client.sendGet(users().query())
			allUsers.find { user ->
				user.name == queryObject[0].id
			}
		}

		def users() {
			new QueryObject('users')
		}
	}

	public static class ConfigurationsHandler implements QueryHandler {
		def execute(EnhancedTestRailClient client, List<QueryObject> queryObject) {
			client.sendGet(configsForProject(queryObject[0].id).query())
		}

		def configsForProject(projectId) {
			new QueryObject('configs', "$projectId")
		}
	}

	public static class PlansHandler implements QueryHandler {
		def execute(EnhancedTestRailClient client, List<QueryObject> queryObject) {
			client.sendGet(plansForProject(queryObject[0].id).query())
		}

		def plansForProject(projectId) {
			new QueryObject('plans', "$projectId")
		}
	}

	public static class MilestonesHandler implements QueryHandler {
		def execute(EnhancedTestRailClient client, List<QueryObject> queryObject) {
			client.sendGet(milestonesForProject(queryObject[0].id).query())
		}

		def milestonesForProject(projectId) {
			new QueryObject('milestones', "$projectId")
		}
	}

	@TupleConstructor
	public static class TestOverviewHandler implements QueryHandler {
		def option

		def execute(EnhancedTestRailClient client, List<QueryObject> queryObject) {
			QueryObject run = queryObject[1]
			if (run != null) {
				QueryObject user = queryObject[0]
				QueryObject tests = new QueryObject('tests', "${run.id}")
				def allTestsForRun = client.sendGet(tests.query())
				def onlyUserTests = []
				if (user.id.isNumber()) {
					onlyUserTests = allTestsForRun.findAll(filterByUser.curry(user.id.toLong()))
				}
				else {
					def userObject = client.sendGet(user.query())
					onlyUserTests = allTestsForRun.findAll(filterByUser.curry(userObject['id'] as Long))
				}

				if (option) {
					def statuses = client.sendGet(client.statuses.query())
					def id = statuses.find { it['name'] == option }.id
					onlyUserTests = onlyUserTests.findAll(filterByStatus.curry(id))
				}
				onlyUserTests
			}
		}

		def filterByStatus = { statusId, entry ->
			entry['status_id'] == statusId
		}

		def filterByUser = { userId, entry ->
			entry['assignedto_id'] != null && userId == entry['assignedto_id']
		}
	}

	public static class ProgressStatusHandler implements QueryHandler {
		def execute(EnhancedTestRailClient client, List<QueryObject> queryObject) {
			def runResult = client.sendGet(queryObject[0].query())
			def allCounts = runResult.collect() {
				if (it.key =~ /.+_count/) {
					it.key
				}
			}
			def onlyCounts = runResult.subMap(allCounts)
			double total = 0
			onlyCounts.each  { total += it.value }
			double passedPercentage = onlyCounts['passed_count'] / total
			onlyCounts << ['passed_percentage' : passedPercentage * 100 as int]
		}

		def resultsForRun(runId) {
			new QueryObject('results_for_run', "$runId")
		}
	}

	public static class TestStatusHandler implements QueryHandler {
		def execute(EnhancedTestRailClient client, List<QueryObject> queryObject) {
			def testResult = client.sendGet(queryObject[0].query())
			def statuses = client.sendGet(statuses().query())
			statuses.find { status ->
				status['id'] == testResult['status_id']
			}
		}

		def statuses = { new QueryObject('statuses') }
	}

	public static class TestResultHandler implements QueryHandler {
		def execute(EnhancedTestRailClient client, List<QueryObject> queryObject) {
			client.sendGet(results(queryObject[0].id).query())
		}

		def results(testId)  {
			new QueryObject('results', "$testId")
		}
	}
}
