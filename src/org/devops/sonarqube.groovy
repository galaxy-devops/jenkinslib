package org.devops

def SonarScan(sonarServer,projectName,projectDesc,projectPath){
    
    /**
     * @param sonarServer defined environment variable from Jenkins
     * @param projectName project name
     * @param projectDesc project description
     * @param projectPath project path where the endpoint of you project
     */

    // define server list in case test or prod situation
    def servers = ["test":"sonarqube-test","prod":"sonarqube-prod"]
    
    withSonarQubeEnv("${servers[sonarServer]}"){
        def scannerHome = "/usr/local/buildtools/sonar-scanner"
        def sonarDate = sh returnStdout: true, script: 'date +%Y%m%d%H%M%S'
        sonarDate = sonarDate - "\n"
        sh """
            ${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=${projectName} \
            -Dsonar.projectName=${projectName} \
            -Dsonar.projectVersion=${sonarDate} \
            -Dsonar.ws.timeout=30 \
            -Dsonar.projectDescription="${projectDesc}" \
            -Dsonar.sources=${projectPath} \
            -Dsonar.sourceEncoding=UTF-8 \
            -Dsonar.java.binaries=target/classes \
            -Dsonar.java.test.binaries=target/test-classes \
            -Dsonar.java.surefire.report=target/surefire-reports -X
        """
    }
}