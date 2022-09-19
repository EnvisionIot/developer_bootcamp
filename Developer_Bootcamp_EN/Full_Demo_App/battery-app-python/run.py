from flask import Flask, request, render_template  # Lightweight web application framework
from datetime import timedelta, datetime
from services import *

# *****application logic*****

# Initialize hte web framework
app = Flask(__name__, template_folder='./templates/', static_folder='./static/', static_url_path='/static')


# The following @app.route decorator is used to register the web request handler

@app.route('/', endpoint='index')
def index():
    return render_template('battery.html')


@app.route('/battery/list/', endpoint='battery.list')
def list():
    parent_assetId = '0RmeXD6D'
    # parent_assetId = '6Pvbj63S'  # Change this to your battery's parent assentId

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
                health_level = int(item['health_level'])
            elif 'accumulating_power' in item.keys():
                accumulating_power = float(item['accumulating_power'])

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
            if 'data' in result.keys() and result['data'] is not None:
                name = result['data']['name']
                if 'defaultValue' in name.keys() and (name['defaultValue'] != ''):
                    item['assetName'] = name['defaultValue']
                elif 'i18nValue' in name.keys():
                    i18_name = name['i18nValue']
                    if 'zh_CN' in i18_name.keys() and (i18_name['zh_CN'] != ''):
                        item['assetName'] = i18_name['zh_CN']
                    elif 'en_US' in i18_name.keys() and (i18_name['en_US'] != ''):
                        item['assetName'] = i18_name['en_US']
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
    # app.run()
    app.run(host='0.0.0.0', port=5000)
