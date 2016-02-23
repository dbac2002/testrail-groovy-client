# testrail-groovy-client
TestRail client for Groovy.

This is a Groovy client for the http://testrail.com API (http://docs.gurock.com/testrail-api2/start).

It uses the Java bindings (http://docs.gurock.com/testrail-api2/bindings-java) and adds a DSL for accessing the TestRail API

## Building ##
The project uses Gradle and can be build with `gradle clean build`

## Run ##
The project allows to execute a Groovy script with the specific DSL directly via Gradle with 

    gradle clean run -Pexample=<PATH_TO_GROOVY_SCRIPT
    
## DSL ##

### GET requests ###
Be aware **NOT** to use any of the keywords used in the dsl, such as `user`, `plan` or `test`

     import com.github.groovyclient.TestRailClient;
    
     @BaseScript(TestRailClient)
     import groovy.transform.BaseScript;
    
     // connect to the TestRail instance
     connect '<server_url>'
    
     // and login
     login '<user>', '<password_or_key>'
    
     // prints out name of project 7
     def p = get project(7) with 'name'
     println p
    
     // prints out name and id of user 1
     def u = get user(1) with 'name', 'id'
     println u

     // prints out user with the email as json string
     def eu = get user('email@example.com')
     println eu.json()

     // gets all projects and then prints only the names
     def ps = get projects()
     println ps.json()['name']

     // get milestones of project 11 and prints the name of them
     def ms = get milestones() of project(11) with 'name'
     println ms

     // prints the title of test 568
     def te = get test(568) with 'title'
     println te

     // prints the title of test case 308
     def tc = get testcase(308) with 'title'
     println tc

     // prints the name of all priorities
     def pt = get priorities() with 'name'
     println pt

### Complex queries ###
     // get all tests of user 1 in run 13 - print out title, id and status_id
     def aT = get tests() of user(1), run(13) with 'title', 'id', 'status_id'
     println aT

     // get all passed tests of user 1 in run 13 - print out title, id, status_id 
     def pT = get tests('passed') of user(1), run(13) with 'title', 'id', 'status_id'
     println pT

     // get the progress of run 13 (all counts) - print out passed_percentage and passed_count
     // passed_percentage is a custom field, not part of the original TestRail api
     def pr = get progress() of run(13) with 'passed_percentage', 'passed_count'
     println pr
     
### delete ###
To delete any type:

    delete milestone(4)
    delete run(12)
    delete section(12)
    
### update ###
To update a type:

    update milestone(10) with ('name' : 'a new milestone name', 'description' : 'some description')
    update testcase(345) with ('title' : 'test case title')
    update run(34) with ('name' : 'new run name')

## Limitations ##
Currently the following limitations exit:
 * Still missing POST requests
 * Almost no request filters are available
 
These limitations will be removed over time.
