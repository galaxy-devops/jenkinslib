#!groovy

@Library('jenkinslib') _

def tools = new org.devops.tools()

pipeline{
    agent any

    options {
        timestamps()                    // log need timestamp
        skipDefaultCheckout()           // skip default checkout action
        disableConcurrentBuilds()       // no parallel
        timeout(time: 1, unit: 'HOURS') // set timeout for pipeline
    }

    stages{
        stage('use share library'){
            steps{
                timeout(time: 30, unit: "MINUTES"){
                    script{
                        print("use shared library")
                        tools.PrintMes("this is my repository")
                    }
                }
            }
        }
    }
}
