# Lab 2. Simulating Measurement Points

##  Using the Device Simulator
To get started without any programing (device-end development) to verify features that require data ingestion as the prerequisite, or to troubleshoot issues, you can use the device simulator that EnOS provides. The device simulator also helps with the below.

- Getting started with EnOS: You can use this function to simulate a device to transmit data to understand how to use the capabilities of EnOS.
- Testing and debugging: During application development, you might sometimes need to simulate a device to send a specific value to verify a device function. Real devices rarely send that specific value. In this case you can use the device simulator to facilitate application development.

In this tutorial, we will simulate a smart battery that sends data to EnOS. We will insert some abnormal data into the data sample so that we can leverage the data to trigger alerts, and view the data in line chart in **Time Series Data Management > Data Insights**.

## Before You Start

- Ensure that you have registered the device to simulate and configured the TSDB storage policy for it.
- Ensure that you have access to device management. If not, contact your OU administrator to grant you the following permissions:
    - Model
    - Device Management
    - Alert
    - Times Series Data

## Step 1: Adding a Device Simulator

In the EnOS Management Console, click **Asset Management > Simulator** from the left navigation menu.

1. Click **New Simulator**, and select the device that you have registered for the simulation.

2. The device list shows all the devices that have been created in the current OU. 

> **Note**: You can only simulate a device with an **inactive** status. You cannot simulate a device that is already activated and is online or offline.

3. Click **OK** to create a simulator for the selected device.

![](media/simulator_add_new.png)

In the list of simulators, you can see the simulator you just created. 

Next, you need to define the simulation data sample for it.

![](media/simulator.png)

## Step 2. Defining and Uploading the Simulation Data Sample

1. In the list of simulators, click **... > Define Sample** for the simulator you just created.

2. Click **Download** in the pop-up window.

3. Input the simulation data sample in the downloaded template.
    
    - The first column **timeOfDay** refers to the _relative time stamp_: you can enter the relative timestamp within one day (24 hours) using the format H:MM:SS.    
    - The rest of the column headers are for the names of model points. You only need to enter the identifier of the model point to be simulated rather than entering all model points.     
    - The content under the header are point values. If the data type is array, the format is [value1, value2, value3, ...]. Leave the cell empty if there is no value at the current time point.

4. In the pop-up window, click **Upload** to upload the simulation data.

5. Click **OK**.

See [AESC_DEMO_Easy.csv](media/AESC_DEMO_Easy.csv) for a sample data upload template.

![](media/upload.png)

When we create the alert rule later, we will be setting the alert threshold to 60 milliampere. So in this sample, we need to modify the value in the `current` column so that at least one value is above 60 to trigger the alert "Current is above threshold".

The best practice is setting some values above 60 at earlier moments so that you do not have to wait long to see an alert triggered. In this tutorial, the time zone where the author is is UTC+08:00. The time is 10:00 when the author is writing this tutorial. Setting abnormal values at 0:02:00, 0:07:00, and 0:10:00, will therefore trigger alerts at 10:02:00, 10:07:00, and 10:10:00 of the author’s local time.

![](media/sim_data.png)

## Step 3: Starting the Device Simulator

You can start simulators one by one or in batch. For this tutorial, we shall set the end time for 24 hours later.

You can actually set your preferred end time, but setting it to a later time leaves enough time for TSDB to ingest enough data to generate a report.

![](media/simulator_start.png)

## Step 4: Checking the Data Insight of the Device

Go to **Time Series Data Management > Data Insights** and select the **SmartBattery_Device_a01** device to view the real-time current data report in minutes.

![](media/data_insight.png)

## Next Lab

[Monitoring Alerts of Smart Battery](302-3_monitoring_alerts_of_device.md)
