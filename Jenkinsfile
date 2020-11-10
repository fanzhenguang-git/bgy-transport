pipeline {
    agent {
        docker {
            image 'base/builder:1.2.0'
            args '-e STAGE_SERVER=${STAGE_SERVER} -e EXPOSE=${EXPOSE}'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }
    stages {
        stage('Build') {
            steps {
                sh 'mvn -B -P prod -DskipTests clean package'
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }
        stage('Deploy') {
            steps {
                sh 'mvn -B -P prod deploy'
            }
        }
        stage('Stage') {
            steps {
                // docker_tag
                sh 'echo -n deliver/$(xmllint --xpath \'/*[local-name()="project"]/*[local-name()="artifactId"]/text()\' pom.xml): > docker_tag'
                sh 'echo -n $(xmllint --xpath \'/*[local-name()="project"]/*[local-name()="version"]/text()\' pom.xml) >> docker_tag'

                // server
                sh 'docker -H ${STAGE_SERVER} pull $(<docker_tag)'
                sh 'docker -H ${STAGE_SERVER} rm -f $(cut -d: -f1 docker_tag | tr / .) || true'
                sh 'docker -H ${STAGE_SERVER} run --restart=always -d \
                    -e "DELIVER_TRANSPORT_LOGGING_LEVEL=${DELIVER_TRANSPORT_LOGGING_LEVEL}" \
                    -e "DELIVER_TRANSPORT_DATASOURCE=${DELIVER_TRANSPORT_DATASOURCE}" \
                    -e "DELIVER_TRANSPORT_MYSQL_USERNAME=${DELIVER_TRANSPORT_MYSQL_USERNAME}" \
                    -e "DELIVER_TRANSPORT_MYSQL_PASSWORD=${DELIVER_TRANSPORT_MYSQL_PASSWORD}" \
                    -e "DELIVER_APOLLO_SERVER_HOST=${DELIVER_APOLLO_SERVER_HOST}" \
                    -e "DELIVER_APOLLO_SERVER_PORT=${DELIVER_APOLLO_SERVER_PORT}" \
                    -e "DELIVER_TRANSPORT_RPC_PORT=${DELIVER_TRANSPORT_RPC_PORT}" \
                    -e "DELIVER_TRANSPORT_TELCOM_ENV=${DELIVER_TRANSPORT_TELCOM_ENV}" \
                    -e "DELIVER_TRANSPORT_TELCOM_HOOK_HOST=${DELIVER_TRANSPORT_TELCOM_HOOK_HOST}" \
                    -e "DELIVER_TRANSPORT_TELCOM_PLUGINS=${DELIVER_TRANSPORT_TELCOM_PLUGINS}" \
                    -e "DELIVER_TRANSPORT_REDIS_DATABASE=${DELIVER_TRANSPORT_REDIS_DATABASE}" \
                    -e "DELIVER_TRANSPORT_TELCOM_ENABLE=${DELIVER_TRANSPORT_TELCOM_ENABLE}" \
                    -e "DELIVER_TRANSPORT_MSJ_ENABLE=${DELIVER_TRANSPORT_MSJ_ENABLE}" \
                    -e "DELIVER_TRANSPORT_COAP_ENABLE=${DELIVER_TRANSPORT_COAP_ENABLE}" \
                    -e "DELIVER_TRANSPORT_TCP_ENABLE=${DELIVER_TRANSPORT_TCP_ENABLE}" \
                    -e "DELIVER_TRANSPORT_UDP_ENABLE=${DELIVER_TRANSPORT_UDP_ENABLE}" \
                    -e "DELIVER_TRANSPORT_QJ_ENABLE=${DELIVER_TRANSPORT_QJ_ENABLE}" \
                    -e "DELIVER_TRANSPORT_REDIS_SERVER_HOST=${DELIVER_TRANSPORT_REDIS_SERVER_HOST}" \
                    -e "DELIVER_TRANSPORT_KAFKA_ENABLE=${DELIVER_TRANSPORT_KAFKA_ENABLE}" \
                    -e "DELIVER_APOLLO_SERVER_ENABLE=${DELIVER_APOLLO_SERVER_ENABLE}" \
                    -e "DELIVER_TRANSPORT_GUIDE_ENABLE=${DELIVER_TRANSPORT_GUIDE_ENABLE}" \
                    ${EXPOSE:+ -p ${EXPOSE//,/ -p }} \
                    --name $(cut -d: -f1 docker_tag | tr / .) $(<docker_tag)'
            }
        }
    }
}
