package org.devops

//encapulate HTTP request for sonarQube
def HttpReq(reqType,reqUrl,reqBody){
    /**  
     * @param reqType     request type, ex: http
     * @param reqUrl      API adress where to request
     * @param reqBody     request body
     */

    def sonarServer = "http://sonarqube:9000/api"

    result = httpRequest authentication: 'sonar-admin-user',
                httpMode: "${reqType}",
                contentType: "APPLICATION_JSON",
                consoleLogResponseBody: true,
                ignoreSslErrors: true,
                requestBody: "${reqBody}",
                url: "${sonarServer}/${reqUrl}",
                quiet: true
    return result
}

// search the project on the sonarQube
def SearchProject(projectName){

    /**
     * @param projectName  the project name
     */

    apiUrl = "projects/search?projects=${projectName}"
    response = HttpReq("GET",apiUrl,'')
    response = readJSON text: """${response.content}"""
    result = response["paging"]["total"]

    if (result.toString() == "0"){
        return "false"
    } else {
        return "true"
    }
}

// create the project on sonarQube
def CreateProject(projectName){

    /**
     * @param projectName the project name which will be created
     */

    apiUrl = "projects/create?name=${projectName}&project=${projectName}"
    response = HttpReq("POST",apiUrl,'')
}

// get the quality profile
def GetQualityProfiles(qpname){

    /**
     * @param qpname the quality profile name
     */

    apiUrl = "qualityprofiles/search?qualityProfile=${qpname}"
    response = HttpReq("GET",apiUrl,'')
    response = readJSON text: """${response.content}"""

    if (response["profiles"].isEmpty()){
        return "error"
    } else {
        return "true"
    }
}

// configure QP
def ConfigQualityProfiles(projectName,lang,qpname){

    /**
      * @param projectName the project name
      * @param lang        code language
      * @param qpname      the quality profile name
     */
    vName = GetQualityProfiles(qpname)
    if (vName == "error") {
        println("there is error in quality profile or does not exist")
        return "error"
    } else {
        apiUrl = "qualityprofiles/add_project?language=${lang}&project=${projectName}&qualityProfile=${qpname}"
        response = HttpReq("POST",apiUrl,'')

        return "true"
    }
}

// get QG name
def GetQualityGateName(gateName){

    /**
     * @param gateName the quality gate name
     */

    apiUrl = "qualitygates/show?name=${gateName}"
    response = HttpReq("GET",apiUrl,'')
    response = readJSON text: """${response.content}"""

    if (response["name"]) {
        return "true"
    } else {
        println("${gateName} not existed")
        return "error"
    }
}

// configure QG
def ConfigQualityGates(projectName,gateName){

     /**
      * @param projectName the project name
      * @param gateName    the quality gate name
      */

    vName = GetQualityGateName(gateName)
    if (vName == "error") {
        println("there is error in quality gate or does not exist")
        return "error"
    } else {
        apiUrl = "qualitygates/select?gateName=${gateName}&projectKey=${projectName}"
        
        response = HttpReq("POST",apiUrl,'')
        return "true"
    }
}

// get the status from sonarQube QG
def GetProjectStatus(projectName){

    /**
     * @param projectName the project name
     */

    // define API endpoint
    apiUrl = "project_branches/list?project=${projectName}"
    response = HttpReq("GET",apiUrl,'')
    response = readJSON text: """${response.content}"""
    result = response["branches"][0]["status"]["qualityGateStatus"]
    return result
}