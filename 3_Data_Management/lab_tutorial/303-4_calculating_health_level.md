# Lab 3. Calculating the Health Level of the Battery

The EnOS Stream Analytics service also provides a user-friendly UI for designing stream data processing jobs (pipelines) with StreamSets operators. You can quickly configure a pipeline by adding operators (stages) to the pipeline, completing data ingestion, filtering, processing, and storage tasks without programming.

In this lab, we will develop a stream data processing job with StreamSets operators to calculate the health level of the battery based on its real-time temperature. The detailed scenario of this lab is as per the below.

- Ingest and store the real-time temperature data of the battery.
- Calculate the average temperature of the battery every 2 minutes.
- Compare the average temperature with the maximum temperature that the battery can work with (defined by the `UpperLimitTemp` attribute of the **SmartBattery_Demo** model).  
- Based on the comparison results, output the following health levels of the battery.
  - **Level code 90**: Healthy (the average temperature is lower than the `UpperLimitTemp`)
  - **Level code 60**: Unhealthy (the average temperature is higher than the `UpperLimitTemp`, but not higher than 3%)
  - **Level code 30**: Extremely Unhealthy (the average temperature is higher than the `UpperLimitTemp`, and exceeding 3%)

To meet the requirement of the above scenario, we need to use the following StreamSets operators.

| Operator                       | Description                                                  |
| ------------------------------ | ------------------------------------------------------------ |
| EDH Kafka Consumer             | Gets complete data records from Kafka                     |
| Point Selector                 | Specifies data records of the `SmartBattery_Demo::temp` measurement point as the input data |
| Fixed Time Window Aggregator   | Aggregates the temperature data every 2 minutes to calculate the average temperature |
| TSL Asset Lookup               | Gets the `UpperLimitTemp` attribute information of the battery model |
| Python Evaluator               | Calculates the health level of the battery by customized Python script |
| EDH Kafka Producer             | Sends the output results to Kafka                          |

The scenario is as depicted in the following figure.

![](media/streamsets_scenario.png)



## Step 1: Installing StreamSets Libraries

Before developing stream processing jobs, you need to install the corresponding StreamSets calculator library.

1. Log in to the EnOS Management Console and click **Stream Processing > Streamsets Libs**.
2. Under the **Calculator Library** tab, view the StreamSets calculator libraries that can be installed.
3. Find the library to be installed and click **Install**. The system will start the installation immediately.

![](media/installing_streamsets_lib.png)


## Step 2: Creating a StreamSets Pipeline

1. Log in to the EnOS Management Console, select **Stream Processing > Stream Development**, and click the **+** icon above the list of stream processing jobs.

2. On the **New Stream** window, select **New** to create the stream processing job. You can also choose to import a configuration file to create the job quickly.

3. Enter the name and description of the stream processing job.

4. From the **Template** drop-down list, select **Origin Pipeline**.

5. From the **Operator Version** drop-down list, select the installed StreamSets calculator library version.

6. For **Message Channel**, select **Real-Time**.

7. Click **OK** to create the stream processing job with the basic settings above.

   <img src="media/creating_streamsets_pipeline.PNG" style="zoom:80%;" />



## Step 3: Adding Operators to the Pipeline

Now we can add the needed operators to the pipeline and connect the operators with arrows to form the pipeline.

1. Select the arrow between the **EDH Kafka Consumer User 1** and **EDH Kafka Producer User 1** operators and click the **Delete** icon to remove the connection.

   ![](media/disconnecting_source_destination.png)

2. Click the **Stage Library** icon in the upper right corner of the page and select **EDH Streaming Calculator Library 0.1.0**.

   ![](media/selecting_stage_library.png)

3. From the list of operators, click the **Point Selector** operator to add it to the pipeline canvas.

4. Connect the output point of the **EDH Kafka Consumer** operator to the input point of the **Point Selector** operator.

   ![](media/connecting_source_selector.png)

5. Repeat steps 3 and 4 to add the remaining operators to the pipeline and connect them by the order shown in the following figure:

   ![](media/added_all_operators.png)

6. Click the **Auto Arrange** icon ![](media/auto_arrange_icon.png) to align the display of operators in the pipeline.



## Step 4: Configuring the Operator Parameter

After the pipeline is created, we can now configure the parameters for the added operators. Select one of the operators and complete the configuration of each tab.

### Point Selector

Complete the configuration of **Input/Output** with the following settings:

| Field       | Value                   | Description                                           |
| ----------- | ----------------------- | ----------------------------------------------------- |
| Input Point | SmartBattery_Demo::temp | Gets the `temp` point data from Kafka as the input |

See the following example:

![](media/point_selector_config.png)

### Fixed Time Window Aggregator

The **Fixed Time Window Aggregator** aggregates the data of a single point by the fixed time window. The settings for this stage are as per the below.

•	Window type: Tumbling window.

•	Supported aggregators: max/min/avg/count/sum/first/last.

•	Latency settings: Support the processing of data that arrives 0~60 minutes late.

•	Early output: Support the early output of intermediate results before the time window is closed (by fixed frequency or by arriving input data).

•	This stage cannot guarantee idempotence of the calculation results due to failure retries caused by any reasons, such as cluster node exceptions.

![](media/sliding_window.png)

Complete the configuration of **Input/Output** with the following settings:

