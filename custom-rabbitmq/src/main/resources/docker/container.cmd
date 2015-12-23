export REPOSITORY_RABBITMQ="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/master/Rabbitmq-v3" &&
export REPOSITORY_MAIN="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/master" && 
apt-get install -y wget &&             
wget https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/master/Rabbitmq-v3/rabbitmq-template.sh && 
chmod +x rabbitmq-template.sh &&
./rabbitmq-template.sh -d $rabbit_vhost -u $rabbit_user -p $rabbit_password -e docker