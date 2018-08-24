package com.mqtt.tcc.mqttconnect;

import android.content.Context;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Created by gabriel on 21/02/2018.
 */

public class Conectar {
    private final Context context;
    public MqttAndroidClient mqttAndroidClient;

    final String serverUri = "tcp://m14.cloudmqtt.com:18525";

    final String clientId = "MQTT_THORQUATO_TESTE_Android";
    final String subscriptionTopic = "";

    final String username = "vtlnvlod";
    final String password = "YGKB8RnFpbiZ";

    public Conectar( Context context){

        this.context= context;


    }

    public MqttAndroidClient clienteMqtt(){
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
       // connect();

        return mqttAndroidClient;
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    public void connect(){
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());

        try {

            mqttAndroidClient.connect(mqttConnectOptions);
        } catch (MqttException ex){
            ex.printStackTrace();
        }

    }

/*
    private void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("Mqtt","Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Subscribed fail!");
                }
            });

        } catch (MqttException ex) {
            System.err.println("Exceptionst subscribing");
            ex.printStackTrace();
        }
    }

*/

}