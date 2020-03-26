
sudo kill -9 `sudo lsof -t -i:8080`

sudo rm -rf Bachelors-backend

# in case no java
sudo apt-get install openjdk-11-jdk -y || true

git clone https://github.com/GIVIl3o/Bachelors-backend.git

cd Bachelors-backend

# forward tcp, i'm lazy to do something more complicated :(((
sudo iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080

sudo ./mvnw spring-boot:run &
