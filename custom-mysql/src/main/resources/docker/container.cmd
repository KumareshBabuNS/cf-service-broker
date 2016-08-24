export REPOSITORY_MYSQL="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD/mariadb" &&
export REPOSITORY_MAIN="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD" &&
apt-get update &&
apt-get install -y wget &&
wget https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD/mariadb/mariadb-template.sh --no-cache &&
chmod +x mariadb-template.sh &&
./mariadb-template.sh -d $database_name -p $database_password -e docker -l $log_host -m $log_port
