# Lab 4. Developing by Yourself

In this lab, you will need to complete the most important part of the application development:

<u>*Get the battery history data and display it in the front end.*</u>

The time for you is **30 minutes**.

## Step 1: Getting the History Data of a Specific Battery

> To get the battery history data, you can invoke the EnOS API *Get Asset AI Raw Data*. The request format is:
>
> https://{apigw-address}/tsdb-service/v2.0/ai?orgId={}&modelId={}&assetIds={}&measurepoints={}&startTime={}&endTime={}&pageSize={}&accessKey={}&localTimeAccuracy={}

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

2. **30 minutes starts now**

3. After you have completed the code development, click **Run > Run** to start the application.

4. Open a browser and enter `http://127.0.0.1:5000` in the address field. Check the data charts for the battery voltage, current, and temperature. See the following example:

<img src="media/application-6.png" style="zoom:75%;" />

## Step 2: Changing Data Uploading Frequency

We can now check the results of the changed battery data uploading frequency.

Change the high frequency mode to (2s), and the data uploading frequency will be changed from 5s to 2s. See the following example:

![application-7](media/application-7.png) 

