# Workshop Overview

## Scenario

EnOS provides REST APIs that cover the core business processes of the system. Based on these APIs, developers can integrate the device, asset, data, and alert resources on EnOS Cloud in one application and meet the requirements of various business scenarios.

In the [Device Connectivity and Management](https://github.com/EnvisionIot/developer_bootcamp/blob/master/Day2_Device_Connectivity_Management/lab_tutorial/302-1_connecting_device_to_EnOS_cloud.md) lab, we connected smart batteries to EnOS. In this workshop, we will develop a web based application with Python to query and display the list of smart battery devices, and the health, remaining energy, current, voltage, temperature, and alert data of each smart battery. 

This workshop will walk you through a typical path of how to develop an application using the EnOS Python Core SDK (Athena) as per the below.

- Get the service account (SA) of the application that is registered on the EnOS Management Console.
- Create a Python Flask web project and install the EnOS Python Core SDK .
- Invoke EnOS API with SDK and verify the returned data.
- Develop back-end interfaces for the data to be displayed on the front-end.
- View the queried battery data on the front-end interface.

## Before You Start

1. Ensure that you have completed setting up the Python development environment.
2. Ensure that you have completed the lab for connecting smart batteries to EnOS and uploading the simulated data to EnOS.

## Labs

This workshop includes the following labs:

> [Lab 1. Getting the SA of the Application](getting_app_sa.md)
>
> 10 minutes

> [Lab 2. Creating a Web Project](creating_web_project.md)
>
> 30 minutes

> [Lab 3. Invoking EnOS APIs](invoking_api_python.md)
>
> 30 minutes

> [Lab 4. Developing by Yourself](developing_by_yourself_python.md)
>
> 30 minutes

