# Java Frontend Web Application

This project is a simple Spring Boot web application with a responsive frontend built using Thymeleaf, Bootstrap, and custom JavaScript.

## Features
- Homepage with dynamic task list and creation form
- REST API for tasks
- Responsive UI and accessible design
- Unit tests for service and controller layers
- Maven build and WAR packaging for Tomcat deployment
- Jenkins pipeline for EC2/Tomcat deployment

## Build
```bash
mvn clean package
```

## Run locally
```bash
mvn spring-boot:run
```

## Deploy to Tomcat
1. Build the WAR:
   ```bash
   mvn clean package
   ```
2. Copy `target/java-frontend-app.war` to Tomcat `webapps/`.

## Jenkins
The included `Jenkinsfile` builds the project, runs tests, and deploys the generated WAR to a Tomcat instance on EC2.




#############       sonarqube installation ####################


sudo apt update -y
sudo apt install -y openjdk-17-jdk

# Install SonarQube
wget https://binaries.sonarsource.com/Distribution/sonarqube/sonarqube-10.4.1.88267.zip
sudo apt install -y unzip
unzip sonarqube-10.4.1.88267.zip
sudo mv sonarqube-10.4.1.88267 /opt/sonarqube

# Create sonar user (SonarQube cannot run as root)
sudo useradd -M -d /opt/sonarqube -r -s /bin/bash sonar
sudo chown -R sonar:sonar /opt/sonarqube
sudo -u sonar /opt/sonarqube/bin/linux-x86-64/sonar.sh start


_________________________________
####      Tomcat Installation     #########

sudo apt update -y
sudo apt install -y openjdk-17-jdk

wget https://archive.apache.org/dist/tomcat/tomcat-10/v10.1.18/bin/apache-tomcat-10.1.18.tar.gz
tar -xzf apache-tomcat-10.1.18.tar.gz
sudo mv apache-tomcat-10.1.18 /opt/tomcat
sudo chmod +x /opt/tomcat/bin/*.sh
sudo /opt/tomcat/bin/startup.sh



_______________NEXUs installation__________________________________________

#!/bin/bash

set -e

echo "Updating packages..."
sudo apt-get update -y

echo "Installing required packages..."
sudo apt-get install -y wget openjdk-17-jdk

echo "Creating Nexus directories..."
sudo mkdir -p /opt/nexus
sudo mkdir -p /tmp/nexus
sudo chown -R ubuntu:ubuntu /tmp/nexus

cd /tmp/nexus

NEXUSURL="https://download.sonatype.com/nexus/3/nexus-3.85.0-03-linux-x86_64.tar.gz"

echo "Downloading Nexus..."
wget "$NEXUSURL" -O nexus.tar.gz

echo "Extracting Nexus..."
tar -xzf nexus.tar.gz

NEXUSDIR=$(find . -maxdepth 1 -type d -name "nexus-*" | head -n 1 | sed 's|^\./||')

if [ -z "$NEXUSDIR" ]; then
  echo "Nexus directory not found after extraction."
  exit 1
fi

echo "Copying Nexus files to /opt/nexus..."
sudo cp -r "$NEXUSDIR" /opt/nexus/

if [ -d "sonatype-work" ]; then
  sudo cp -r sonatype-work /opt/nexus/
else
  sudo mkdir -p /opt/nexus/sonatype-work
fi

echo "Creating nexus user if not exists..."
if ! id "nexus" &>/dev/null; then
    sudo useradd --system --no-create-home --shell /bin/false nexus
fi

echo "Setting ownership..."
sudo chown -R nexus:nexus /opt/nexus

echo "Configuring nexus.rc..."
echo 'run_as_user="nexus"' | sudo tee /opt/nexus/$NEXUSDIR/bin/nexus.rc > /dev/null

echo "Creating systemd service file..."
sudo tee /etc/systemd/system/nexus.service > /dev/null <<EOT
[Unit]
Description=Nexus Repository Manager
After=network.target

[Service]
Type=forking
LimitNOFILE=65536
User=nexus
Group=nexus
ExecStart=/opt/nexus/$NEXUSDIR/bin/nexus start
ExecStop=/opt/nexus/$NEXUSDIR/bin/nexus stop
Restart=always

[Install]
WantedBy=multi-user.target
EOT

echo "Reloading systemd..."
sudo systemctl daemon-reload

echo "Enabling Nexus service..."
sudo systemctl enable nexus

echo "Starting Nexus service..."
sudo systemctl start nexus

echo "Checking Nexus status..."
sudo systemctl status nexus --no-pager

echo "Cleaning temporary files..."
rm -rf /tmp/nexus

echo "Nexus installation completed successfully."
echo "Access Nexus at: http://<EC2-PUBLIC-IP>:8081"
echo "Default admin password file: /opt/nexus/sonatype-work/nexus3/admin.password"
