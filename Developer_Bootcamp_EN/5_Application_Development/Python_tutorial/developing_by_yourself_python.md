# Lab 4. Develop by Yourself

In this lab, you need to complete the most important part of the application development: 

Get the battery history data and display the data in the front end.

## Step 1: Get the History Data of a Specific Battery

Invoke **Get Asset AI Raw Data** API and configure the `get_asset_ai_raw_data` function by the following steps to get the history data of a specific battery device:

> The request format of **Get Asset AI Raw Data** is `https://{apigw-address}/tsdb-service/v2.0/ai?orgId={}&modelId={}&assetIds={}&measurepoints={}&startTime={}&endTime={}&pageSize={}&accessKey={}&localTimeAccuracy={}`.

1. In the `services.py` file, find the `get_asset_ai_raw_data` stub function. Refer to the EnOS API documentation, and try to finish it by yourself.

   ```
   # The following function is used to get the AI raw data of battery current, voltage and temperature
   # for the specified battery device within a certain period.
   # Refer to `Get Asset AI Raw Data` API documentation on EnOS Console.
   # Args:
   #   assetId: The asset Id you want to query
   #   startTime: Time of start sampling data, UTC time format or local time format
   #   endTime: Time of end sampling data, UTC time format or local time format
   # Returns:
   #   Just return the EnOS API results
   def get_asset_ai_raw_data(assetId, startTime, endTime):
       pass
   ```

2. After you have completed the code development, click **Run > Run** to start the application.

3. Open a browser and enter `http://127.0.0.1:5000` in the address field. Check the data charts for the battery voltage, current, and temperature. 

<img src="media/application-6.png" style="zoom:75%;" />

## Step 2: Change Data Uploading Frequency

Now, you can change the data uploading frequency of the application by the following steps:

1. In the frontend page, click **Data Upload Frequency** box to open a dropdown list.
2. Select **High-2s** from the dropdown list to change the data upload frequency from 5 seconds to 2 seconds.

![application-7](media/application-7.png) 

