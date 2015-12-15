export REPOSITORY_POSTGRES="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/master/Postgres-v3" &&
export REPOSITORY_MAIN="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/master" && 
apt-get install -y wget && 
wget https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/master/Postgres-v3/postgres-template.sh && 
chmod +x postgres-template.sh  && 
./postgres-template.sh -d $database_name -u $database_user -p $database_password -e docker && 
top