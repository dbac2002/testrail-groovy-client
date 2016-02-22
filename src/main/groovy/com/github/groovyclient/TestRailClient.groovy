package com.github.groovyclient

import com.github.groovyclient.model.QueryObject
import com.github.groovyclient.model.QueryType
import com.github.groovyclient.model.QueryObject.RunQueryObject
import com.github.groovyclient.model.QueryObject.TestQueryObject
import com.gurock.testrail.APIClient
import com.gurock.testrail.APIException


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
		new QueryObject('case', "$id")
	}

	def QueryObject milestone(int id)  {
		new QueryObject('milestone', "$id")
	}

	def QueryObject plan(int id) {
		new QueryObject('plan', "$id")
	}

	def RunQueryObject run(int id)  {
		new RunQueryObject('run', "$id")
	}

	def QueryObject section(int id)  {
		new QueryObject('section', "$id")
	}

	def TestQueryObject test(int id)  {
		new TestQueryObject('test', "$id")
	}

	def QueryObject suite(int id)  {
		new QueryObject('suite', "$id")
	}

	def QueryObject user(int id) {
		new QueryObject('user', "$id")
	}

	def QueryObject user(String email) {
		new QueryObject('user', "$email")  {
					def query(QueryType type) {
						"get_user_by_email&email=$email"
					}
				}
	}

	def QueryObject project(int id) {
		new QueryObject('project', "$id")
	}

	def QueryObject testcases(QueryObject project, QueryObject suite, QueryObject section) {
		new QueryObject('testcases'){
					def query(QueryType type){
						"get_cases/${project.id}&suite_id=${suite.id}&section_id=${section.id}"
					}
				}
	}

	def QueryObject configurations(QueryObject project) {
		new QueryObject('configs', project.id)
	}

	def QueryObject results(TestQueryObject test) {
		new QueryObject('results', test.id)
	}

	def QueryObject results(RunQueryObject run) {
		new QueryObject('results_for_run', run.id)
	}

	def QueryObject results(QueryObject testcase, QueryObject run) {
		new QueryObject('results_for_case') {
					def query(QueryType type) {
						"get_results_for_case/${run.id}/${testcase.id}"
					}
				}
	}

	def QueryObject milestones(QueryObject project, method='normal') {
		def queryMap = [
			'normal': "get_milestones/${project.id}",
			'completed': "get_milestones/${project.id}&is_completed=1",
			'not completed': "get_milestones/${project.id}&is_completed=0"
		]
		new QueryObject('plans') {
					def query(QueryType type) {
						queryMap[method]
					}
				}
	}

	def QueryObject plans(QueryObject project, method='normal') {
		def queryMap = [
			'normal': "get_plans/${project.id}",
			'completed': "get_plans/${project.id}&is_completed=1",
			'not completed': "get_plans/${project.id}&is_completed=0"
		]
		new QueryObject('plans') {
					def query(QueryType type) {
						queryMap[method]
					}
				}
	}

	def entry(int id, QueryObject plan) {
		// Only called for deleting entry in plan
		new QueryObject('plan_entry') {
					def query(QueryType type) {
						"delete_plan_entry/${plan.id}/$id"
					}
				}
	}
	def casefields = { new QueryObject('case_fields') }
	def casetypes  = { new QueryObject('case_types') }
	def priorities = { new QueryObject('priorities') }
	def projects   = { new QueryObject('projects') }

	def testcase = { 'add_case/' }
	def milestone = { 'add_milestone/' }

	def delete(QueryObject queryObject) {
		postRequest(queryObject.query(QueryType.delete))
	}

	def postRequest(String query, def dataMap=[:]) {
		try {
			client.sendPost(query, dataMap)
		}
		catch (APIException ex) {
			System.err << "POST failed: " + ex
		}
	}

	def show(QueryObject type) {
		def res = get(type)

		['name', 'title', 'id'].collect { if (res[it]) println res[it] }
	}

	def show(String... property) {
		[of: { typeDef ->
				QueryObject info = typeDef instanceof Closure ? typeDef() : typeDef
				def resp = getRequest(info.query(QueryType.get))
				property.each { println resp[it] }
			}]
	}

	def get(String... property) {
		[of: { typeDef ->
				QueryObject info = typeDef instanceof Closure ? typeDef() : typeDef
				def resp = getRequest(info.query(QueryType.get))
				property.collect { resp[it] } - null
			}]
	}

	def update(QueryObject type) {
		[with : { map ->
				postRequest(type.query(QueryType.update), map)
			}]
	}

	def getRequest(String query) {
		try {
			client.sendGet(query)
		}
		catch(APIException ex) {
			System.err << "GET failed: " + ex
		}
	}

	def get(QueryObject info) {
		getRequest(info.query(QueryType.get))
	}
}
