pipeline {
    agent any

    options { timestamps () }

    tools {
            jdk 'jdk17'  // 这里的名称需与Jenkins全局工具配置中的JDK名称一致
    }

    stages {
        stage('build lcxm-springboot-basic-support') {
            steps {
                echo "install lcxm-springboot-basic-support"
                sh 'mvn clean install'
                echo 'install success'
            }
        }
    }
    post {
        always {
            emailext body: '$DEFAULT_CONTENT', subject: '$DEFAULT_SUBJECT', to: '84597585@qq.com'
        }
        success {
            echo "success"
        }
        failure {
            echo "failure"
        }
    }
}
