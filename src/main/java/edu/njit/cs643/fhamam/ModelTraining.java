package edu.njit.cs643.fhamam;

import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.IOException;


public class ModelTraining {
    /**
     * Main Method
     * - Application Entry Point
     * @param args Command Line Arguments
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Model Training Application");
        System.out.println("Initializing Spark:");
        SparkSession sparkSession = Utility.initializeSparkSession();
        
        String trainingPath = args.length > 0 ? args[0] : "TrainingDataset.csv";
        String validationPath = args.length > 1 ? args[1] : "ValidationDataset.csv";
        
        System.out.println("Training Logistic Regression Model...");
        //Initial Training
        Dataset<Row> wineDataFrame = Utility.readDataframeFromCsvFile(sparkSession, trainingPath);
        Dataset<Row> assemblyResult = Utility.assembleDataframe(wineDataFrame);
        
        LogisticRegression logisticRegression = new LogisticRegression()
                .setFeaturesCol("features")
                .setRegParam(0.45)
                .setMaxIter(45)
                .setLabelCol("quality");
        
        LogisticRegressionModel lrModel = logisticRegression.fit(assemblyResult);
        
        System.out.println("Validating Trained Model");
        //Validating Trained Model
        Dataset<Row> validationDataFrame = Utility.readDataframeFromCsvFile(sparkSession, validationPath);
        Dataset<Row> assembledValidationDataFrame = Utility.assembleDataframe(validationDataFrame);
        Dataset<Row> modelTransformationResult = Utility.transformDataframeWithModel(lrModel, assembledValidationDataFrame);
        
        System.out.println("Validation Results");
        //Print Results
        Utility.evaluateAndSummarizeDataModel(modelTransformationResult);
        
        System.out.println("Saving trained model.");
        //Save new model.
        lrModel.write().overwrite().save("/mnt/models/reg_model");
        
        sparkSession.stop();
    }
}
