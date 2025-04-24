package edu.njit.cs643.fhamam;

import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.api.java.function.FilterFunction;
import static org.apache.spark.sql.functions.*;

public class Utility {
    //Constants
    public static final String APP_NAME = "CS643-Assignment2";
    public static final int THREADS_TO_USE = 4;
    
    // Clean column names for features
    public static final String[] FEATURE_COLUMNS = {
        "fixed_acidity", 
        "volatile_acidity", 
        "citric_acid", 
        "residual_sugar", 
        "chlorides", 
        "free_sulfur_dioxide", 
        "total_sulfur_dioxide", 
        "density", 
        "pH", 
        "sulphates", 
        "alcohol"
    };

    public static SparkSession initializeSparkSession() {
        SparkSession session = SparkSession.builder()
                .appName(APP_NAME)
                .getOrCreate();
        session.sparkContext().setLogLevel("OFF");
        return session;
    }

    public static Dataset<Row> readDataframeFromCsvFile(SparkSession sparkSession, String path) {
        // Check if path is absolute or relative
        String fullPath = path.startsWith("/") ? path : System.getProperty("user.home") + "/" + path;
        
        System.out.println("Reading CSV file: " + fullPath);
        
        // Read CSV without header first
        Dataset<Row> rawData = sparkSession.read()
                .option("header", false)
                .option("delimiter", ";")
                .csv(fullPath);
        
        // Print sample data
        System.out.println("==== Raw Data ====");
        rawData.show(5);
        
        // Define clean column names
        String[] cleanColumnNames = new String[] {
            "fixed_acidity", 
            "volatile_acidity", 
            "citric_acid", 
            "residual_sugar", 
            "chlorides", 
            "free_sulfur_dioxide", 
            "total_sulfur_dioxide", 
            "density", 
            "pH", 
            "sulphates", 
            "alcohol", 
            "quality"
        };
        
        // Skip the header row and rename columns
        FilterFunction<Row> headerFilter = (FilterFunction<Row>) row -> {
            String firstCol = row.getString(0);
            return !firstCol.contains("fixed") && !firstCol.contains("\"");
        };
        
        Dataset<Row> dataWithoutHeader = rawData.filter(headerFilter);
        
        // Apply column names
        for (int i = 0; i < cleanColumnNames.length; i++) {
            dataWithoutHeader = dataWithoutHeader.withColumnRenamed("_c" + i, cleanColumnNames[i]);
        }
        
        // Convert string columns to doubles
        for (String colName : cleanColumnNames) {
            dataWithoutHeader = dataWithoutHeader.withColumn(
                colName, 
                col(colName).cast("double")
            );
        }
        
        System.out.println("==== Processed Data ====");
        dataWithoutHeader.show(5);
        
        // Print schema to verify column types
        System.out.println("==== Schema ====");
        dataWithoutHeader.printSchema();
        
        return dataWithoutHeader;
    }

    public static Dataset<Row> assembleDataframe(Dataset<Row> dataframe) {
        VectorAssembler vectorAssembler = new VectorAssembler()
                .setInputCols(FEATURE_COLUMNS)
                .setOutputCol("features");
        
        Dataset<Row> assemblerResult = vectorAssembler.transform(dataframe);
        return assemblerResult.select("quality", "features");
    }

    public static void evaluateAndSummarizeDataModel(Dataset<Row> dataFrame) {
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setLabelCol("quality")
                .setPredictionCol("prediction");
        double accuracy = evaluator.setMetricName("accuracy").evaluate(dataFrame);
        double f1 = evaluator.setMetricName("f1").evaluate(dataFrame);
        System.out.println("Model Accuracy: " + accuracy);
        System.out.println("F1 Score: " + f1);
    }

    public static Dataset<Row> transformDataframeWithModel(LogisticRegressionModel model, Dataset<Row> dataFrame) {
        return model.transform(dataFrame).select("features", "quality", "prediction");
    }
}
