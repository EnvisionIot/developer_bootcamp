package com.example;

import com.envisioniot.enos.iot_mqtt_sdk.core.IConnectCallback;
import com.envisioniot.enos.iot_mqtt_sdk.core.MqttClient;
import com.envisioniot.enos.iot_mqtt_sdk.core.login.LoginInput;
import com.envisioniot.enos.iot_mqtt_sdk.core.login.NormalDeviceLoginInput;
import com.envisioniot.enos.iot_mqtt_sdk.core.msg.IMessageHandler;
import com.envisioniot.enos.iot_mqtt_sdk.core.profile.DefaultProfile;
import com.envisioniot.enos.iot_mqtt_sdk.message.downstream.tsl.*;
import com.envisioniot.enos.iot_mqtt_sdk.message.upstream.tsl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Random;

import java.util.*;

public class SmartBatterySamples {
    private final static Logger LOG = LoggerFactory.getLogger(SmartBatterySamples.class);

    private final static String PLAIN_SERVER_URL = "tcp://mqtt-ppe1.envisioniot.com:11883";

    private final static String PRODUCT_KEY = "tLtF7bCb";
    private final static String DEVICE_KEY = "sG97uG4zS5";
    private final static String DEVICE_SECRET = "3rWZa3IXlPJctAgeCMgF";
    private static int interval = 5; //5秒
    private static final double VOL_MAX = 26;
    private static final double VOL_MIN = 22;
    private static final double CUR_MAX = 11;
    private static final double CUR_MIN = 9;
    private static final double TEMP_MAX = 80;
    private static final double CUR_D_MAX = -9;
    private static final double CUR_D_MIN = -11;
    private static final double ACCUMULATE_POWER_MAX = 10;
    private static final int CUR_PERIOD = 60*60;
    private static final int TEMP_PERIOD = 20*60;
    private static final int POWER_PERIOD = 60*60;
    private static int cur_count = 0;
    private static double temp_count = 0;
    private static double power_count = 0;
    private static boolean flag = true;
    private static boolean temp_flag = true;
    private static boolean power_flag = true;
    public static String change = "current";

    public static void main(String[] args) {
        LoginInput input = new NormalDeviceLoginInput(PLAIN_SERVER_URL, PRODUCT_KEY, DEVICE_KEY, DEVICE_SECRET);
        final MqttClient client = new MqttClient(new DefaultProfile(input));
        //MqttClient client = new MqttClient(PLAIN_SERVER_URL, PRODUCT_KEY, DEVICE_KEY, DEVICE_SECRET); // json device
        //client.getProfile().setConnectionTimeout(60).setAutoReconnect(true);

        client.connect(new IConnectCallback() {
            public void onConnectSuccess() {
                LOG.info("Connect Success.");

                // Set service handler to handle service command from cloud
                client.setArrivedMsgHandler(ServiceInvocationCommand.class, createServiceCommandHandler(client));
                client.setArrivedMsgHandler(MeasurepointSetCommand.class, createMeasurePointSetHandler(client));
                try {
                    monitor(client);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LOG.info("Waiting commands from cloud");
            }

            public void onConnectLost() {
                LOG.info("Connect Lost.");
                client.close();
            }

            public void onConnectFailed(int reason) {
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
        BigDecimal temp = new BigDecimal(temp_count*(TEMP_MAX/TEMP_PERIOD));
        BigDecimal voltage = new BigDecimal(random.nextDouble()*(VOL_MAX - VOL_MIN) + VOL_MIN);
        BigDecimal current = new BigDecimal(random.nextDouble()*(CUR_MAX - CUR_MIN) + CUR_MIN);
        BigDecimal current_d = new BigDecimal(random.nextDouble()*(CUR_D_MAX - CUR_D_MIN) + CUR_D_MIN);
        BigDecimal accumulating_power = new BigDecimal(power_count*(ACCUMULATE_POWER_MAX/POWER_PERIOD));

        data.put("temp", temp.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
        data.put("voltage", voltage.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
        data.put("current", current.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
        data.put("current_d", current_d.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
        data.put("accumulating_power", accumulating_power.setScale(4, BigDecimal.ROUND_DOWN).doubleValue());

        return data;
    }

    // post measure point of current
    private static void calculateCurrent() {
        cur_count += interval;
        if (cur_count >= CUR_PERIOD) {
            flag = !flag;
            cur_count = 0;
            if (flag) change = "current";
            else change = "current_d";
        }
    }

    // post measure point of temperature
    private static void caculateTemp() {
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
    }

    // post measure point of temperature
    private static void caculateAccumulatingPower() {
        if (power_flag) {
            if (power_count >= 0 && power_count < POWER_PERIOD) {
                power_count += interval;
            } else if (power_count >= POWER_PERIOD) {
                power_flag = !power_flag;
                power_count = POWER_PERIOD - interval;
            }
        } else {
            if(power_count > 0) {
                power_count -= interval;
            } else if (power_count <= 0) {
                power_flag = !power_flag;
                power_count = interval;
            }
        }
    }

    // post measure point of voltage
    private static void postMeasurePoints(final MqttClient client) {
        Map<String, Object> measurePoints = simulateMeasurePoints();

        caculateTemp();
        calculateCurrent();
        caculateAccumulatingPower();

        try {
            MeasurepointPostRequest request = MeasurepointPostRequest.builder()
                    .setQos(0)
                    .addMeasurePoint("voltage", measurePoints.get("voltage"))
                    .addMeasurePoint("temp", measurePoints.get("temp"))
                    .addMeasurePoint("current", measurePoints.get(change))
                    .addMeasurePoint("accumulating_power", measurePoints.get("accumulating_power"))
                    .build();

            MeasurepointPostResponse response = client.publish(request);
            if (response.isSuccess()) {
                LOG.info("Measure points are published successfully");
            } else {
                LOG.error("failed to publish measure points, error: {}", response.getMessage());
            }
        } catch (Exception e) {
            LOG.error("failed to publish measure point(voltage)", e);
        }
    }

    // Monitoring the voltage, temperature and current of device
    public static void monitor(final MqttClient client) throws Exception {
        LOG.info("Start post measure points ...");
        while (true) {
            postMeasurePoints(client);
            Thread.sleep(interval*1000);
        }
    }

}
