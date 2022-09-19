# 实验 4：完成应用程序开发

本实验中，你将通过以下步骤完成应用程序开发过程中最为重要的部分——获取智能电池设备的历史数据并在应用前端进行显示。

## 步骤 1：获取电池设备的历史数据

通过以下步骤调用 **Get Asset AI Raw Data** API 并配置 `get_asset_ai_raw_data` 函数获取智能电池设备的历史数据：

> **Get Asset AI Raw Data** 的请求格式为；`https://{apigw-address}/tsdb-service/v2.0/ai?orgId={}&modelId={}&assetIds={}&measurepoints={}&startTime={}&endTime={}&pageSize={}&accessKey={}&localTimeAccuracy={}`。

1. 在 `services.py` 文件中找到 `get_asset_ai_raw_data` 函数并参考 [EnOS™ API 文档](https://support.envisioniot.com/docs/api/zh_CN/2.3.0/overview.html) 以及以下提示尝试独立完成应用开发：

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

2. 完成代码开发后点击 **Run > Run** 启动应用程序。

3. 打开浏览器并在地址栏输入 `http://127.0.0.1:5000` 查看智能电池设备的电压、电流和温度数据图表。

<img src="media/application-6.png" style="zoom:75%;" />

## 步骤 2：更改数据上传频率

现在，你可以通过以下步骤更改应用程序的数据上传频率：

1. 在应用程序前端页面，点击 **Data Upload Frequency** 选择框打开下拉菜单。

2. 在下拉菜单中选择 **High-2s**，数据上传频率将从 5 秒更改为 2 秒。

![application-7](media/application-7.png) 

