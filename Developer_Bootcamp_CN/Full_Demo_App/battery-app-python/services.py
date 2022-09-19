from poseidon import poseidon  # This is the python SDK for EnOS API.
import urllib.parse as parser  # The library to convert a dictionary into query string.

# The AccessKey and SecretKey, you get it when you register an application
app_access_key = '0787eefe-d96c-4db6-a97c-c5ee16ce9a93'
app_secret_key = '7b12307f-4993-4ef3-8554-93c6cc0e3cb6'
# The api gateway address
api_gateway = 'https://apim-ppe1.envisioniot.com'

# This is the training OU Id in which we perform our experiment
training_orgId = 'o15724268424841'
# This is **Asset Tree Id** where batteries is mounted on, if you create your own asset tree,
# please replace it wit your own.
battery_provider_treeId = '50XiYXpx'


# The following function is used to query assets under the specified parent node,
# by specifying orgId and treeId of the asset tree.
# Refer to `Search Related Asset Node` API doc in our doc center.
# Args:
#   parentId: The parent asset Id under which the child assets is located
def get_child_asset_list(parentId):
    url = api_gateway + '/asset-tree-service/v2.1/asset-nodes?'

    query_data = {
        'action': 'searchRelatedAsset',
        'treeId': battery_provider_treeId,
        'orgId': training_orgId}

    query_string = parser.urlencode(query_data)
    url += query_string

    data = {
        "filter": {
            "isChildOfAssetId": parentId  # Specify the parent node Id for this query
        },
        "projection": ["assetId", "name.defaultValue"]
    }

    # poseidon.urlopen is the function defined in our SDK to send an API request.
    req = poseidon.urlopen(app_access_key, app_secret_key, url, data)
    return req


# The following function is used to query the specified asset information by specifying asset Id.
# Refer to `Get Asset` API doc in our doc center.
# Args:
#   assetId: The asset Id you want to query
def get_asset_info(assetId):
    url = api_gateway + '/asset-service/v2.1/assets?'

    query_data = {
        'action': 'get',
        'orgId': training_orgId,
        'assetId': assetId}

    query_string = parser.urlencode(query_data)
    url += query_string

    req = poseidon.urlopen(app_access_key, app_secret_key, url)

    return req


# The following function is used to get the latest reading of all measurement points of a specified device.
# Refer to `Get Asset Latest Data` API doc in our doc center.
# Args:
#   assetIds: The asset Id to query, multiple assets can be sperated by comma.
#   measurepoints: The measure poin Ids to query, multiple measure points can be sperated by comma.
def get_asset_latest_data(assetIds, measurepoints):
    url = api_gateway + '/tsdb-service/v2.0/latest?'

    query_data = {
        'orgId': training_orgId,
        'assetIds': assetIds,
        'measurepoints': measurepoints,
        'timeWindow': '',
        'accessKey': app_access_key}

    query_string = parser.urlencode(query_data)
    url += query_string

    req = poseidon.urlopen(app_access_key, app_secret_key, url)
    return req


# The following function is used to get the AI raw data of current, voltage and temperature
# for the specified battery device within a certain period.
# Refer to `Get Asset AI Raw Data` API doc in our doc center.
# Args:
#   assetId: The asset Id you want to query
#   startTime: Time of start sampling data, UTC time format or local time format
#   endTime: Time of end sampling data, UTC time format or local time format
def get_asset_ai_raw_data(assetId, startTime, endTime):
    url = api_gateway + '/tsdb-service/v2.0/ai?'

    query_data = {
        'orgId': training_orgId,
        'modelId': '',
        'assetIds': assetId,
        'measurepoints': 'current,voltage,temp',
        'startTime': startTime,
        'endTime': endTime,
        'pageSize': '',
        'accessKey': app_access_key}

    query_string = parser.urlencode(query_data)
    url += query_string

    req = poseidon.urlopen(app_access_key, app_secret_key, url)

    return req


# The following function is used to get the current active alerts from all the batteries
# on the battery asset tree.
# Refer to `Search Active Alerts` API doc in our doc center.
def get_active_alerts():
    url = api_gateway + '/event-service/v2.1/active-alerts?'

    query_data = {
        'action': 'search',
        'orgId': training_orgId}

    query_string = parser.urlencode(query_data)
    url += query_string

    data = {
        "scope": {
            "treeId": battery_provider_treeId  # Specify the asset tree from which to search active alerts
        },
        "pagination": {
            "pageNo": 1,
            "pageSize": 10
        }
    }

    req = poseidon.urlopen(app_access_key, app_secret_key, url, data)
    return req


# The following function is used to get the current active alerts from all the batteries
# on the battery asset tree.
# Refer to `Search Active Alerts` API doc in our doc center.
def set_battery_frequency(assetId, frequency):
    url = api_gateway + '/connect-service/v2.1/commands?'

    query_data = {
        'action': 'invokeService',
        'orgId': training_orgId,
        'assetId': assetId,
        'serviceId': 'high_frequency_report_service',
        'pendingTtl': 1000}

    query_string = parser.urlencode(query_data)
    url += query_string

    print(url)

    data = {
        "inputData": {
            "interval": frequency
        }
    }

    req = poseidon.urlopen(app_access_key, app_secret_key, url, data)
    return req
