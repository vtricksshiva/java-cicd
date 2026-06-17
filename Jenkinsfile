pipeline {
    agent any

    environment {
        AWS_REGION = 'ap-south-1'
        ECR_REPO   = '841229410409.dkr.ecr.ap-south-1.amazonaws.com/demo'
    }

    tools {
        maven 'maven'
    }

    stages {
        stage('Clone Repo') {
            steps {
                git branch: 'main', url: 'https://github.com/vtricksshiva/java-cicd.git'
            }
        }

        stage('Sonar Scan') {
            steps {
                script {
                    withSonarQubeEnv('sonar-server') {
                        sh "mvn clean verify sonar:sonar -Dsonar.projectKey=sonar-scan-with-jenkins -Dsonar.projectName='sonar-scan-with-jenkins'"
                    }
                }
            }
        }

        stage('Build Binaries') {
            steps {
                sh 'mvn clean install'
                sh 'cp target/java-frontend-app.war .'
            }
        }

        stage('Push Binaries to Nexus') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'nexus-cred', passwordVariable: 'pass', usernameVariable: 'user')]) {
                        sh """
                        curl -u ${user}:${pass} -T java-frontend-app.war \
                        "http://15.207.248.132:8081/repository/demo-project/-${BUILD_NUMBER}.war"
                        """
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh """
                docker build -t java-frontend-app:${BUILD_NUMBER} .
                docker tag java-frontend-app:${BUILD_NUMBER} ${ECR_REPO}:${BUILD_NUMBER}
                docker tag java-frontend-app:${BUILD_NUMBER} ${ECR_REPO}:latest
                """
            }
        }

        stage('Push to ECR') {
    steps {
        withCredentials([
            [$class: 'AmazonWebServicesCredentialsBinding',
             credentialsId: 'aws-creds']
        ]) {

            sh """
            aws sts get-caller-identity

            aws ecr get-login-password --region ${AWS_REGION} | \
            docker login --username AWS --password-stdin \
            841229410409.dkr.ecr.ap-south-1.amazonaws.com

            docker push ${ECR_REPO}:${BUILD_NUMBER}
            docker push ${ECR_REPO}:latest
            """
        }
    }
}

    }

    post {
        success {
            echo "Pipeline completed — Docker image pushed to ECR: ${ECR_REPO}:${BUILD_NUMBER}"
        }
        failure {
            echo "Pipeline failed — check the logs above"
        }
        always {
            sh "docker rmi java-frontend-app:${BUILD_NUMBER} || true"
            sh "docker rmi ${ECR_REPO}:${BUILD_NUMBER} || true"
        }
    }
}
