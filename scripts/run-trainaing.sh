#!/bin/bash

cd ~/wine-quality-prediction

spark-submit \
  --class edu.njit.cs643.fhamam.ModelTraining \
  --master spark://$1:7077 \
  --deploy-mode client \
  target/wine-quality-prediction-1.0-SNAPSHOT.jar /mnt/data/TrainingDataset.csv /mnt/data/ValidationDataset.csv