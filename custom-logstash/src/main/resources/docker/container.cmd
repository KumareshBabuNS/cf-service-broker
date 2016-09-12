export REPOSITORY_LOGSTASH=$repo_service &&
export REPOSITORY_MAIN=$repo_main &&
apt-get update &&
apt-get install -y wget &&
wget $repo_service/logstash-template.sh --no-cache &&
chmod +x logstash-template.sh &&
./logstash-template.sh -d $es_host -p $ls_password -e docker
