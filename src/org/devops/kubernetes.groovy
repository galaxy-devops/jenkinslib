package org.devops

//encapulse http request
def HttpReq(reqType,reqUrl,reqBody){
    
    /**
     * @param   reqType     the request type, such like http
     * @param   reqUrl      the url of server
     * @param   reqBody     the request body which to be sent
     */
       
    //k8s server
    def apiServer = "https://10.138.181.3:6443/apis/apps/v1"
    
    withCredentials([string(credentialsId: 'kubernetes-token', variable: 'kubernetestoken')]) {
        result = httpRequest customHeaders: [[maskValue: true, name: 'Authorization', value: "Bearer ${kubernetestoken}"],
                                            [maskValue: false, name: 'Content-Type', value: 'application/yaml'],
                                            [maskValue: false, name: 'Accept', value: 'application/yaml']],
                httpMode: "${reqType}",
                consoleLogResponseBody: true,
                ignoreSslErrors: true,
                requestBody: "${reqBody}",
                url: "${apiServer}/${reqUrl}",
                quiet: true
    }
    return result
}

// retrieve deployment resource object
def GetDeployment(nameSpace,deployName){
    
    /**
     * @param   nameSpace       the namespace in the k8s
     * @param   deployName      deployment object name
     */
    
    apiUrl = "namespaces/${nameSpace}/deployments/${deployName}"
    response = HttpReq('GET',apiUrl,'')
    return response
}

// update Deployment resource object
def UpdateDeployment(nameSpace,deployName,deployBody){
    
    /**
     * @param   nameSpace       the namespace in the k8s
     * @param   deployName      deploy resource object name of k8s
     * @param   deployBody      the deploy content
     */
    
    apiUrl = "namespaces/${nameSpace}/deployments/${deployName}"
    response = HttpReq('PUT',apiUrl,deployBody)
}