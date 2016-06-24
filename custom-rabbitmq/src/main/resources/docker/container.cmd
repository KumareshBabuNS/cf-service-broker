export REPOSITORY_RABBITMQ="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD/rabbitmq" &&
export REPOSITORY_MONIT="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD/monit" && 
export REPOSITORY_MAIN="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD" && 
apt-get install -y wget &&             
wget https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/HEAD/rabbitmq/rabbitmq-template.sh --no-cache && 
chmod +x rabbitmq-template.sh &&
./rabbitmq-template.sh -d $rabbit_vhost -u $rabbit_user -p $rabbit_password -e docker