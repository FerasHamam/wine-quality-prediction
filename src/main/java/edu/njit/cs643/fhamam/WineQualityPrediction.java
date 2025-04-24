package edu.njit.cs643.fhamam;

import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;


public class WineQualityPrediction {
    public static void main(String[] args) {
        System.out.println("Wine Quality Prediction Application");
        System.out.println("Initializing Spark:");
        SparkSession sparkSession = Utility.initializeSparkSession();
        
        //Check if path is given in args -> use validation dataset since there is no test dataset
        String testPath = args.length > 0 ? args[0] : "TestDataset.csv";
        String modelPath = args.length > 1 ? args[1] : "/mnt/models/reg_model";
        
        System.out.println("Loading Test Data Set");
        //Load Test Dataframe
        Dataset<Row> testingDataFrame = Utility.readDataframeFromCsvFile(sparkSession, testPath);
        Dataset<Row> assembledTestDataFrame = Utility.assembleDataframe(testingDataFrame);
        
        System.out.println("Loading Training Model");
        //Load Training Model
        LogisticRegressionModel lrModel = LogisticRegressionModel.load(modelPath);
        
        System.out.println("Predicting using Trained Model and Test Data");
        //Predict using Test Dataframe
        Dataset<Row> predictionData = Utility.transformDataframeWithModel(lrModel, assembledTestDataFrame);
        predictionData.show();
        
        System.out.println("Evaluation Results:");
        Utility.evaluateAndSummarizeDataModel(predictionData);
        
        sparkSession.stop();
    }
}
