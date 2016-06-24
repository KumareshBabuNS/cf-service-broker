export REPOSITORY_REDIS="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD/redis" &&
export REPOSITORY_MAIN="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD" && 
apt-get install -y wget &&            
wget https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD/redis/redis-template.sh &&
chmod +x redis-template.sh && 
./redis-template.sh -n $database_number -p $database_password -e docker