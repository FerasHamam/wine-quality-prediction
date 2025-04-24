#!/bin/bash

# Install Java
sudo apt update
sudo apt install -y openjdk-8-jdk maven

# Install Spark
wget https://archive.apache.org/dist/spark/spark-3.1.2/spark-3.1.2-bin-hadoop3.2.tgz
tar -xzvf spark-3.1.2-bin-hadoop3.2.tgz
sudo mv spark-3.1.2-bin-hadoop3.2 /opt/spark
rm spark-3.1.2-bin-hadoop3.2.tgz

