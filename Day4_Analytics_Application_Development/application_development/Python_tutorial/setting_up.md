# Setting up the Development Environment

Before developing an application with Python, we need to set up 

## Getting the Service Account

The SA consists of the `AccessKey` and `SecretKey` of the application, which can be found on the **App Detail** page of the application. Take the following steps to get the SA:

1. Log into the EnOS Console and select **Application Registration** in the left navigation bar.

2. Under the **Apps of This OU** tab, click on the **SmartBattery_Demo** application name to open the **App Detail** page.

3. Find and save the `AccessKey` and `SecretKey` of the application.

   ![](media/sa.png)

For more information about registering and managing applications, see [Registering and Managing Applications](https://support.envisioniot.com/docs/app-development/en/latest/app_management/managing_apps.html).



## Reference

If an application is newly registered on EnOS, the SA of the application must be authorized with permission to access resources (for example, asset information and ingested data) on EnOS.

In this lab, the SA of the **SmartBattery_Demo** application has already been authorized.

For detailed steps on how to authorize a service account, see [Managing Service Accounts](https://support.envisioniot.com/docs/enos/en/latest/iam/howto/service_account/managing_service_account.html).



## Next Unit

[Creating a Web Project](creating_web_project.md)