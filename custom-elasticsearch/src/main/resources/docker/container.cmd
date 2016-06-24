export REPOSITORY_ELASTICSEARCH="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD/elasticsearch" &&
export REPOSITORY_MONIT="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD/monit" &&
export REPOSITORY_MAIN="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD" &&
apt-get install -y wget &&
wget https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD/elasticsearch/elasticsearch-template.sh --no-cache &&
chmod +x elasticsearch-template.sh &&
./elasticsearch-template.sh -p $es_password -e docker
