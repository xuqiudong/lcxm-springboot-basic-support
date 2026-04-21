pipeline {
    agent any

    options { timestamps () }

    tools {
            jdk 'jdk21'  // 这里的名称需与Jenkins全局工具配置中的JDK名称一致
    }

    stages {
        stage('build lcxm-springboot-basic-support parent') {
            steps {
                echo "install lcxm-springboot-basic-support parent"
                sh 'mvn clean install -N -Dmaven.javadoc.skip=false'
                echo 'install parent success'
            }
        }

        stage('build lcxm-springboot-basic-support all module') {
                    steps {
                        echo "build all modules (skip clean to save time)"
                        sh 'mvn install -Dmaven.javadoc.skip=false'
                        echo 'build all success'
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
