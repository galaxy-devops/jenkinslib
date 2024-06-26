package org.devops

// encapsulate HTTP request
def HttpReq(reqType,reqUrl,reqBody){
    
    /**
     * @param reqType       request type
     * @param reqUrl        API url which want to access
     * @param reqBody       request body
     */
    
    // gitlab server
    def gitServer = "http://10.4.7.101:8088/api/v4"
    withCredentials([string(credentialsId: 'gitlab-token', variable: 'gitlabToken')]) {
        result = httpRequest customHeaders: [[maskValue: true, name: 'PRIVATE-TOKEN', value: "${gitlabToken}"]],
                    httpMode: "${reqType}",
                    contentType: "APPLICATION_JSON",
                    consoleLogResponseBody: true,
                    ignoreSslErrors: true,
                    requestBody: "${reqBody}",
                    url: "${gitServer}/${reqUrl}",
                    quiet: true
    }
    return result
}

// retrieve ID
def GetProjectID(repoName='',projectName){

    /**
     * @param   repoName        the repository name
     * @param   projectName     the project name   
     */

    projectAPI = "projects?search=${projectName}"
    response = HttpReq('GET',projectAPI,'')
    def result = readJSON text: """${response.content}"""

    for (repo in result){
        if (repo['path'] == "${projectName}"){
            repoId = repo['id']
            println("Project ID：${repoId}")
            break
        }
    }
    return repoId
}

// create branch
def CreateBranch(projectId,refBranch,newBranch){

    /**
     * @param   projectId       the ID number of project
     * @param   refBranch       branch name which based on it
     * @param   newBranch       new branch name
     */

    try{
        branchApi = "projects/${projectId}/repository/branches?branch=${newBranch}&ref=${refBranch}"
        response = HttpReq("POST",branchApi,'').content
        branchInfo = readJSON text: """${response}"""
    } catch(e){
        println(e)
    }
}

// create branch request
def CreateMr(projectId,sourceBranch,targetBranch,title,assigneeUser=""){
    
    /**
     * @param       projectId       the id number of project which in gitlab
     * @param       sourceBranch    the source branch in MR
     * @param       targetBranch    the target branch which merge to
     * @param       title           the title info
     * @param       assigneeUser    the assignee name
     */
    
    try{
        def mrUrl = "projects/${projectId}/merge_requests"
        def reqBody = """{"source_branch":"${sourceBranch}","target_branch":"${targetBranch}","title":"${title}","assignee_id":"${assigneeUser}"}"""
        response = HttpReq("POST",mrUrl,reqBody).content
        return response
    } catch(e){
        println(e)
    }
}

// create file
def CreateRepoFile(projectId,filePath,fileContent){
    /**
     * @param   projectId       the project ID
     * @param   filePath        file path
     * @param   fileContent     file content which need to put on the gitlab
     */

    apiUrl = "projects/${projectId}/repository/files/${filePath}"
    reqBody = """{"branch": "master","encoding":"base64","content":"${fileContent}","commit_message":"create a new file"}"""
    response = HttpReq('POST',apiUrl,reqBody)
}

// get file content
def GetRepoFile(projectId,filePath){

    /**
     * @param   projectId   the project ID
     * @param   filePath    the file path
     */

    apiUrl = "projects/${projectId}/repository/files/${filePath}/raw?ref=master"
    response = HttpReq('GET',apiUrl,'')
    return response.content
}

// update file
def UpdateRepoFile(projectId,filePath,fileContent){

    /**
     * @param   projectId   the project ID
     * @param   filePath    the file path
     * @param   fileContent file content
     */

    apiUrl = "projects/${projectId}/repository/files/${filePath}"
    reqBody = """{"branch": "master","encoding":"base64", "content": "${fileContent}", "commit_message": "update a new file"}"""
    response = HttpReq('PUT',apiUrl,reqBody)
}

// AcceptMR
def AcceptMr(projectId,mergeId){
    
    /**
     * @param   projectId   the project ID
     * @param   mergeId     merge ID
     */

    def apiUrl = "projects/${projectId}/merge_requests/${mergeId}/merge"
    HttpReq('PUT',apiUrl,'')
}

// Retrieve MR status after AcceptMr
def GetSingleMr(projectId,mergerRequestIid){

    /**
     * @param   projectId           the project ID
     * @param   mergerRequestIid    the MR ID
     */

    apiUrl = "projects/${projectId}/merge_requests/${mergerRequestIid}"
    response = HttpReq("GET",apiUrl,'')
    newResponse = response.content
    def branchInfo = readJSON text: """${newResponse}"""
    if(branchInfo.size() == 0){
        return "error"
    } else {
        state = branchInfo["state"]
        return state
    }
}

// Delete branch
def DeleteBranch(projectId,branchName){

    /**
     * @param   projectId       the project ID
     * @param   branchName      the branch name
     */

     apiUrl = "/projects/${projectId}/repository/branches/${branchName}"
     response = HttpReq("DELETE",apiUrl,'').content
}