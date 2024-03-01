#!groovy
@Library('jenkinslib') _

def tools = new org.devops.tools()
String workspace = "/opt/jenkins/workspace"

//Pipeline
pipeline {
    agent {
        node {
            label "master"
            customWorkspace "${workspace}"  // specify workspace directory
        }
    }

    options {
        timestamps()    // give it timestamps
        skipDefaultCheckout()    // show checkout scm detail
        disableConcurrentBuilds()    // no parallel
        timeout(time: 1, unit: 'HOURS')    // pipeline timeout
    }

    stages {
        // retrieve code
        stage("GetCode"){    // stage1
            steps{          
                timeout(time: 5, unit: 'MINUTES'){  // set timeout
                    script{                           
                        println('get the code')
                        tools.PrintMes("Retrieve the code", 'red')
                    }
                }
            }
        }

        // building
        stage("Build"){
            steps{
                timeout(time: 20, unit: 'MINUTES'){
                    script{
                        println('packet')
                        tools.PrintMes("packet the code", 'green1')
                    }
                }
            }
        }

        // code scan
        stage("CodeScan"){
            steps{
                timeout(time: 30, unit: 'MINUTES'){
                    script{
                        println("scan code")
                        tools.PrintMes("this is my ansiColor test", 'green')
                    }
                }
            }
        }
    }
       //post building
       post {
            always {
                script{
                    println("always")
                }
            }

            success {
                script{
                    currentBuild.description = "\n building successfully~"
                }
            }

            failure {
                script{
                    currentBuild.description = "\n building failed~"
                }
            }

            aborted {
                script{
                    currentBuild.description = "\n building aborting~"
                }
            }
       }
    }
