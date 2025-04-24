FROM openjdk:8-jdk-slim

# Install dependencies
RUN apt-get update && \
    apt-get install -y wget && \
    rm -rf /var/lib/apt/lists/*

# Install Spark
RUN wget https://archive.apache.org/dist/spark/spark-3.1.2/spark-3.1.2-bin-hadoop3.2.tgz && \
    tar -xvzf spark-3.1.2-bin-hadoop3.2.tgz && \
    mv spark-3.1.2-bin-hadoop3.2 /opt/spark && \
    rm spark-3.1.2-bin-hadoop3.2.tgz

# Set environment variables
ENV SPARK_HOME=/opt/spark
ENV PATH=$PATH:$SPARK_HOME/bin:$SPARK_HOME/sbin

# Create app directory
WORKDIR /app

# Copy JAR file
COPY target/wine-quality-prediction-1.0-SNAPSHOT.jar /app/

# Create directories for data and model
RUN mkdir -p /data
RUN mkdir -p /mnt/models/reg_model

# Copy model files
COPY reg_model /mnt/models/reg_model/

# Set command
CMD ["spark-submit", "--class", "edu.njit.cs643.fhamam.WineQualityPrediction", "--master", "local[*]", "wine-quality-prediction-1.0-SNAPSHOT.jar", "/data/TestDataset.csv"]