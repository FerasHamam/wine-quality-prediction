#!/bin/bash

# Check if running as master or worker
if [ "$1" == "master" ]; then
    # Set up NFS server
    sudo apt install -y nfs-kernel-server
    sudo mkdir -p /mnt/models
    sudo mkdir -p /mnt/data
    cp ../data/* /mnt/data/
    sudo chmod 777 /mnt/models
    echo "/mnt/models *(rw,sync,no_subtree_check,no_root_squash)" | sudo tee -a /etc/exports
    echo "/mnt/data *(rw,sync,no_subtree_check,no_root_squash)" | sudo tee -a /etc/exports
    sudo exportfs -a
    sudo systemctl restart nfs-kernel-server
    
    # Start Spark master
    $SPARK_HOME/sbin/start-master.sh
    
    echo "Master node setup complete - Master Started!"
else
    # Set up NFS client
    sudo apt install -y nfs-common
    sudo mkdir -p /mnt/models
    sudo mkdir -p /mnt/data
    sudo mount $2:/mnt /mnt
    echo "$2:/mnt/models /mnt/models nfs rw,sync,hard,intr 0 0" | sudo tee -a /etc/fstab
    echo "/mnt/data *(rw,sync,no_subtree_check,no_root_squash)" | sudo tee -a /etc/fstab

    
    # Start Spark worker
    $SPARK_HOME/sbin/start-worker.sh spark://$2:7077
    
    echo "Worker node setup complete - Worker Started!"
fi