pipeline {
    agent { label 'agenthost' }
    tools {
        jfrog 'jfrog-cli'
    }
    stages {
        stage('Initialisation') {
            steps {
                script {
                    // Définition d'une closure pour factoriser la logique de push vers Jfrog
                    jfrogPush = { projectDir, projectPrefix, repository ->
                        dir(projectDir) {
                            echo "📦 Building the ${projectDir} project for Jfrog push!"
                            sh 'mvn install -DskipTests'
                            def date = new Date().format('yyMMdd-HHmm')
                            def fileName = (env.GIT_BRANCH == 'main') ?
                                "${projectPrefix}-${date}.jar" : "${projectPrefix}-${date}-SNAPSHOT.jar"
                            def folderName = (env.GIT_BRANCH == 'main') ?
                                "release/${date}" : "snapshot/${date}"
                            echo "📤 Publishing the ${projectDir} project to jfrog folder ${folderName} as ${fileName}"
                            jf "rt u *.jar ${repository}/${folderName}/${fileName}"
                        }
                    }
                }
            }
        }
        stage('[BE] Build') {
            steps {
                dir('backend') {
                    echo '🛠️ Pipeline is building the backend project !'
                    sh 'mvn clean compile'
                }
            }
        }
        stage('[CLI] Build') {
            steps {
                dir('cli') {
                    echo '🛠️ Pipeline is building the CLI project !'
                    sh 'mvn clean compile'
                }
            }
        }
        stage('[BE] Unit tests') {
            steps {
                dir('backend') {
                    echo '🧪 Pipeline is launching backend unit tests !'
                    sh 'mvn test'
                }
            }
        }
        stage('[CLI] Unit tests') {
            steps {
                dir('cli') {
                    echo '🧪 Pipeline is launching cli unit tests !'
                    sh 'mvn test'
                }
            }
        }
        stage('[BE] Docker Build') {
            when {
                anyOf {
                    branch 'dev'
                    environment name: 'CHANGE_TARGET', value: 'dev'
                }
            }
            steps {
                dir('backend') {
                    echo '🐋📷 Pipeline is building the docker image of the backend project!'
                    sh './build.sh'
                }
            }
        }
        stage('SonarQube Analysis') {
            when {
                anyOf {
                    branch 'dev'
                    branch 'main'
                    environment name: 'CHANGE_TARGET', value: 'dev'
                }
            }
            steps {
                dir('cli') {
                    withSonarQubeEnv("KiwiCardSonar") {
                        sh "mvn verify sonar:sonar -Dsonar.projectKey=KiwiCardCLI -Dsonar.projectName='KiwiCardCLI' -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml,target/site/jacoco-it/jacoco.xml -Dsonar.junit.reportsPaths=target/surefire-reports,target/failsafe-reports"
                    }
                }
                dir('backend') {
                    withSonarQubeEnv("KiwiCardSonar") {
                        sh "mvn verify sonar:sonar -Dsonar.projectKey=KiwiCard -Dsonar.projectName='KiwiCard' -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml,target/site/jacoco-it/jacoco.xml -Dsonar.junit.reportsPaths=target/surefire-reports,target/failsafe-reports"
                    }
                }
            }
        }
        stage('[BE] Jfrog push') {
            when {
                anyOf {
                    branch 'dev'
                    branch 'main'
                    environment name: 'CHANGE_TARGET', value: 'dev'
                }
            }
            steps {
                script {
                    jfrogPush('backend', 'kiwi-card-be', 'kiwi-card-be-generic-local')
                }
            }
        }
        stage('[CLI] Jfrog push') {
            when {
                anyOf {
                    branch 'dev'
                    branch 'main'
                    environment name: 'CHANGE_TARGET', value: 'dev'
                }
            }
            steps {
                script {
                    jfrogPush('cli', 'kiwi-card-cli', 'kiwi-card-cli-generic-local')
                }
            }
        }
        stage('[BE][CLI] Docker Push') {
            when {
                anyOf {
                    branch 'dev'
                    branch 'main'
                    environment name: 'CHANGE_TARGET', value: 'dev'
                }
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                    script {
                        // Connexion à Docker Hub (effectuée une seule fois)
                        sh "docker login -u $DOCKERHUB_USERNAME -p $DOCKERHUB_PASSWORD"

                        // Définition des informations spécifiques à chaque projet
                        def projects = [
                            backend: [
                                artifactoryUrl: 'http://vmpx10.polytech.unice.fr:8011/artifactory/kiwi-card-be-generic-local',
                                artifactPrefix: 'kiwi-card-be',
                                imageName: 'teamj/kiwicard-spring-backend',
                                dockerHubRepo: 'kiwicard-spring-backend'
                            ],
                            cli: [
                                artifactoryUrl: 'http://vmpx10.polytech.unice.fr:8011/artifactory/kiwi-card-cli-generic-local',
                                artifactPrefix: 'kiwi-card-cli',
                                imageName: 'teamj/kiwicard-cli',
                                dockerHubRepo: 'kiwicard-cli'
                            ]
                        ]

                        // Boucle sur chaque projet (backend et cli)
                        projects.each { project, data ->
                            dir(project) {
                                echo "📥 Téléchargement du JAR depuis Artifactory pour ${project}..."
                                def date = new Date().format('yyMMdd-HHmm')
                                def artifactoryFolder = (env.GIT_BRANCH == 'main') ? "release/${date}" : "snapshot/${date}"
                                def fileSuffix = (env.GIT_BRANCH == 'main') ? "${date}" : "${date}-SNAPSHOT"
                                def downloadUrl = "${data.artifactoryUrl}/${artifactoryFolder}/${data.artifactPrefix}-${fileSuffix}.jar"
                                echo "Downloading ${project} JAR from ${downloadUrl}"
                                withCredentials([usernamePassword(credentialsId: 'jfrog-creds', usernameVariable: 'JFROG_USER', passwordVariable: 'JFROG_PASSWORD')]) {
                                    sh "curl -u $JFROG_USER:$JFROG_PASSWORD  -L -o app.jar '${downloadUrl}'"
                                }

                                echo "🐳 Construction de l'image Docker pour ${project} avec le tag ${fileSuffix}"
                                sh "docker build -t ${data.imageName}:${fileSuffix} -f Dockerfile ."

                                echo "📤 Push de l'image Docker ${project} sur Docker Hub avec le tag ${fileSuffix}"
                                sh "docker tag ${data.imageName}:${fileSuffix} \$DOCKERHUB_USERNAME/${data.dockerHubRepo}:${fileSuffix}"
                                sh "docker push \$DOCKERHUB_USERNAME/${data.dockerHubRepo}:${fileSuffix}"
                                if (env.GIT_BRANCH == 'main') {
                                    echo "📤 Push de l'image Docker ${project} avec le tag latest"
                                    sh "docker tag ${data.imageName}:${fileSuffix} \$DOCKERHUB_USERNAME/${data.dockerHubRepo}:latest"
                                    sh "docker push \$DOCKERHUB_USERNAME/${data.dockerHubRepo}:latest"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
