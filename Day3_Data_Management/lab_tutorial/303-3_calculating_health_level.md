# Lab 3. Calculating the Health Level of the Battery

EnOS Stream Analytics service also provides a user-friendly UI for designing stream data processing jobs (pipelines) with StreamSets operators. You can quickly configure a pipeline by adding operators (stages) to the pipeline, thus completing data ingestion, filtering, processing, and storage tasks without programming.

In this lab, we will develop a stream data processing job with StreamSets operators to calculate the health level of the battery based on its real-time temperature. Detailed scenario of this lab is as follows:

1. Ingest and store the real-time temperature data of the battery.
2. Calculate the average temperature of the battery in every 2 minutes.
3. Compare the average temperature with the maximum temperature that the battery can work with (defined by the `UpperLimitTemp` attribute of the **SmartBattery_Demo** model).  
4. Based on the result of the comparison, output the following health levels of the battery:
  - **Level code 90**: Health (the average temperature is lower than the `UpperLimitTemp`)
  - **Level code 60**: Unhealthy (the average temperature is higher than the `UpperLimitTemp`, but not higher than 3%)
  - **Level code 30**: Extremely Unhealthy (the average temperature is higher than the `UpperLimitTemp`, and exceeding 3%)

To meet the requirement of the above business scenario, we need to use the following StreamSets operators:

| Operator                       | Description                                                  |
| ------------------------------ | ------------------------------------------------------------ |
| Data Source                    | Getting complete data records from Kafka                     |
| Point Selector                 | Specifying data records of the `SmartBattery_Demo::temp` measuring point as the input data |
| Sliding Time Window Aggregator | Aggregating the temperature data in every 2 minutes to calculate the average temperature |
| TSL Asset Lookup               | Getting the `UpperLimitTemp` attribute information of the battery model |
| Python Evaluator               | Calculating the health level of the battery by customized Python script |
| Data Destination               | Sending the output results to Kafka                          |

The business scenario is as depicted in the following figure:

![](media/streamsets_scenario.png)

## Creating a StreamSets pipeline

Take the following steps to create a StreamSets pipeline:

1. Download the StreamSets pipeline configuration template from https://support-cn5.envisioniot.com/docs/data-asset/en/latest/_static/streamsets_pipeline_demo.json (right click the link and save the `streamsets_pipeline_demo.json` file to a local directory).

2. Log in EnOS Console, select **Stream Data Processing > StreamSets**, click the triangle beside the **Create New Pipeline** button, and select **Import Pipeline**.

3. On the **Import Pipeline** window, enter a pipeline title and optional description, click **Browse ...**, navigate and select the configuration template file, and click **Import**.

   ![](media/streamsets_importing_pipeline.png)

4. Set the yarn queue for the new pipeline. Click the **Cluster** tab, set the value of the `spark.yarn.queue` field as `root.streaming_{orgId}`, in which `orgId`  is the organization ID (can be retrieved on the **IAM > Organization Profile** page of EnOS Console).

   ![](media/streamsets_yarn_queue.png)

5. Set the Kafka Consumer Group. Select the **Data Source** stage, click the **Kafka** tab, and set the value of the **Consumer Group** parameter (which must be unique within the organization). It is recommended to use the pipeline ID as the consumer group.

   ![](media/streamsets_kafka_consumer.png)


## Adding operators to the pipeline

Now we can add the needed operators to the pipeline and connect the operators with arrows to form the pipeline.

1. Select the arrow between the **Data Source** and **Data Destination** operators and click the **Delete** icon to remove the connection.

   ![](media/disconnecting_source_destination.png)

2. Click the **Stage Library** icon in the upper right corner of the page and select **EDH Streaming Calculator Library 0.0.4**.

   ![](media/selecting_stage_library.png)

3. From the list of operators, click the **Point Selector** operator to add it to the pipeline canvas.

4. Connect the output point of the **Data Source** operator to the input point of the **Point Selector** operator.

   ![](media/connecting_source_selector.png)

5. Repeat steps 3 and 4 to add the remaining operators to the pipeline and connect them by the order shown in the following figure:

   ![](media/added_all_operators.png)

6. Click the **Auto Arrange** icon ![](media/auto_arrange_icon.png) to align the display of operators in the pipeline.



## Configuring operator parameter

After the pipeline is created, we can now configure the parameters for the added operators. Select one of the operators and complete the configuration of each tab.

