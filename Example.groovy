import com.github.groovyclient.TestRailClient;

@BaseScript(TestRailClient)
import groovy.transform.BaseScript;

connect '<server_url>'
login '<user>', '<password_or_key>'

// show the status_ids for every result of test #478
show('status_id') of results(test(478))

// show status_ids for every result of test case #93 in run #12
show('status_id') of results(testcase(93), run(12))

// show status_ids for every result of run #12
show('status_id') of results(run(12))

// show name of all projects
show('name') of projects

// get user #7 and print it
def user1 = get user(1)
println user1

// show name of user #7
show('name') of user(7)

// show name of user with email 'example@example.com'
show('name') of user('example@example.com')

// show name of project #7
show('name') of project(7)

// show name of milestone #3
show('name') of milestone(3)

// show name of plan #15
show('name') of plan(15)

// show name of test plans for project #11
show('name') of plans(project(11))

// show name of run #14
show('name') of run(14)

// show name of section #122
show('name') of section(122)

// show title of test #590
show('title') of test(590)

// show name of suite #14
show('name') of suite(14)

// show title of testcase #308
show('title') of testcase(308)

// show ids of all testcases in project #11, suite #14 and section #122
show('id') of testcases(project(11), suite(14), section(122))

// show the names of all casefields
show('name') of casefields

// show names of all casetypes
show('name') of casetypes

// show the confis of all configurations of project #11
show('configs') of configurations(project(11))

// show the names of all completed milestones in project #7
show('name') of milestones(project(7), 'completed')