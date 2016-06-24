export REPOSITORY_POSTGRES="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD/postgres" &&
export REPOSITORY_MAIN="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD" && 
apt-get install -y wget && 
wget https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD/postgres/postgres-template.sh && 
chmod +x postgres-template.sh  && 
./postgres-template.sh -d $database_name -u $database_user -p $database_password -e docker && 
top