### Point Selector

Complete the configuration of **Input/Output** with the following settings:

| Field       | Value                   | Description                                           |
| ----------- | ----------------------- | ----------------------------------------------------- |
| Input Point | SmartBattery_Demo::temp | Getting the `temp` point data from Kafka as the input |

See the following example:

![](media/point_selector_config.png)

### Sliding Time Window Aggregator

The **Sliding Time Window Aggregator** uses a sliding window for data aggregation. Opposed to a tumbling window, the sliding window slides over the incoming stream of data. Because of this, a sliding window can be overlapping and it gives a smoother aggregation over the incoming stream of data. The following figure illustrates the difference between a tumbling window and a sliding window:

![](media/sliding_window.png)

Complete the configuration of **Input/Output** with the following settings:

| Field               | Value                       | Description                                           |
| ------------------- | --------------------------- | ----------------------------------------------------- |
| Input Point         | SmartBattery_Demo::temp     | Specifying the `temp` point data as the input         |
| Fixed Window Size   | 2                           | Specifying the duration of the time window            |
| Fixed Window Unit   | minute                      | Unit of the time window                               |
| Sliding Window Size | 1                           | Specifying the step length of the time window         |
| Sliding Window Unit | minute                      | Unit of the time window                               |
| Aggregator Policy   | avg                         | Calculating average temperature value                 |
| Output Point        | SmartBattery_Demo::temp_avg | Specifying the point that receives the output results |

See the following example:

![](media/sliding_window_config.png)

### TSL Asset Lookup

Complete the configuration of **Input/Output** with the following settings:

| Field        | Value                           | Description                                     |
| ------------ | ------------------------------- | ----------------------------------------------- |
| Input Point  | SmartBattery_Demo::temp_avg     | Receiving the average temperature data as input |
| Output Point | SmartBattery_Demo::temp_avg_out | Keeping the average temperature data as output  |

See the following example:

![](media/tsl_asset_config_1.png)

Complete the configuration of **Criteria** with the following settings:

| Field     | Value | Description                                                  |
| --------- | ----- | ------------------------------------------------------------ |
| Attribute | All   | Getting the `UpperLimitTemp` attribute information of the battery model and including it in the output results |

See the following example:

![](media/tsl_asset_config_2.png)

### Python Evaluator

Complete the configuration of **Input/Output** with the following settings:

| Field        | Value                           | Description                                                  |
| ------------ | ------------------------------- | ------------------------------------------------------------ |
| Input Point  | SmartBattery_Demo::temp_avg_out | Specifying the average temperature data as input             |
| Output Point | SmartBattery_Demo::health_level | Specifying the point that receives output results of the battery health level |

See the following example:

![](media/python_evaluator_config.png)

Under the **Script** tab, enter the following script in the **Python Script** field:

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




## Validating and running the pipeline

When the configuration of the operators is completed, we can now validate the configuration and start running the pipeline.

1. Click the **Validate** icon ![](media/validate_icon.png) in the tool bar to verify the configuration of all the operators.

   ![](media/validating_pipeline.png)

2. If the validation fails, update the configuration of the operators accordingly.

3. If the validation is successful, click the **Start** icon ![](media/start_icon.png) in the tool bar to start running the pipeline. It may take about 5 minutes for the pipeline to start up.

   ![](media/starting_pipeline.png)

4. When the pipeline is running, you can check the data processing result in the **Monitoring** section.

5. Select any stage in the pipeline to check the input data and output data of the stage.

   ![](media/streamsets_result.png)



## Viewing the health level results

After the stream data processing pipeline keeps running for a while, you can go to the **Data Insights** page to view the calculated health level results of the battery.

1. Select **Time Series Data > Data Insights** from the left navigation panel of EnOS Console to open the Data Insights page.
2. In the **Select Time Range** section, select **1H**.
3. Click the **Select Devices** input box, search for your battery device, and select it from the drop-down list. The selected device will be dynamically presented in the **Selected Measuring Points** column for selecting corresponding measuring points.  
4. In the **Selected Measuring Points** column, click on the selected device name, expand the list of measuring points, and select the **health_level** point. The queried battery health level data will be displayed in the chart on the right.

See the following example of the queried data:

![](media/queried_health_level.png)


## Next Lab

[Monitoring Health Level Alerts](303-4 monitoring_alerts.md)

