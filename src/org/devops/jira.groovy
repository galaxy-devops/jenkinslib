package org.devops

// encapulate http request
def HttpReq(reqType,reqUrl,reqBody){
    
    /**
     * @param   reqType     the request type
     * @param   reqUrl      the request address
     * @param   reqBody     the request content
     */

    // jira server
    def apiServer = "http://10.138.181.3:18009/rest/api/2"
    result = httpRequest authentication: 'jira-admin-user',
            httpMode: "${reqType}",
            contentType: "APPLICATION_JSON",
            consoleLogResponseBody: true,
            ignoreSslErrors: true,
            requestBody: "${reqBody}",
            url: "${apiServer}/${reqUrl}",
            quiet: true
    
    return result

}

// send request to Jira
def RunJql(jqlContent){
    
    /**
     * @param   jqlContent      the command content
     */

    apiUrl = "search?jql=${jqlContent}"
    response = HttpReq("GET",apiUrl,'')
    return response
}