# Lab 1: Data ETL

Use Data Explorer to load the data from HDFS to Hive, and transform the data into a format that is ready for analytics.

## Extracting Data from HDFS to Hive

In this lab, we will first use the Zeppelin Notebook to extract the archived data of the battery from HDFS to Hive with shell and hive scripts.

### Creating a Zeppelin note

Take the following steps to create a Zeppelin note:

1. Log in EnOS Console and select **Data Explorer** from the left navigation panel.

2. Find a sandbox with *Running* status, or click the **Start** icon in the Operations column to start a sandbox. 

3. Click **Zeppelin** from the **URL** column to open the Zeppelin notebook.

   ![](media/data_explorer_sandbox.png)

4. Click the **Create new note** button, enter a name for the note (or a name with path), select **hive** as the default interpreter, and click **Create**. The Zeppelin note will be opened directly.

   <img src="media/creating_note.png" style="zoom:60%;"/>


### Viewing archived files in HDFS

Take the following steps to view the archived data of the battery in HDFS:

1. In the Zeppelin note, type the following scripts for viewing all the archived files in the specified path:

   ```
   %sh
   # View the current user
   whoami
   
   # List the files under the user's directory
   hadoop fs -ls -R /user/$(whoami)/smart_battery/
   ```

   **Note**: In the above example, the path `smart_battery` is specified in the data archiving job that is created in the [Archiving Uploaded Data of the Battery](../../../D3_Data_Management/Labs/stream_data_processing_tutorial/archiving_data.md) lab. You may need to replace it with the path you specified.

2. Click the **Run this paragraph** icon to run the scripts. See the following example:

   ![](media/viewing_file_script.png)

3. View the running results of the scripts. See the following example:

   ![](media/viewing_file_result.png)

4. In the note, type the following script to show the content of your data file file:

   ```
   %sh
   hadoop fs -cat /user/$(whoami)/smart_battery/20191115/07/data_20191115T0700_bINF3y.csv|head -5
   ```

5. Run the scripts and view the running results. See the following example:

   ![](media/viewing_data_result.png)



### Creating a Hive table

Take the following steps to create a Hive table with columns defined to receive the archived battery data:

1. In the Zeppelin note, type the following scripts:

> Note that you'll need to replace `data_o15724268424841` with your own.

   ```
   %hive
   DROP TABLE IF EXISTS `smart_battery`;
   
   CREATE EXTERNAL TABLE IF NOT EXISTS `smart_battery`(
   `orgId` string comment 'orgId',
   `modelId` string comment 'modelId',
   `assetId` string comment 'assetId',
   `measurepoints` string comment 'measurepoints',
   `timestamp` string comment 'timestamp',
   `value` string comment 'value',
   `quality` string comment 'quality')
   ROW FORMAT DELIMITED
   FIELDS TERMINATED BY ','
   LINES TERMINATED BY '\n'
   STORED AS TEXTFILE
   LOCATION '/user/data_o15724268424841/smart_battery/20191115/06/';
   
   MSCK REPAIR TABLE `smart_battery`;
   ```

2. Run the scripts and view the running results. See the following example:

   ![](media/creating_hive_result.png)

3. In the note, type the following scripts for viewing data in the created Hive table:

   ```
   %hive
   select * from smart_battery limit 5;
   ```

4. Run the scripts and view the running results. See the following example:

   ![](media/viewing_hive_result.png)



## Converting Time Series Data to Relational Data Table

The data of the battery archived to HDFS is time series data, which is featured by "1 row containing data of 1 measuring point". Before data analysis, we need to convert the time series data table into a relational data table, in which measuring point data is displayed in columns.



### Creating a relational data table

Take the following steps to create a relational data table for the extracted battery data:

1. In the Zeppelin note, type the following scripts:

   ```
   %hive
   use data_o15724268424841;
   DROP TABLE smart_battery_format;
   
   CREATE EXTERNAL TABLE IF NOT EXISTS `smart_battery_format`(
   `asset_id` string,
   `time` string,
   `temp` string,
   `current` string,
   `voltage` string,
   `cycle_number` string,
   `SOH` string)
   
   ROW FORMAT DELIMITED
   FIELDS TERMINATED BY '\t'
   LINES TERMINATED BY '\n';
   ```

   **Note**: In this example, `data_o15724268424841` is the user ID. You may need to replace it with the user ID of your organization.

2. Run the scripts and view the running results. See the following example:

   ![](media/creating_relational_result.png)

3. In the note, type the following scripts for viewing the created relational data table:

   ```
   %hive
   select * from smart_battery_format limit 5;
   ```

4. Run the scripts and view the running results. See the following example:

   ![](media/viewing_relational_result.png)



### Optimizing Hive job parameters

Before performing the "column-to-row" operation, we will need to optimize Hive job parameters.

1. In the note, type the following scripts:

   ```
   %hive
   
   set mapreduce.job.queuename=root.haicheng;
   set hive.exec.dynamic.partition.mode=nonstrict;
   set hive.exec.dynamic.partition=true;
   set hive.exec.max.dynamic.partitions.pernode=10000;
   set hive.exec.max.dynamic.partitions=10000;
   ```

   **Note**: In this example, `root.haicheng` is the queue name of the data explorer instance. You may need to replace it with the queue name of your sandbox.

2. Run the scripts and view the running results. See the following example:

   ![](media/optimizing_hive.png)



### Converting rows to columns

Take the following steps to perform the "column-to-row" operation on the created relational data table and insert the battery data into the table:

1. In the note, type the following scripts:

   ```
   %hive
   insert overwrite table smart_battery_format
   
   select * from (
   select  a.`assetId` as asset_id, a.`timestamp` as time,
   max(case when a.`measurepoints` = 'temp' then a.value end) as temp,
   max(case when a.`measurepoints` = 'current' then a.value end) as `current`,
   max(case when a.`measurepoints` = 'voltage' then a.value end) as voltage,
   max(case when a.`measurepoints` = 'cycle_number' then a.value end) as cycle_number,
   max(case when a.`measurepoints` = 'SOH' then a.value end) as SOH
   
   from (
   select `assetId`, `measurepoints`, `timestamp`, `value` from
   smart_battery) a
   group by a.`assetId`, a.`timestamp`
   
   ) b
   sort by b.`asset_id`, b.`time`;
   ```

2. Run the scripts and view the running results. It may take 3-5 minutes to complete the conversion. See the following example:

   ![](media/viewing_conversion_result.png)

3. In the note, type the following scripts for viewing the converted battery data:

   ```
   %hive
   select * from smart_battery_format;
   ```

4. Run the scripts and view the running results. See the following example:

   ![](media/viewing_conversion_result_1.png)

5. After conversion, the Hive table will be stored as a text file in HDFS. In the note, type the following scripts for viewing the stored file:

   ```
   %sh
   hadoop fs -ls /user/hive/warehouse/$(whoami).db/smart_battery_format
   ```

6. Run the scripts and view the running results. See the following example:

   ![](media/viewing_hive_table_files.png)



## Next Lab

[Predicting Battery Service Life](304-2_predicting_service_life.md)
