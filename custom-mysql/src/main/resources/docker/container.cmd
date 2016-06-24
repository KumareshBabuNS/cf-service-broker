export REPOSITORY_MYSQL="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD/mysql" &&
export REPOSITORY_MAIN="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD" &&
apt-get install -y wget &&
wget https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD/mysql/mysql-template.sh
chmod +x mysql-template.sh &&
./mysql-template.sh -d $database_name -p $database_password -e docker