# Workshop Overview

## Scenario

EnOS provides REST APIs that cover core business processes on EnOS. Based on these APIs, developers can integrate devices, assets, data, and alert resources on EnOS Cloud in one application and meet the requirements of various business scenarios.

In the [Device Connectivity and Management](../../2_Device_Connectivity_Management/lab_tutorial/302-1_connecting_device_to_EnOS_cloud_python.md) lab, you have connected smart batteries to EnOS. In this workshop, you will develop a web based application with Python to query and display the list of smart battery devices, and the metrics of each smart battery, including health, remaining energy, current, voltage, temperature, and alert data. 

This workshop walks you through a typical path of how to develop an application by the following steps:

- Get the service account (SA) of the application that is registered on the EnOS Management Console
- Create a Python Flask web project and install the EnOS Python Core SDK
- Invoke EnOS API with SDK and verify the returned data
- View the queried battery data on the front-end interface

## Before You Start

Before you start this workshop, you have to ensure that:

- You have completed setting up the Python development environment.
- You have completed the lab for [Connect a Smart Battery to EnOS™](../../2_Device_Connectivity_Management/lab_tutorial/302-1_connecting_device_to_EnOS_cloud_python.md) and [Simulate Measurement Points](../../2_Device_Connectivity_Management/lab_tutorial/302-2_simulating_measure_points.md).

## Labs

This workshop includes the following labs:

> [Lab 1. Get the SA of the Application](getting_app_sa.md)
>
> 10 minutes

> [Lab 2. Create a Web Project](creating_web_project.md)
>
> 30 minutes

> [Lab 3. Invoke EnOS APIs](invoking_api_python.md)
>
> 30 minutes

> [Lab 4. Develop by Yourself](developing_by_yourself_python.md)
>
> 30 minutes

