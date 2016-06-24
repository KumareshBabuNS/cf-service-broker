export REPOSITORY_ELASTICSEARCH="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD/elasticsearch" &&
export REPOSITORY_MAIN="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD" &&
apt-get install -y wget && 
wget https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD/elasticsearch/elasticsearch-template.sh &&
chmod +x elasticsearch-template.sh &&
./elasticsearch-template.sh -p 0pens4ckDeock -e docker