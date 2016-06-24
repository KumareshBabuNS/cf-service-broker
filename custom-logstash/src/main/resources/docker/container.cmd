export REPOSITORY_LOGSTASH="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD/logstash" &&
export REPOSITORY_MAIN="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD" &&
apt-get install -y wget && 
wget https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD/logstash/logstash-template.sh &&
chmod +x logstash-template.sh &&
./logstash-template.sh -d $es_host -p 3uvjnv3e9823 -e docker
