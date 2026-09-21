pipeline {
    agent any

    options {
        timestamps()
    }

    tools {
        // 名称需与 Jenkins 全局工具配置中的 JDK 名称一致。
        jdk 'jdk21'
    }

    stages {
        stage('build and verify') {
            steps {
                echo 'build and verify all modules with 2 Maven threads'
                sh 'mvn -T 2 clean install -Dmaven.javadoc.skip=false'
                echo 'build success'
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
