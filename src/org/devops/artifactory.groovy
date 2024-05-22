package org.devops

// maven building
def MavenBuild(buildShell){

    /**
     * @param   buildShell  the build command from pipeline
     */

    def server = Artifactory.newServer url: "http://10.138.181.4:31084/artifactory"
    def rtMaven = Artifactory.newMavenBuild()
    def buildInfo = Artifactory.newBuildInfo()
    server.connection.timeout = 300
    server.credentialsId = 'artifactory-admin-user'

    rtMaven.tool = 'M2'

    String newBuildShell = "${buildShell}".toString()
    rtMaven.run pom: 'pom.xml', goals: newBuildShell, buildInfo: buildInfo
    server.publishBuildInfo buildInfo   
}

// upload artifact
def PushArtifact(){
    
    // get jar file
    def jarName = sh returnStdout: true, script: "cd target; ls *.jar"
    jarName = jarName - "\n"

    def pom = readMavenPom file: 'pom.xml'

    env.pomVersion = "${pom.version}"
    env.serviceName = "${JOB_NAME}".split("_")[0]
    env.buildTag = "${BUILD_ID}"

    // rename new jar file
    def newJarName = "${serviceName}-${pomVersion}-${buildTag}.jar"
    println("will rename jar file：${jarName} to the new one：${newJarName}")
    sh "mv target/${jarName} target/${newJarName}"

    // upload artifact
    env.businessName = "${env.JOB_NAME}".split("-")[0]
    env.repoName = "${businessName}-${JOB_NAME.split("_")[-1].toLowerCase()}"

    println("The artifact will be uploaded to ${repoName} ")
    env.uploadDir = "${repoName}/${businessName}/${serviceName}/${pomVersion}"

    println(">>>>>>>>>    start to upload    <<<<<<<<<")
    rtUpload (
        serverId: "artifactory",
        spec:
            """{
                "files": [
                    {
                        "pattern": "target/${newJarName}",
                        "target": "${uploadDir}/"
                    }
                ]
            }"""
    )
}

def main(buildType,buildShell){
    if (buildType == "mvn"){
        MavenBuild(buildShell)
    }
}