pipeline {
    agent any

    environment {
        MAVEN_HOME = tool 'Maven 3.8.6'
        PATH = "${env.MAVEN_HOME}/bin:${env.PATH}"
        DOCKER_IMAGE = "your-dockerhub-username/skill-battle-plus:${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'github-credentials-id', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
                    git url: 'https://github.com/ChauHoangTan/Skill-Battle-Plus.git',
                        branch: 'main',
                        credentialsId: 'github-credentials-id'
                }
            }
        }
        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh './mvnw clean package -DskipTests'
                }
            }
        }
        stage('Test Backend') {
            steps {
                dir('backend') {
                    sh './mvnw test'
                }
            }
        }
        stage('Docker Build & Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials-id', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh "docker login -u $DOCKER_USER -p $DOCKER_PASS"
                    sh "docker build -t $DOCKER_IMAGE ."
                    sh "docker push $DOCKER_IMAGE"
                }
            }
        }
        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig-credentials-id', variable: 'KUBECONFIG')]) {
                    sh "kubectl --kubeconfig=$KUBECONFIG set image deployment/skill-battle-plus skill-battle-plus=$DOCKER_IMAGE"
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