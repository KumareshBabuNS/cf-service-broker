export REPOSITORY_LOGSTASH="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/master/Logstash-v3" &&
export REPOSITORY_MAIN="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/master" &&
apt-get install -y wget && 
wget https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/master/Logstash-v3/logstash-template.sh &&
chmod +x logstash-template.sh &&
./logstash-template.sh -d $es_host -e docker &&
top
