#!/usr/bin/env python3

import time
import random

from enos.core.MqttClient import MqttClient
from enos.message.upstream.tsl.MeasurepointPostRequest import MeasurepointPostRequest
from enos.message.downstream.tsl.ServiceInvocationCommand import ServiceInvocationCommand
from enos.message.downstream.tsl.ServiceInvocationReply import ServiceInvocationReply

TCP_SERVER_URL = "tcp://mqtt-ppe1.envisioniot.com:21883"
PRODUCT_KEY="tLtF7bCb"
DEVICE_KEY="demo-python"
DEVICE_SECRET="sMMy5HqnCuO5Ey8H9kEvx"

# 
VOL_MAX = 4.5;
VOL_MIN = 3.8;
CUR_MAX = 42;
CUR_MIN = 40;
TEMP_MIN = 45;
TEMP_MAX = 52;
SIMULATE_PERIOD = 10

# 
interval = 1
discharge_energy = 0

def simulate_measure_points(number):
    global discharge_energy
    data = {}
    
    # 充电周期累加
    cycle_number = int(number/SIMULATE_PERIOD)
    data["cycle_number"] = cycle_number

    # 温度:随机上升, 电压:随机下降
    i = number %  SIMULATE_PERIOD
    data["temp"] = TEMP_MIN + (TEMP_MAX-TEMP_MIN)*(i+random.random())/SIMULATE_PERIOD
    data["voltage"] = VOL_MAX - (VOL_MAX-VOL_MIN)*(i+random.random())/SIMULATE_PERIOD

    # 电流随机
    data["current"] = CUR_MIN + (CUR_MAX-CUR_MIN)*random.random()

    # 放电量
    if (number%SIMULATE_PERIOD==0):
        discharge_energy = 0
    discharge_energy = discharge_energy + data["voltage"] * data["current"]
    data["discharge_energy"] = discharge_energy

    return data

def post_measure_points(data):
    request = MeasurepointPostRequest.builder() \
        .set_timestamp(int(time.time()*1000))

    for point in data:
        request.add_measurepoint(point, data[point])

    response = client.publish(request.build())
    if response:
        print('measurepoint post response code: {}, {}'.format(response.get_code(), data))

def service_command_handler(arrived_message, arg_list):
    print('receive service invocation command: {}, args: {}'.format(arrived_message, arg_list))
    product_key, device_key, service_name = arg_list
    params = arrived_message.get_params()

    if service_name == 'high_frequency_report_service':
        global interval
        interval = int(params.get('interval'))
        return ServiceInvocationReply.builder()\
            .add_output_data('result', 0)\
            .set_code(200)\
            .build()
    else:
        return ServiceInvocationReply.builder().set_message('unknown service:').set_code(220).build()

if __name__ == '__main__':
    client = MqttClient(TCP_SERVER_URL, PRODUCT_KEY, DEVICE_KEY, DEVICE_SECRET)
    client.get_profile().set_auto_reconnect(True)
    
    client.connect()  # connect in sync
    client.register_arrived_message_handler(ServiceInvocationCommand.get_class(), service_command_handler)

    i = 0
    while True:
        data = simulate_measure_points(i)
        i+=1

        post_measure_points(data)
        time.sleep(interval)
