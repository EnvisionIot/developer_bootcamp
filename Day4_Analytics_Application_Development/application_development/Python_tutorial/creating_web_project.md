# Creating a Web Project

In this unit, we will create a Python Flask web project for developing the application.

## Installing Python dependency

Ensure that have installed Python 3 and PyCharm on your workstation. If not, please refer to Day 1 part for environment setup.

1. Install Flask Web Framework:

   ```
   pip install flask
   ```

2. Install EnOS Python Core SDK. We will use this SDK to access EnOS API：

   ```
   pip install aphrodite
   ```

## Creating a project

We will use PyCharm for developing the application. 

1. Open the JetBrains PyCharm Community Edition and select **File > New Project** from the menu.

2. On the **Create Project** window:

- Browse and select the target project folder.
- Choose the existing Python interpreter and click **Create**.

<img src="media/create_project.png" style="zoom:67%;" />

3. In the project, select **File > New > Python File** from the menu and create a python file named `run.py`.

   ![create_python_file](media/create_python_file.png)

   Open the `run.py` file and add the following code:

   ```
   from flask import Flask, request, render_template  # Lightweight web framework
   from datetime import timedelta, datetime
   from services import *
   
   # *****application logic*****
   
   # Initialize hte web framework
   app = Flask(__name__, template_folder='./', static_folder='./static/')
   
   
   # The following @app.route decorator is used to register the web request handler
   
   @app.route('/', endpoint='index')
   def index():
       return render_template('battery.html')
   
   
   @app.route('/battery/list/', endpoint='battery.list')
   def list():
       parent_assetId = '0RmeXD6D'  # Change this to your battery's parent assentId
       results = get_child_asset_list(parent_assetId)
   
       resp = {}
       battery_list = {}
   
       if results is not None:
           for item in results['data']:
               battery_list[item['name']['defaultValue']] = item['assetId']
   
       # Sort the asset list in ascending order by the aseet name
       sorted_list = [{'name': k, 'assetId': battery_list[k]} for k in sorted(battery_list.keys())]
       resp['batteries'] = sorted_list
   
       return resp
   
   
   @app.route('/battery/asset/<assetId>', endpoint='battery.asset')
   def asset(assetId):
       req = get_asset_info(assetId)
       return req
   
   
   @app.route('/battery/status/<assetId>', endpoint='battery.status')
   def status(assetId):
       req = get_asset_info(assetId)
       capacity = 10
       if req is not None:
           capacity = req['data']['attributes']['Capacity']
   
       health_level = 100
       accumulating_power = 0
   
       req = get_asset_latest_data(assetId, 'health_level,accumulating_power')
       if req is not None:
           for item in req['data']['items']:
               if 'health_level' in item.keys():
                   health_level = int(item['health_level']) / 10
               elif 'accumulating_power' in item.keys():
                   accumulating_power = int(item['accumulating_power'])
   
       # Calculate the remaining power percentage of batteries
       remaining_power = "%.0f%%" % (100 * accumulating_power / capacity)
   
       resp = {'health_level': health_level, 'remaining_power': remaining_power}
   
       return resp
   
   
   @app.route('/battery/tsdb/<assetId>', endpoint='battery.tsdb')
   def tsdb(assetId):
       endTime = datetime.now()
       startTime = endTime + timedelta(hours=-1)
       startTime = startTime.strftime(format='%Y-%m-%d %H:%M:%S')
       endTime = endTime.strftime(format='%Y-%m-%d %H:%M:%S')
       req = get_asset_ai_raw_data(assetId, startTime, endTime)
   
       # Arrange the returned data in array format
       time = []
       current = []
       voltage = []
       temp = []
   
       if req is not None:
           for item in req['data']['items']:
               if 'current' in item.keys():
                   time.append(item['localtime'])
                   current.append(item['current'])
               elif 'voltage' in item.keys():
                   voltage.append(item['voltage'])
               elif 'temp' in item.keys():
                   temp.append(item['temp'])
               else:
                   pass
   
       # Assemble the response structure
       resp = {'time': time, 'voltage': voltage, 'current': current, 'temp': temp}
   
       return resp
   
   
   @app.route('/battery/alerts/', endpoint='battery.alerts')
   def alerts():
       req = get_active_alerts()
   
       # Add asset name for each alert record
       if req is not None:
           for item in req['data']:
               result = get_asset_info(item['assetId'])
               item['assetName'] = result['data']['name']['defaultValue']
   
       return req
   
   
   @app.route('/battery/service/<assetId>', endpoint='battery.service', methods=['POST'])
   def service(assetId):
       req = {}
       if request.method == 'POST':
           if request.form['command'] == 'set_frequency':
               freq = int(request.form['parameter'])
               req = set_battery_frequency(assetId, freq)
   
       res = {
           "status": "success",
           "result": req
       }
       return res
   
   
   # Main entrance
   if __name__ == '__main__':
       app.debug = True
       app.run()
   ```

