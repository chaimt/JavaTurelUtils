import groovy.json.*


def incRelease = params['release']
def owner = params['repo_owner']
def product = params['repo_product']
def scope = params['repo_scope']
def component = params['repo_component']
def branch = params['repo_branch']
def repo_base = 'https://git-codecommit.eu-west-2.amazonaws.com/v1/repos/'
def repo_name = owner + "_" + product + "_" + scope + "_" + component
def repo_url = repo_base + repo_name
def ecr_base = 'abc.ecr.us-east-1.amazonaws.com/'
def docker_component = scope + "_" + component
def ecr_url = ecr_base + repo_name
def goal = params['goal']
def build_version
def merge_to_dev = params['merge_to_dev']
def remove_branch = params['remove_branch']
def rtServer = Artifactory.server "abc"
def rtMaven = Artifactory.newMavenBuild()
def buildInfo
def clean_docker = params['clean_docker']
def hotfix = params['hotfix']
def pomLocation = 'pom.xml'
def mavenBuild
def genericBuild
def dockerBuild
def dockerBuildVersion = params['docker_version']
def docker_tag_version = 'latest'
def buildProperties
def defaultMavenImage = "abc.dkr.ecr.us-east-1.amazonaws.com/dev_docker_maven363-java8:latest"

// a.b.c.d
// major.minor.release.build
String getNextVersion(build_version, minor, incRelease, isHotfix) {
    versionParts = build_version.trim().split("-|\\.")
    if (isHotfix) {
        minor = versionParts[1]
        incRelease = true
    }
    if (incRelease) {
        releaseNumber = Integer.valueOf(versionParts[2])
        if (minor > versionParts[1]){
            releaseNumber = 1
        }
        releaseNumber = releaseNumber + 1
        return versionParts[0].trim() + "." + minor.trim() + "." + releaseNumber + ".0"
    } else {
        buildNumber = Integer.valueOf(versionParts[3])
        releaseNumber = Integer.valueOf(versionParts[2])
        if (minor > versionParts[1]){
            releaseNumber = 1
            buildNumber = 0
        }
        buildNumber = buildNumber + 1
        return versionParts[0].trim() + "." + minor.trim() + "." + releaseNumber.toString() + "." + buildNumber.toString()
    }
}


//load yaml properties file
def loadProperties(){
    def properties
    if (fileExists("build_info.yaml")){
        properties = readYaml file: "./build_info.yaml"
    }
    else{
        properties = new HashMap<>()
    }

    return properties;
}

