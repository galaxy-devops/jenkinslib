package org.devops

//building
def Build(buildType,buildShell){

    /**
     * @param   buildType       the value from the choice parameter in jenkins
     * @param   buildShell      the command from the jenkins
     */

    def buildTools=["mvn":"M2","ant":"ANT","gradle":"GRADLE","npm":"NPM"]

    println("The type of building is ${buildType}")

    buildHome = tool buildTools[buildType]

    if ("${buildType}" == "npm"){
        sh """
            export NODE_HOME=${buildHome}
            export PATH=\$NODE_HOME/bin:\$PATH
            ${buildHome}/bin/${buildType} ${buildShell}
           """
    }else{
        sh "${buildHome}/bin/${buildType} ${buildShell}"
    }
}