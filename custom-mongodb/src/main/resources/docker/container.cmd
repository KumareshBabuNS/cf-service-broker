export REPOSITORY_MONGO="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/master/Mongo-v3" && 
export REPOSITORY_MAIN="https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/master" && 
apt-get install -y wget && 
wget https://bitbucket.org/evoila-boxer/deployment-scripts-docker-openstack/raw/master/Mongo-v3/mongo-template.sh && 
chmod +x mongo-template.sh  && 
./mongo-template.sh -d $database_name -u $database_user -p $database_password -e docker && 
top