node('docker') {
    //the clean up is not run by most builds, only when there is a problem
    //it will remove all the docker images
    stage('clean up') {
        deleteDir()
        if (clean_docker) {
            try {
                sh 'docker stop $(docker ps -a -q)'
            } catch (err) {
            }
            try {
                sh 'docker rm -f $(docker ps -a -q)'
            } catch (err) {
            }
            try {
                sh 'docker rmi -f $(docker images -a -q)'
            } catch (err) {
            }
        }
    }
    //check out git
    stage('Checkout') {
        sh 'echo component: ' + component
        if (branch == "") {
            catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                sh "echo invalid branch"
                sh "exit 1"
            }
        }

        git(
                url: repo_url,
                credentialsId: 'my cred',
                branch: branch
        )
    }

    //based on files in repo, check what needs to be run (java, docker, other)
    mavenBuild = fileExists(pomLocation)
    genericBuild = fileExists("build.sh")
    dockerBuild = fileExists("Dockerfile")

    sh 'echo mavenBuild=' + mavenBuild + ', genericBuild=' + genericBuild + ', dockerBuild=' + dockerBuild
    buildProperties = loadProperties()

    //if java, then increment the version by maven,
    if (mavenBuild) {
        stage('Artifactory Configuration') {
            //do not add rtMaven.resolver since it will limit us to download from external repos that are in the pom file
            echo 'Configuration: ' + goal
            rtMaven.deployer server: rtServer, releaseRepo: 'libs-release-local', snapshotRepo: 'libs-snapshot-local'
            rtMaven.deployer.deployArtifacts = true
            rtMaven.opts = '-Dmaven.repo.local=/home/jenkins/.m2'
        }
        stage('Job Info') {
            //download sprint version from s3 file, and use that version for sprint version (override pom version)
            sh 'aws s3 cp s3://devops/jenkins/current_sprint .'
            def sprint_version = sh(script: 'head -1 current_sprint | tr -d "\\n"', returnStdout: true)
            echo 'sprint_version = ' + sprint_version

            build_version = sh(script: 'mvn -U -f ' + pomLocation + ' help:evaluate -Dexpression=project.version -q -DforceStdout ', returnStdout: true)
            build_version = getNextVersion(build_version, sprint_version, incRelease, hotfix)
            echo 'new version = ' + build_version
            docker_tag_version = "v-" + build_version

            //add version to env so that it will be accessable in docker
            env.build_version = build_version

            //update jenkins job name
            currentBuild.displayName = '#' + currentBuild.number + '_' + scope + '_' + component + '_goal:' + goal + '_version: pom : ' + build_version + '[' + branch + ']'
            currentBuild.description = "[#BN-Component-Version-Goal]"
        }

        //this stage will run in a docker so that each repo can decide what version of java and maven it needs to build
        stage('docker Build Jar') {
            def mavenImageName = buildProperties.maven_image
            if (!mavenImageName?.trim()) {
                mavenImageName = defaultMavenImage
            }
            sh 'echo mavenImageName: ' + mavenImageName

            //since in the docker that we are building, we need to run another docker for tests,
            //we do not want to run a docker in a docker, but siblings of dockers.
            //for this to work we need to shard the host /var/run/docker.sock
            //in addition so that the docker does not redownload all dependencies we need to share /home/jenkins
            sh 'echo $docker_opts'
            env.MAVEN_HOME = '/usr/share/maven'
            sh 'echo mavenImage: ' + mavenImageName
            def docker_opts = sh 'echo -v /home/jenkins:/home/jenkins -v /var/run/docker.sock:/var/run/docker.sock'
            //def userId = sh 'echo $UID:$UID'
            docker.image(mavenImageName)
                    .inside("--network jenkins -v /home/jenkins:/home/jenkins -v /var/lib/docker:/var/lib/docker -v /var/run/docker.sock:/var/run/docker.sock -u 1001:1001") {
                        sh 'echo INNER build version: ' + build_version
                        sh 'mvn -version'
                        sh '$JAVA_HOME/bin/javac -version'
                        sh 'echo mvn versions: ' + build_version
                        rtMaven.run pom: pomLocation, goals: 'versions:set -DnewVersion=' + build_version + ' -DprocessAllModules=true -s /home/jenkins/.m2/settings.xml'

                        //the aritifact plugin is used for deployment so that we will deploy only after all has build properly
                        sh 'echo build Goal: ' + goal
                        buildInfo = Artifactory.newBuildInfo()
                        buildInfo.env.capture = true
                        rtMaven.run pom: pomLocation, goals: goal + ' -Dmaven.repo.local=/home/jenkins/.m2 -s /home/jenkins/.m2/settings.xml -Pjenkins', buildInfo: buildInfo
                        rtMaven.deployer.deployArtifacts buildInfo
                    }
        }

        stage('Post Build Actions') {
            stage('Commit GIT') {
                sh 'git add *.xml'
                sh 'git checkout .'
                sh 'git commit -am "build version ' + build_version + '"'
                sh "git push --set-upstream origin " + branch
                sh 'git tag -a ' + repo_name + '/' + build_version + ' -m "jenkins build version ' + build_version + '"'
                sh 'git push origin ' + repo_name + '/' + build_version
                if (merge_to_dev && branch != "dev") {
                    sh "git fetch origin"
                    sh "git checkout -b dev origin/dev"
                    sh "git merge " + branch
                    sh "git push "
                    if (remove_branch) {
                        sh "git branch -d " + branch
                        sh "git push origin --delete " + branch
                    }
                }
            }
            stage('deploy Jar') {
                if (merge_to_dev || branch == "dev") {
                    sh 'echo publish'
                    rtServer.publishBuildInfo buildInfo
                }
            }
        }
    }

    if (genericBuild) {
        stage('Generic Build') {
            sh "chmod +x build.sh"
            sh "source build.sh"
        }
    }

    if (dockerBuild) {
        stage('Build docker') {
            if (!genericBuild && !mavenBuild) {
                currentBuild.displayName = '#' + currentBuild.number + '_' + scope + '_' + component + '_version: ' + dockerBuildVersion + '[' + branch + ']'
            }

            if (!dockerBuildVersion?.trim()) {
                dockerBuildVersion = 'latest'
            }
            echo 'docker version: ' + dockerBuildVersion
            echo 'docker_component: ' + docker_component

            dir('.') {
                docker_login_cmd = '$(aws ecr get-login --no-include-email --region us-east-1)'
                echo 'Running CMD: ' + docker_login_cmd
                sh '$(aws ecr get-login --no-include-email --region us-east-1)'
                echo "ecr_url: " + ecr_url
                echo "docker_component: " + docker_component
                echo "docker_tag_version: " + docker_tag_version
                echo 'clear images '
                sh 'docker images |  grep ' + docker_component + ' | awk \'{print \$3}\' | xargs docker rmi -f 2>/dev/null || true'
                echo 'images cleared'
                sh 'docker build -t ' + docker_component + ':' + docker_tag_version + ' -f Dockerfile .'
                docker_tag_cmd = "docker tag " + docker_component + ":" + docker_tag_version + " " + ecr_url + ":" + docker_tag_version
                echo 'Running CMD: ' + docker_tag_cmd
                sh docker_tag_cmd
                if (branch == "dev") {
                    docker_tag_cmd = "docker tag " + docker_component + ":" + docker_tag_version + " " + ecr_url + ":dev"
                    echo 'Running CMD: ' + docker_tag_cmd
                    sh docker_tag_cmd
                }
                echo 'Running CMD: docker push ' + ecr_url
                sh 'docker push ' + ecr_url
                sh 'docker rmi ' + docker_component + ':' + docker_tag_version
            }
        }
    }

}
