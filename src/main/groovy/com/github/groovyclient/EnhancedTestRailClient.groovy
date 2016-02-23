package com.github.groovyclient

import com.github.groovyclient.model.QueryObject
import com.github.groovyclient.model.QueryType
import com.github.groovyclient.model.QueryObject.RunQueryObject
import com.github.groovyclient.model.QueryObject.TestQueryObject
import com.github.groovyclient.queryhandler.QueryHandler
import com.github.groovyclient.queryhandler.QueryHandler.ConfigurationsHandler
import com.github.groovyclient.queryhandler.QueryHandler.MilestonesHandler
import com.github.groovyclient.queryhandler.QueryHandler.PlansHandler
import com.github.groovyclient.queryhandler.QueryHandler.ProgressStatusHandler
import com.github.groovyclient.queryhandler.QueryHandler.TestOverviewHandler
import com.github.groovyclient.queryhandler.QueryHandler.TestResultHandler
import com.github.groovyclient.queryhandler.QueryHandler.TestStatusHandler
import com.gurock.testrail.APIClient
import com.gurock.testrail.APIException



abstract class EnhancedTestRailClient extends Script {
	private APIClient client
	private String url
	private QueryHandler testQueryHandler

	def connect(String url) {
		client = new APIClient(url)
		this.url = url
	}

	def login(String user, String pass) {
		client.m_user = user
		client.m_password = pass
	}

	def delete(QueryObject type) {
		sendPost(type.query(QueryType.delete))
	}

	def sendPost(query, dataMap = [:]) {
		try {
			client.sendPost(query, dataMap)
		}
		catch(APIException e) {
			System.err << "POST failed: " + e
		}
	}

	def update(QueryObject type) {
		[with: { dataMap -> sendPost(type.query(QueryType.update), dataMap)	}]
	}

	def get(action) {
		[with: { display, ...displays ->
				def res = sendGet(action.query())
				new QueryResultHandler(res, display, displays).handleResult(action, baseurl())
			},
			of: { QueryObject... t ->
				[with: { display, ...displays ->
						def r = testHandler().execute(this, t.flatten())
						new QueryResultHandler(r, display, displays).handleResult(t)
					},
					json: {
						testHandler().execute(this, t.flatten())
					}]
			},
			json: { sendGet(action.query()) }]
	}

	def testHandler() {
		testQueryHandler
	}

	def status() {
		testQueryHandler = new TestStatusHandler()
	}

	def results() {
		testQueryHandler= new TestResultHandler()
	}

	def progress() {
		testQueryHandler = new ProgressStatusHandler()
	}

	def milestones() {
		testQueryHandler = new MilestonesHandler()
	}

	def plans() {
		testQueryHandler = new PlansHandler()
	}

	def projects() {
		new QueryObject('projects', "", "/index.php?/projects/overview/")
	}

	def baseurl() {
		url
	}

	def sendGet(String query) {
		try {
			client.sendGet(query)
		}
		catch(APIException e) {
			System.err << "GET failed: " + e
		}
	}

	def tests(String option="") {
		testQueryHandler = new TestOverviewHandler(option)
	}

	def configurations() {
		testQueryHandler = new ConfigurationsHandler()
	}

	def casefields() {
		new QueryObject('case_fields')
	}

	def casetypes () {
		new QueryObject('case_types')
	}

	def priorities (){
		new QueryObject('priorities')
	}

	def test(int id) {
		new TestQueryObject('test', "$id", '/index.php?/tests/view/')
	}

	def statuses = new QueryObject('statuses')

	def user(String email) {
		new QueryObject('user', "$email")  {
					def query(QueryType type) {
						"get_user_by_email&email=$email"
					}
				}
	}

	def user(int id) {
		new QueryObject('user', "$id")
	}

	def run(int id) {
		new RunQueryObject('run', "$id")
	}

	def project(int id) {
		new QueryObject('project', "$id")
	}

	def QueryObject suite(int id)  {
		new QueryObject('suite', "$id")
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

	def QueryObject section(int id)  {
		new QueryObject('section', "$id")
	}
}
