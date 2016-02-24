package com.github.groovyclient

import com.github.groovyclient.model.QueryObject
import com.github.groovyclient.model.QueryType
import com.github.groovyclient.model.QueryObject.RunQueryObject
import com.github.groovyclient.model.QueryObject.TestQueryObject
import com.github.groovyclient.queryhandler.QueryHandler
import com.github.groovyclient.queryhandler.QueryHandler.DelegateQueryHandler
import com.github.groovyclient.queryhandler.QueryHandler.FilterQueryHandler
import com.github.groovyclient.queryhandler.QueryHandler.ProgressStatusHandler
import com.github.groovyclient.queryhandler.QueryHandler.RunQueryHandler
import com.github.groovyclient.queryhandler.QueryHandler.TestOverviewHandler
import com.github.groovyclient.queryhandler.QueryHandler.TestStatusHandler
import com.gurock.testrail.APIClient
import com.gurock.testrail.APIException

abstract class EnhancedTestRailClient extends Script {
	private APIClient client
	private String url

	def connect(String url) {
		this.client = new APIClient(url)
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
		[with: { dataMap ->
				sendPost(type.query(QueryType.update), dataMap)
			}]
	}

	def get(QueryObject action) {
		def actionQueryHandler = action.queryHandler()

		[with: { display, ...displays ->
				def result = actionQueryHandler.execute(this)
				new QueryResultHandler(result, display, displays).handleResult()
			},
			of: { QueryObject additional1, QueryObject additional2=null  ->
				[with: { display, ...displays ->
						def result = actionQueryHandler.execute(this, additional1, additional2)
						new QueryResultHandler(result, display, displays).handleResult()
					},
					json: {
						actionQueryHandler.execute(this, additional1, additional2)
					}]
			},
			json: { actionQueryHandler.execute(this) }]
	}

	def results() {
		new QueryObject('results') {
					QueryHandler queryHandler() {
						new DelegateQueryHandler(this)
					}
				}
	}

	def status() {
		new QueryObject('statuses') {
					QueryHandler queryHandler() {
						new TestStatusHandler(this)
					}
				}
	}

	def progress() {
		new QueryObject('run') {
					QueryHandler queryHandler() {
						new ProgressStatusHandler(this)
					}
				}
	}

	def milestones() {
		new QueryObject('milestones') {
					QueryHandler queryHandler() {
						new DelegateQueryHandler(this)
					}
				}
	}

	def plans() {
		new QueryObject('plans') {
					QueryHandler queryHandler() {
						new DelegateQueryHandler(this)
					}
				}
	}

	def QueryObject projects() {
		new QueryObject('projects', "", "/index.php?/projects/overview/")
	}

	def String baseurl() {
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
		new QueryObject('tests') {
					QueryHandler queryHandler() {
						new TestOverviewHandler(this, option)
					}
				}
	}

	def configurations() {
		new QueryObject('configs') {
					QueryHandler queryHandler() {
						new DelegateQueryHandler(this)
					}
				}
	}

	def QueryObject casefields() {
		new QueryObject('case_fields')
	}

	def QueryObject casetypes () {
		new QueryObject('case_types')
	}

	def QueryObject priorities (){
		new QueryObject('priorities')
	}

	def QueryObject test(int id) {
		new TestQueryObject('test', "$id", '/index.php?/tests/view/')
	}

	def QueryObject user(String email) {
		if (email.contains('@')) {
			return new QueryObject('user', "$email")  {
						def query(QueryType type) {
							"get_user_by_email&email=$email"
						}
					}
		}
		new QueryObject('users') {
					QueryHandler queryHandler() {
						new FilterQueryHandler(this, email)
					}
				}
	}

	def QueryObject project(String name) {
		new QueryObject('projects') {
					QueryHandler queryHandler() {
						new FilterQueryHandler(this, name)
					}
				}
	}

	def QueryObject user(int id) {
		new QueryObject('user', "$id")
	}

	def QueryObject run(String title) {
		new QueryObject('runs') {
					QueryHandler queryHandler() {
						new RunQueryHandler(this, title, 'name', projects())
					}
				}
	}

	def QueryObject run(int id) {
		new RunQueryObject('run', "$id")
	}

	def QueryObject project(int id) {
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
