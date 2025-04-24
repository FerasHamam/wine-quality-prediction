#!/bin/bash


cd ~/wine-quality-prediction

# Run prediction without Docker
if [ "$1" == "local" ]; then
    spark-submit \
      --class edu.njit.cs643.fhamam.WineQualityPrediction \
      --master local[*] \
      target/wine-quality-prediction-1.0-SNAPSHOT.jar $2
# Run prediction with Docker
elif [ "$1" == "docker" ]; then
    docker run -v $(pwd)/data:/data ferasouihamam/wine-quality-prediction:latest $2
else
    echo "Usage: ./run-prediction.sh [local|docker]"
fi