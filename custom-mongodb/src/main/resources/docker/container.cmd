export REPOSITORY_MONGO="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD/mongodb" &&
export REPOSITORY_MONIT="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD/monit" && 
export REPOSITORY_MAIN="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD" && 
apt-get install -y wget && 
wget https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD/mongodb/mongo-template.sh --no-cache && 
chmod +x mongo-template.sh  && 
./mongo-template.sh -d $database_name -u $database_user -p $database_password -e docker