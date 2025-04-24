# Wine Quality Prediction Using Apache Spark ML

A scalable machine learning application for predicting wine quality using Apache Spark's MLlib. This application demonstrates distributed training and prediction on a wine quality dataset.


## Architecture

The application consists of two main components:

1. **Model Training Application**: Runs on a Spark cluster for distributed training
2. **Prediction Application**: Runs either locally or in a Docker container

Both components share access to models and data through an NFS mount.

## Requirements

- Apache Spark 3.x
- Java 8+
- Maven
- EC2 instances (for distributed training) - Mine was 4 instances (1 Master, 3 Workers)
- Docker (for containerized prediction)

## Quick Start

### Setup Cluster

1. Clone the repository on the master node:
   ```bash
   git clone https://github.com/FerasHamam/wine-quality-prediction.git
   cd wine-quality-prediction
   ```

2. Copy setup scripts to worker nodes:
   ```bash
   scp scripts/setup-cluster.sh scripts/setup-nfs.sh ec2-user@worker-node:~/
   ```

3. Set up Spark on all nodes:
   ```bash
   # On Master node
   cd ~/scripts
   # On each node
   ./setup-cluster.sh
   echo 'export SPARK_HOME=/opt/spark' >> ~/.bashrc
   echo 'export PATH=$PATH:$SPARK_HOME/bin:$SPARK_HOME/sbin' >> ~/.bashrc
   source ~/.bashrc
   ```

4. Set up NFS file sharing:
   ```bash
   # On master node
   sudo ./setup-nfs.sh master
   
   # On worker nodes
   ./setup-nfs.sh worker <master-private-ip>
   ```

### Build and Run

1. Build the application on the master node:
   ```bash
   cd ~/wine-quality-prediction
   mvn clean package
   ```

2. Run the training job:
   ```bash
   cd scripts
   ./run-training.sh <master-private-ip>
   ```

3. Run prediction locally:
   ```bash
   ./run-prediction.sh local /path/to/TestDataset.csv
   # /path/to/TestDataset.csv is /mnt/data/TestDataset.csv based on setup-nfs.sh script
   ```
   
   Or using manual Spark submit:
   ```bash
   spark-submit --class edu.njit.cs643.fhamam.WineQualityPrediction \
      --master local[*] \
      target/wine-quality-prediction-1.0-SNAPSHOT.jar \
      /home/ubuntu/wine-quality-prediction/data/TestDataset.csv \
      /mnt/models/reg_model/
   ```

4. Run prediction with Docker:
   ```bash
   ./install-docker.sh
   ./run-prediction.sh docker

   # Alterantive
   docker run -v $(pwd)/data:/data ferasouihamam/wine-quality-prediction:latest
   ```

## Implementation Details

### Training Process
- Data preprocessing with column standardization
- Feature vector assembly
- Model training with hyperparameter tuning
- Validation against test dataset
- Model persistence to shared storage

### Prediction Process
- Model loading from shared storage
- Feature transformation
- Quality prediction
- Performance evaluation metrics
- Feature importance visualization

## Directory Structure

```
wine-quality-prediction/
├── data/                      # Wine quality datasets
├── scripts/                   # Setup and execution scripts
│   ├── setup-cluster.sh       # Spark cluster setup
│   ├── setup-nfs.sh           # NFS configuration
│   ├── run-training.sh        # Training job execution
│   └── run-prediction.sh      # Prediction execution
├── src/                       # Source code
│   └── main/java/edu/njit/cs643/fhamam/
│       ├── ModelTraining.java     # Training application
│       ├── WineQualityPrediction.java  # Prediction application
│       └── Utility.java       # Utility functions
├── target/                    # Compiled code
└── README.md                  # This file
```

## Contributors

- Feras Hamam (fih@njit.edu)