4. In the project, select **File > New > Python File** from the menu and create a python file named `services.py`.

   ![create_python_file2](media/create_python_file2.png)

   Open the `services.py` file and add following code: 

   ```
   from poseidon import poseidon  # This is the python SDK for EnOS API.
   import urllib.parse as parser  # The library to convert a dictionary into query string.
   
   # The AccessKey and SecretKey, you get it when you register an application
   app_access_key = '0787eefe-d96c-4db6-a97c-c5ee16ce9a93'
   app_secret_key = '7b12307f-4993-4ef3-8554-93c6cc0e3cb6'
   
   # The api gateway address
   api_gateway = 'https://apim-ppe1.envisioniot.com'
   
   # This is the training OU Id in which we perform our experiment
   training_orgId = 'o15724268424841'
   # Asset Tree Id
   battery_provider_treeId = '50XiYXpx'
   
   
   # The following function is used to query assets under the specified parent node,
   # by specifying orgId and treeId of the asset tree.
   # Refer to `Search Related Asset Node` API doc in our doc center.
   # Args:
   #   parentId: The parent asset Id under which the child assets is located
   def get_child_asset_list(parentId):
       pass
   
   
   # The following function is used to query the specified asset information by specifying asset Id.
   # Refer to `Get Asset` API doc in our doc center.
   # Args:
   #   assetId: The asset Id you want to query
   def get_asset_info(assetId):
       pass
   
   
   # The following function is used to get the latest reading of all measurement points of a specified device.
   # Refer to `Get Asset Latest Data` API doc in our doc center.
   # Args:
   #   assetIds: The asset Id to query, multiple assets can be sperated by comma.
   #   measurepoints: The measure poin Ids to query, multiple measure points can be sperated by comma.
   def get_asset_latest_data(assetIds, measurepoints):
       pass
   
   
   # The following function is used to get the AI raw data of current, voltage and temperature
   # for the specified battery device within a certain period.
   # Refer to `Get Asset AI Raw Data` API doc in our doc center.
   # Args:
   #   assetId: The asset Id you want to query
   #   startTime: Time of start sampling data, UTC time format or local time format
   #   endTime: Time of end sampling data, UTC time format or local time format
   def get_asset_ai_raw_data(assetId, startTime, endTime):
       pass
   
   
   # The following function is used to get the current active alerts from all the batteries
   # on the battery asset tree.
   # Refer to `Search Active Alerts` API doc in our doc center.
   def get_active_alerts():
       pass
   
   
   # The following function is used to get the current active alerts from all the batteries
   # on the battery asset tree.
   # Refer to `Search Active Alerts` API doc in our doc center.
   def set_battery_frequency(assetId, frequency):
       pass
   ```

5. The final Python project structure is as follows:

<img src="media/python_project_structure.png" alt="python_project_structure" style="zoom:50%;" />

## Adding the Front End

In this step, we will extract the provided front-end package into the Python web project by the following steps:

1. Download the front-end package `Front_End_File.zip`.

2. Extract the front-end package to the `root` directory of the Python web project. Ensure that the `static` directory,  `battery.html`, and the `run.py` file are under the same directory. The directory structure is as follows:

   ```shell
   ├── root
   │   ├── static/
   │   ├── battery.html
   │   ├── run.py
   │   ├── services.py
   ```

3. In the project root directory, run the following command to start the application:

   ```
   python run.py
   ```

   ![start_app](media/start_app.png)

4. Open a browser and enter `http://127.0.0.1:5000` in the address field. View the displaying result of the application. See the following example:

   ![application](media/application-0.png)

Now, as shown in the above picture, no data is displayed. 

In the next step, we will try calling EnOS APIs to get the following battery data step by step:

- Get the battery asset list
- Get battery asset information
- Get the battery status (health level and remaining power)
- Get the asset alert records
- Set battery data uploading frequency 
- Get the dynamic data of batteries: real-time voltage, current, and temperature

## Next Unit

[Invoking EnOS APIs](invoking_api_python.md)