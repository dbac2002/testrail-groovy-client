package com.github.groovyclient

import com.github.groovyclient.model.QueryObject
import com.github.groovyclient.model.QueryObject.RunQueryObject
import com.github.groovyclient.model.QueryObject.TestQueryObject
import com.gurock.testrail.APIClient

abstract class TestRailClient extends Script {
	private APIClient client

	def connect(url) {
		client = new APIClient(url)
	}

	def login(user, pass) {
		client.m_user = user
		client.m_password = pass
	}

	def QueryObject testcase(int id)  {
		new QueryObject("get_case/$id", "$id")
	}

	def QueryObject milestone(int id)  {
		new QueryObject("get_milestone/$id", "$id")
	}

	def QueryObject plan(int id) {
		new QueryObject("get_plan/$id", "$id")
	}

	def RunQueryObject run(int id)  {
		new RunQueryObject("get_run/$id", "$id")
	}

	def QueryObject section(int id)  {
		new QueryObject("get_section/$id", "$id")
	}

	def TestQueryObject test(int id)  {
		new TestQueryObject("get_test/$id", "$id")
	}

	def QueryObject suite(int id)  {
		new QueryObject("get_suite/$id", "$id")
	}

	def QueryObject user(int id) {
		new QueryObject("get_user/$id", "$id")
	}

	def QueryObject user(String email) {
		new QueryObject("get_user_by_email&email=$email", email)
	}

	def QueryObject project(int id) {
		new QueryObject("get_project/$id", "$id")
	}

	def QueryObject testcases(QueryObject project, QueryObject suite, QueryObject section) {
		new QueryObject("get_cases/${project.id}&suite_id=${suite.id}&section_id=${section.id}")
	}

	def QueryObject configurations(QueryObject project) {
		new QueryObject("get_configs/${project.id}")
	}

	def QueryObject results(TestQueryObject test) {
		new QueryObject("get_results/${test.id}")
	}

	def QueryObject results(RunQueryObject run) {
		new QueryObject("get_results_for_run/${run.id}")
	}

	def QueryObject results(QueryObject testcase, QueryObject run) {
		new QueryObject("get_results_for_case/${run.id}/${testcase.id}")
	}

	def QueryObject milestones(QueryObject project, method='normal') {
		def queryMap = [
			'normal': "get_milestones/${project.id}",
			'completed': "get_milestones/${project.id}&is_completed=1",
			'not completed': "get_milestones/${project.id}&is_completed=0"
		]
		new QueryObject(queryMap[method])
	}

	def QueryObject plans(QueryObject project, method='normal') {
		def queryMap = [
			'normal': "get_plans/${project.id}",
			'completed': "get_plans/${project.id}&is_completed=1",
			'not completed': "get_plans/${project.id}&is_completed=0"
		]
		new QueryObject(queryMap[method])
	}

	def casefields = { new QueryObject('get_case_fields') }
	def casetypes  = { new QueryObject('get_case_types') }
	def priorities = { new QueryObject('get_priorities') }
	def projects   = { new QueryObject('get_projects') }

	def show(property) {
		[of: { typeDef ->
				QueryObject info = typeDef instanceof Closure ? typeDef() : typeDef
				println getRequest(info.query)[property]
			}]
	}

	def getRequest(String query) {
		client.sendGet(query)
	}

	def get(QueryObject info) {
		getRequest(info.query)
	}
}
