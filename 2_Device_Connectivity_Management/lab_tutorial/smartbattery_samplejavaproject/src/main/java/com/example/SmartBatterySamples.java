package com.example;

import com.envisioniot.enos.iot_mqtt_sdk.core.ConnCallback;
import com.envisioniot.enos.iot_mqtt_sdk.core.MqttClient;
import com.envisioniot.enos.iot_mqtt_sdk.core.login.LoginInput;
import com.envisioniot.enos.iot_mqtt_sdk.core.login.NormalDeviceLoginInput;
import com.envisioniot.enos.iot_mqtt_sdk.core.msg.IMessageHandler;
import com.envisioniot.enos.iot_mqtt_sdk.core.profile.DefaultProfile;
import com.envisioniot.enos.iot_mqtt_sdk.message.downstream.tsl.*;
import com.envisioniot.enos.iot_mqtt_sdk.message.upstream.tsl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Random;

import java.util.*;

/**
 *  @class SmartBatterySamples
 *  @brief Sample class to illustrate how developers can push data to EnOS
 *
 *  The class provides functions pertaining to the Smart Battery Use Case in the training documents.
 *  It explicitly allows developers to:
 *  a) Connect to EnOS via MQTT
 *  b) Upload measure points such as voltage, current and temperature
 *
 *  @note that this class is purely for demonstration only.
 *  Developers should still adhere to the design patterns required in a production grade development
 *
 */
public class SmartBatterySamples {

    private final static Logger LOG = LoggerFactory.getLogger(SmartBatterySamples.class);

    private final static String PLAIN_SERVER_URL = "tcp://mqtt-cn5.envisioniot.com:11883"; // The MQTT broker address, which can be found from EnOS Console > Help > Environment Information

    private final static String PRODUCT_KEY = "dSRsW10z"; // Product key, which is obtained after you register the device
    private final static String DEVICE_KEY = "inKJ1zYSX4"; // Device key, which is obtained after you register the device
    private final static String DEVICE_SECRET = "247B8L1rBrR1waIeNZwZ"; // Device secret, which is obtained after you register the device
    private static int interval = 5; //Data upload frequency: 5 seconds
    private static final double VOL_MAX = 26;
    private static final double VOL_MIN = 22;
    private static final double CUR_MAX = 11;
    private static final double CUR_MIN = 9;
    private static final double TEMP_MIN = 0;
    private static final double TEMP_MAX = 80;
    private static final double CUR_D_MAX = -9;
    private static final double CUR_D_MIN = -11;
    private static final int CUR_PERIOD = 60*60;
    private static final int TEMP_PERIOD = 20*60;
    private static int cur_count = 0;
    private static double temp_count = 0;
    public static String change = "current";
    private static boolean flag = true;
    private static boolean temp_flag = true;

    public static void main(String[] args) {

        LoginInput loginInput = new NormalDeviceLoginInput(PLAIN_SERVER_URL, PRODUCT_KEY, DEVICE_KEY, DEVICE_SECRET);
        final MqttClient client = new MqttClient(new DefaultProfile(loginInput));

        client.connect(new ConnCallback() {

            @Override
            public void connectComplete(boolean status) {

                if (!status) {
                    LOG.info("Connect Success.");

                    // Set service handler to handle service command from cloud
                    client.setArrivedMsgHandler(ServiceInvocationCommand.class, createServiceCommandHandler(client));
                    client.setArrivedMsgHandler(MeasurepointSetCommand.class, createMeasurePointSetHandler(client));
                    try {
                        monitor(client); //Mock method which simulates the monitoring of the battery and posting of data to EnOS
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    LOG.info("Waiting commands from cloud");
                }
                else {
                    LOG.info("Connect in-complete due to security reasons");
                    client.close();
                }
            }

            @Override
            public void connectLost(Throwable reason) {
                LOG.info("Connect Lost.");
                client.close();
            }

            @Override
            public void connectFailed(Throwable reason) {
                LOG.info("Connect Failed.");
                client.close();
            }
        });
    }

    private static IMessageHandler<MeasurepointSetCommand, MeasurepointSetReply> createMeasurePointSetHandler(final MqttClient client) {
        return (MeasurepointSetCommand arrivedMessage, List<String> argList) -> {
            byte[] bytes = arrivedMessage.encode();
            LOG.info("arrivedMessage: {}", new String(bytes));
            LOG.info("len: {}", bytes.length);
            LOG.info("argList: {}", argList);

            // argList: productKey, deviceKey, serviceName
            // If the request is for sub-device, the productKey and deviceKey
            // are used to identify the target sub-device.
            String productKey = argList.get(0);
            String deviceKey = argList.get(1);
            //String serviceName = argList.get(2);
            LOG.info("productKey: {}, deviceKey: {}",
                    productKey, deviceKey);

            return MeasurepointSetReply.builder().build();
        };
    }

    private static IMessageHandler<ServiceInvocationCommand, ServiceInvocationReply> createServiceCommandHandler(final MqttClient client) {
        return (ServiceInvocationCommand request, List<String> argList) -> {
            LOG.info("receive command: {}", request);

            // argList: productKey, deviceKey, serviceName
            // If the request is for sub-device, the productKey and deviceKey
            // are used to identify the target sub-device.
            String productKey = argList.get(0);
            String deviceKey = argList.get(1);
            String serviceName = argList.get(2);
            LOG.info("productKey: {}, deviceKey: {}, serviceName: {}, params: {}",
                    productKey, deviceKey, serviceName, request.getParams());

            LOG.info("<<<<< [service command] rcvn async serevice invocation command: " + request + " topic: " + argList);

            if (serviceName.equals("high_frequency_report_service")) {
                Map<String, Object> params = request.getParams();
                int n = (Integer) params.get("interval");
                LOG.info("arg interval: {}", n);
                interval = n;

                // Set the reply result
                return ServiceInvocationReply.builder().build();
            } else if (serviceName.equals("disconnect")) {
                Map<String, Object> params = request.getParams();
                int delayMS = (Integer) params.get("delayMS");
                LOG.info("arg delay: {}", delayMS);

                final Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        LOG.info("now close connection ...");
                        client.close();
                        timer.cancel();
                    }
                }, delayMS);
                return ServiceInvocationReply.builder().build();
            }

