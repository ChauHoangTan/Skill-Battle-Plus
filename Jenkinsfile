pipeline {
    agent any

    environment {
        REGISTRY = "chauhoangtan"
        MAVEN_HOME = tool 'Maven 3.8.6'
        PATH = "${env.MAVEN_HOME}/bin:${env.PATH}"
        DOCKER_IMAGE = "chauhoangtan/skill-battle-plus:${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'credential-for-skill-battle-plus',
                        usernameVariable: 'GIT_USER',
                        passwordVariable: 'GIT_PASS'
                    )
                ]) {
                    git branch: 'main',
                        credentialsId: 'credential-for-skill-battle-plus',
                        url: 'https://github.com/ChauHoangTan/Skill-Battle-Plus.git'
                }
            }
        }

        stage('Detect changes') {
            steps {
                script {
                    // get List of change files
                    def changedFiles = sh(
                        script: "git diff --name-only origin/main...HEAD",
                        returnStdout: true
                    ).trim().split("\n")

                    echo "Changed files: ${changedFiles}"

                    // list of services
                    def services = [
                        "user-service",
                        "quiz-service",
                        "question-service",
                        "notification-service",
                        "exam-service",
                        "eureka-service",
                        "battle-service",
                        "auth-service",
                        "api-gateway",
                        "analytics-service",
                        "admin-service",
                    ]

                    // filter for changed service
                    def changedServices = services.findAll { svc ->
                        changedFiles.any { it.startsWith("${svc}/") }
                    }

                    env.CHANGED_SERVICES = changedServices.join(',')
                    echo "Services to build: ${env.CHANGED_SERVICES}"
                }
            }
        }

        stage('Build Backend') {
            when {
                expression { env.CHANGED_SERVICES?.trim() }
            }
            steps {
                script {
                    for (svc in env.CHANGED_SERVICES.split(',')) {
                        dir("server/${svc}") {
                            sh "mvn clean package -DskipTests"
                        }
                    }
                }
            }
        }

        // stage('Test Backend') {
        //     steps {
        //         dir('server') {
        //             sh 'mvn test'
        //         }
        //     }
        // }

        stage('Docker Build & Push') {
            when {
                expression { env.CHANGED_SERVICES?.trim() }
            }
            steps {
                dir('server') {
                    script {
//                         def services = [
//                             [name: "user-service", path: "user-service"],
//                             [name: "quiz-service", path: "quiz-service"],
//                             [name: "question-service", path: "question-service"],
//                             [name: "notification-service", path: "notification-service"],
//                             [name: "exam-service", path: "exam-service"],
//                             [name: "eureka-service", path: "eureka-service"],
//                             [name: "battle-service", path: "battle-service"],
//                             [name: "auth-service", path: "auth-service"],
//                             [name: "api-gateway", path: "api-gateway"],
//                             [name: "analytics-service", path: "analytics-service"],
//                             [name: "admin-service", path: "admin-service"]
//                         ]

                        withCredentials([
                            usernamePassword(
                                credentialsId: 'docker-credentials-for-skill-battle-plus',
                                usernameVariable: 'DOCKER_USER',
                                passwordVariable: 'DOCKER_PASS'
                            )
                        ]) {
                            sh """
                                echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                            """

                            for (svc in env.CHANGED_SERVICES.split(',')) {
                                def imageName = "${REGISTRY}/${svc}:latest"
                                sh """
                                    docker build -t ${imageName} -f ${svc}/Dockerfile .
                                    docker push ${imageName}
                                """
                                env["${svc.toUpperCase().replace('-', '_')}_IMAGE"] = imageName
                            }
                        }
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
//                     def services = [
//                         "user-service",
//                         "quiz-service",
//                         "question-service",
//                         "notification-service",
//                         "exam-service",
//                         "eureka-service",
//                         "battle-service",
//                         "auth-service",
//                         "api-gateway",
//                         "analytics-service",
//                         "admin-service"
//                     ]

                    withCredentials([
                        file(
                            credentialsId: 'kubeconfig-credentials-id',
                            variable: 'KUBECONFIG'
                        )
                    ]) {
                        for (svc in env.CHANGED_SERVICES.split(',')) {
                            def envVarName = "${svc.toUpperCase().replace('-', '_')}_IMAGE"
                            def imageName = env[envVarName]
                            sh """
                                kubectl --kubeconfig=$KUBECONFIG set image deployment/${svc} ${svc}=${imageName} -n dev
                                kubectl --kubeconfig=$KUBECONFIG rollout status deployment/${svc} -n dev
                            """
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished.'
        }
        success {
            echo 'Build and deployment succeeded!'
        }
        failure {
            echo 'Build or deployment failed!'
        }
    }
}
