# 教程概览

## 业务场景

EnOS™ 开放涵盖系统各个核心业务流程的 REST API 接口。开发者可以基于这些接口 EnOS™ Cloud 系统内的设备、资产、数据以及告警资源并开发各类应用满足复杂业务场景需要。

在 [设备连接与管理](../../2_Device_Connectivity_Management/lab_tutorial/302-1_connecting_device_to_EnOS_cloud_python.md) 中，你已将智能电池设备连接到了 EnOS。本部分中，你将基于 Python 开发一个网页应用程序用于查询并显示智能电池设备列表以及单个电池设备的剩余电量、电流、电压、温度和告警信息。

你将尝试利用 EnOS Python Core SDK (Athena) 通过以下步骤开发应用程序：

- 获取已在 EnOS 管理控制台注册的应用的服务账号（SA）
- 创建 Python Flask Web 项目并安装 EnOS Python Core SDK
- 调用 EnOS API 并验证返回数据
- 在前端界面查询电池数据

## 前提条件

- 你已设置完成 Python 开发环境。
- 你已完成 [将智能电池设备接入 EnOS™](../../2_Device_Connectivity_Management/lab_tutorial/302-1_connecting_device_to_EnOS_cloud_python.md)  和 [模拟测点数据](../../2_Device_Connectivity_Management/lab_tutorial/302-2_simulating_measure_points.md) 的相关内容。

## 实验

该教程包含以下实验：

[实验 1：获取应用程序服务账号](getting_app_sa.md)

10 分钟

[实验 2：创建 Web 项目](creating_web_project.md)

30 分钟

[实验 3：调用 EnOS API](invoking_api_java.md)

30 分钟

[实验 4：完成应用程序开发](developing_by_yourself_java.md)

30 分钟

