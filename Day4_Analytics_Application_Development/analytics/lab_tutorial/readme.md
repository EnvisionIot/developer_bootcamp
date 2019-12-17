# Tutorial Overview

## Scenario

EnOS Data Explorer provides a browser-based interactive data exploration and analytics environment. We can write queries or scripts to transform data and extract insights from data. 

In this tutorial, we will use EnOS Data Explorer to extract the archived data of the battery from HDFS to a Hive table, convert the time series data of the battery to relational data table that is easier to read, and then predict the service life of the battery with a battery life prediction algorithm. 

The scenario is as depicted in the following chart:

.. image:: media/scenario.png

This tutorial walks you through a typical path of integrating, transforming, and exploring offline data of the battery, and that is:

- Extracting data from HDFS to Hive
- Converting time series data to relational data table
- Predicting battery service life

## Prerequisites

1. The uploaded data of the battery has been archived in HDFS.
2. The batch computing, data warehouse storage, and data explorer sandbox resources have been allocated for your OU. 

## Units

This tutorial includes the following units:

> [Unit 1. Extracting Data from HDFS to Hive](304-1 extracting_data.md)
>
> 20 minutes

> [Unit 2. Converting Time Series Data to Relational Data Table](converting_data.md)
>
> 30 minutes

> [Unit 3. Predicting Battery Service Life](304-2 predicting_service_life.md)
>
> 20 minutes