| Field               | Value                       | Description                                           |
| ------------------- | --------------------------- | ----------------------------------------------------- |
| Input Point         | SmartBattery_Demo::temp     | Specifies the `temp` point data as the input         |
| Fixed Window Size   | 2                           | Specifies the duration of the time window            |
| Fixed Window Unit   | minute                      | Unit of the time window                               |
| Aggregator Policy   | avg                         | Calculates the average temperature value                 |
| Output Point        | SmartBattery_Demo::temp_avg | Specifies the point that receives the output results |

See the following example:

![](media/sliding_window_config.png)

### TSL Asset Lookup

Complete the configuration of **Input/Output** with the following settings:

| Field        | Value                           | Description                                     |
| ------------ | ------------------------------- | ----------------------------------------------- |
| Input Point  | SmartBattery_Demo::temp_avg     | Receives the average temperature data as input |
| Output Point | SmartBattery_Demo::temp_avg_out | Keeps the average temperature data as output  |

See the following example:

![](media/tsl_asset_config_1.png)

Complete the configuration of **Criteria** with the following settings:

| Field     | Value | Description                                                  |
| --------- | ----- | ------------------------------------------------------------ |
| Attribute | All   | Gets the `UpperLimitTemp` attribute information of the battery model and includes it in the output results |

See the following example:

![](media/tsl_asset_config_2.png)

### Python Evaluator

Complete the configuration of **Input/Output** with the following settings:

| Field        | Value                           | Description                                                  |
| ------------ | ------------------------------- | ------------------------------------------------------------ |
| Input Point  | SmartBattery_Demo::temp_avg_out | Specifies the average temperature data as input             |
| Output Point | SmartBattery_Demo::health_level | Specifies the point that receives output results of the battery health level |

See the following example:

![](media/python_evaluator_config.png)

Under the **Script** tab, enter the following script in the **Python Script** field.

```
# Comparing the average temperature with the specified temperature limit
def get_health_level(temp_avg, temp_limit):
    if temp_avg > temp_limit * 1.03:
        return 30
    elif temp_avg > temp_limit:
        return 60
    else:
        return 90

# Getting the temperature data from the input data records       
for record in records:
  try:
    attrs = record.value['attr']['tslAssetLookup']['attributes']
    temp_limit = float(attrs['UpperLimitTemp'])
    temp_avg = record.value['value']
    health_level = get_health_level(temp_avg, temp_limit)

# Sending results to the specified output point    
    record.value['pointId'] = 'health_level'
    record.value['value'] = health_level
    output.write(record)

  except Exception as e:
    # Send record to error
    error.write(record, str(e))
```




## Step 5: Validating and Publishing the Pipeline

When the configuration of the operators is completed, we can now validate the configuration and publish the pipeline.

1. Click the **Validate** icon ![](media/validate_icon.png) in the tool bar to verify the configuration of all the operators.

   ![](media/validating_pipeline.png)

2. If the validation fails, update the configuration of the operators accordingly.

3. Click Save to save the configuration of the stream data processing job.

4. Click Release to publish the job online.

   ![](media/streamset_pipeline_publish.png)

   

## Step 6: Starting the StreamSets Pipeline

Before starting the stream processing job, make sure that the corresponding system pipelines are started and running. The real-time and offline message channels have 2 system pipelines (data writer and data reader) separately.

Follow the steps below to start the stream processing job.

1. Open the **Stream Operation** page, and check the status of the system pipelines under the **System Pipeline** tab.

2. In the list of system pipelines, click the **Start** icon ![start_icon](https://support.envisioniot.com/docs/data-asset/en/latest/_images/start_icon1.png) to start the required system pipeline. 

   ![](media/starting_system_pipeline.png)

3. Once the system pipeline has started, you can start your stream processing job. On the **Stream Operation** page, find the StreamSets pipelin you have published under **User Pipeline** tab, and then click the **Start** icon ![](media/start_icon.png) to start the job.  It may take about 5 minutes for the pipeline to start up.

   ![](media/start_streamset_pipeline.png)



## Step 7: Viewing the Job Running Results

On the **Stream Operation** page, find the running stream data processing job under the **User Pipeline** tab, and click the job name to open the **Stream Details** page. You can view the following information about the job.

- **Summary**: View the summary of the running stream, such as the count of processed data records and the record throughput.

  ![](media/streamset_result_summary.png)

- **Log**: Click the **View Logs** icon on the upper right corner to check the running log of the job.

- **Results**: The processed data will be stored in TSDB according to the configured storage policy.



After the stream data processing pipeline has run for a while, you can go to the **Data Insights** page to view the calculated health level results of the battery.

1. Select **Time Series Data Management > Data Insights** from the left navigation menu to open the **Data Insights** page.
2. In the **Select Time Range** section, select **1h**.
3. Click the **Select Devices** input box, search for your battery device, and select it from the drop-down list. The selected device will be dynamically presented in the **Selected Measuring Points** column for you to select the corresponding measurement points.  
4. In the **Selected Measuring Points** column, click on the selected device name, expand the list of measurement points, and select the **health_level** point. The queried battery health level data will be displayed in the chart on the right.

See the following example of the queried data:

![](media/queried_health_level.png)



## Next Lab

[Monitoring Health Level Alerts](303-4_monitoring_alerts.md)

