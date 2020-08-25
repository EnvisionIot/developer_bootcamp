# Lab 1. Getting the SA of the Application

When invoking EnOS APIs, the SA of the application is required as the identity of the application for API request authentication. Before developing an application based on EnOS APIs, we need to get the SA of the application on the EnOS Management Console.

The SA will be automatically generated when an application is registered on the EnOS Management Console. In this lab, the application is already registered. 

## Step 1: Getting the SA

The SA consists of the `AccessKey` and `SecretKey` of the application, which can be found on the **App Detail** page of the application. Follow the steps below to get the SA.

1. Log in to the EnOS Management Console and select **Application Registration** in the left navigation menu.

2. Under the **Apps of This OU** tab, click on the **SmartBattery_Demo** application name to open the **App Detail** page.

3. Find and save the `AccessKey` and `SecretKey` of the application.

   ![](media/sa.png)

For more information about registering and managing applications, see [Registering and Managing Applications](https://support.envisioniot.com/docs/app-development/en/latest/app_management/managing_apps.html).



## Reference

If an application is newly registered on EnOS, the SA of the application must be authorized with permission to access the resources (for example, asset information and ingested data) on EnOS.

In this lab, the SA of the **SmartBattery_Demo** application has already been authorized.

For detailed steps on how to authorize a service account, see [Managing Service Accounts](https://support.envisioniot.com/docs/enos/en/latest/iam/howto/service_account/managing_service_account.html).



## Next Unit

[Creating a Web Project](creating_web_project.md)