
apt update

kill -9 `sudo lsof -t -i:8080`

rm -rf Bachelors-backend

# in case no java
add-apt-repository ppa:openjdk-r/ppa -y || true
apt-get install openjdk-13-jdk -y || true

git clone https://github.com/GIVIl3o/Bachelors-backend.git

cd Bachelors-backend

# tag to env
get_instance_tags () {
    instance_id=$(curl --silent http://169.254.169.254/latest/meta-data/instance-id)
    echo $(aws ec2 describe-tags --region eu-central-1 --filters "Name=resource-id,Values=$instance_id")
}

get_ami_tags () {
    ami_id=$(curl --silent http://169.254.169.254/latest/meta-data/ami-id)
    echo $(aws ec2 describe-tags --region eu-central-1 --filters "Name=resource-id,Values=$ami_id")
}

tags_to_env () {
    tags=$1

    for key in $(echo $tags | /usr/bin/jq -r ".[][].Key"); do
        value=$(echo $tags | /usr/bin/jq -r ".[][] | select(.Key==\"$key\") | .Value")
        export $key="$value"
    done
}

ami_tags=$(get_ami_tags)
instance_tags=$(get_instance_tags)

tags_to_env "$ami_tags"
tags_to_env "$instance_tags"
# tag to env end


# forward tcp, i'm lazy to do something more complicated :(((
iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080

./mvnw spring-boot:run