            return ServiceInvocationReply.builder().setMessage("unknown service: " + serviceName).setCode(220).build();
        };
    }

    // Simulate the measure points of devices
    public static Map<String, Object> simulateMeasurePoints() {
        Map<String, Object> data=new HashMap<String, Object>();
        Random random = new Random();
        data.put("temp", temp_count*(TEMP_MAX/TEMP_PERIOD));
        data.put("voltage", random.nextDouble()*(VOL_MAX - VOL_MIN) + VOL_MIN);
        data.put("current", random.nextDouble()*(CUR_MAX - CUR_MIN) + CUR_MIN);
        data.put("current_d", random.nextDouble()*(CUR_D_MAX - CUR_D_MIN) + CUR_D_MIN);

        return data;
    }

    //! post measuring point of voltage
    private static void postVoltage(final MqttClient client) {
        Map<String, Object> measurePoints = simulateMeasurePoints();
        try {
            MeasurepointPostRequest request = MeasurepointPostRequest.builder()
                    .setQos(0)
                    .addMeasurePoint("voltage", measurePoints.get("voltage"))
                    .build();

            MeasurepointPostResponse response = client.publish(request);
            if (response.isSuccess()) {
                LOG.info("measure points(voltage) are published successfully");
            } else {
                LOG.error("failed to publish measure points, error: {}", response.getMessage());
            }
        } catch (Exception e) {
            LOG.error("failed to publish measure point(voltage)", e);
        }
    }

    //! post measure point of current
    private static void postCurrent(final MqttClient client) {
        Map<String, Object> measurePoints = simulateMeasurePoints();
        try {

            // just for simulating the measure points according to battery
            cur_count += interval;
            if(cur_count >= CUR_PERIOD) {
                flag = !flag;
                cur_count = 0;
                if (flag) change = "current";
                else change = "current_d";
            }

            MeasurepointPostRequest request = MeasurepointPostRequest.builder()
                    .setQos(0)
                    .addMeasurePoint("current", measurePoints.get(change))
                    .build();

            MeasurepointPostResponse response = client.publish(request);
            if (response.isSuccess()) {
                LOG.info("measure points(current) are published successfully");
            } else {
                LOG.error("failed to publish measure points, error: {}", response.getMessage());
            }
        } catch (Exception e) {
            LOG.error("failed to publish measure point(voltage)", e);
        }
    }

    //! post measure point of temperature
    private static void postTemp(final MqttClient client) {
        Map<String, Object> measurePoints = simulateMeasurePoints();
        try {
            // just for simulating the measure points according to battery
            if (temp_flag) {
                if (temp_count >= 0 && temp_count < TEMP_PERIOD) {
                    temp_count += interval;
                } else if (temp_count >= TEMP_PERIOD) {
                    temp_flag = !temp_flag;
                    temp_count = TEMP_PERIOD - interval;
                }
            } else {
                if(temp_count > 0) {
                    temp_count -= interval;
                } else if (temp_count <= 0) {
                    temp_flag = !temp_flag;
                    temp_count = interval;
                }
            }

            MeasurepointPostRequest request = MeasurepointPostRequest.builder()
                    .setQos(0)
                    .addMeasurePoint("temp", measurePoints.get("temp"))
                    .build();

            MeasurepointPostResponse response = client.publish(request);
            if (response.isSuccess()) {
                LOG.info("measure points(Temp) are published successfully");
            } else {
                LOG.error("failed to publish measure points, error: {}", response.getMessage());
            }
        } catch (Exception e) {
            LOG.error("failed to publish measure points", e);
        }
    }

    /**
     * Method which simulates the monitoring of battery and posting of results to EnOS
     * @param client
     * @throws Exception
     */
    // Monitoring the voltage, temperature and current of device
    public static void monitor(final MqttClient client) throws Exception {
        System.out.println("post measure points start ...");
        Thread t1 = new Thread() {
            public void run() {
                while (true) {
                    postVoltage(client);
                    try {
                        Thread.sleep(interval * 1000);
                    } catch (InterruptedException e) {
                        System.out.println("post thread end.");
                    }

                }
            }
        };
        t1.start();

        Thread t2 = new Thread() {
            public void run() {
                while (true) {
                    postTemp(client);
                    try {
                        Thread.sleep(interval * 1000);
                    } catch (InterruptedException e) {
                        System.out.println("post thread end.");
                    }

                }
            }
        };
        t2.start();

        Thread t3 = new Thread() {
            public void run() {
                while (true) {
                    postCurrent(client);
                    try {
                        Thread.sleep(interval * 1000);
                    } catch (InterruptedException e) {
                        System.out.println("post thread end.");
                    }

                }
            }
        };
        t3.start();
    }
}
