# Tutorial Overview

## Scenario

EnOS provides REST APIs that cover the core business processes of the system. Based on these APIs, developers can integrate the device, asset, data, and alert resources on EnOS Cloud in one application, meeting the requirements of various business scenarios.

In the [Device Connectivity and Management](http://git.envisioncn.com/knowledge-transfer/bootcamp/blob/master/D2_Device_Connectivity&Management/Labs/device_management_tutorial/302-1%20connecting_device_to_EnOS_cloud.md) lab, we have connected smart batteries into EnOS. In this tutorial, we will develop a web based application with Python to query and display the list of smart battery devices, the health, remaining energy, current, voltage, temperature, and alert data of each smart battery. 

This tutorial walks you through a typical path of developing an application using the EnOS Python Core SDK (Athena), and that is:

- Setting up the development environment
- Getting the SA (service account) of the application that is registered on the EnOS Console.
- Checking the connectivity of batteries
- Developing back-end interfaces for data to be displayed on the front-end
- Viewing the queried battery data on the front-end interface

## Prerequisites

1. You have completed setting up the Python development environment.
2. You have completed the lab for connecting smart batteries into EnOS and uploading the simulated data to EnOS.

## Units

This tutorial includes the following units:

> [Unit 1. Setting up Development Environment](setting_up.md)
>
> 20 minutes

> [Unit 2. Getting the SA of the Application](getting_app_sa.md)
>
> 10 minutes

> [Unit 3. Checking Connectivity of Batteries](checking_connectivity.md)
>
> 30 minutes

> [Unit 4. Developing Back-end Interfaces](developing_front_end.md)](developing_back_end.md)
>
> 30 minutes

> [Unit 5. Developing the Front End](developing_front_end.md)
>
> 30 minutes