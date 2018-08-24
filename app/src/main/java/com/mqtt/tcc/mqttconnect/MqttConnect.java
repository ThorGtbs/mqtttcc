package com.mqtt.tcc.mqttconnect;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class MqttConnect extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {


    public MqttAndroidClient mqttAndroidClient;

    final String topicoUmidade = "thorgtbsMQTT/umidade";
    final String topicoFeedBackLuzOn1 = "thorgtbsMQTT/feedbackluz1on";
    final String topicoEnergia = "thorgtbsMQTT/energia";
    final String serverUri = "tcp://m14.cloudmqtt.com:18909";
    final String clientId = "MQTT_THORQUATO_TESTE_Android";

    final String subscriptionTopic = "";


    final String username = "txhtcwjv";
    final String password = "Fz8MLQifU88t";
    public TextView dataReceivedUmi, dataReceivedEnergia;
    public Switch lSwitch1;
    public Switch lSwitch2;

    public LineChart lineChart;
    private GraficoVHU graficoVHU;
    private GraficoVHU graficoEnergia;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt_connect);

        dataReceivedUmi = findViewById(R.id.umiVal);
        dataReceivedEnergia = findViewById(R.id.engVal);
        lineChart = findViewById(R.id.lineChart);

        // INICIA LINECHART
        graficoVHU = new GraficoVHU(lineChart, this);
        graficoVHU.iniciaGrafico();

        lSwitch1 = findViewById(R.id.switchB1);
        lSwitch2 = findViewById(R.id.switchB2);
        lSwitch1.setClickable(false);
        lSwitch2.setClickable(false);
        lSwitch1.setOnCheckedChangeListener(this);
        lSwitch2.setOnCheckedChangeListener(this);


        mqttConnect(this,serverUri,clientId);


    }


    public void assinatura() {
        try {
            mqttAndroidClient.subscribe(topicoUmidade, 0);
            mqttAndroidClient.subscribe(topicoEnergia, 0);
            mqttAndroidClient.subscribe(topicoFeedBackLuzOn1, 0);

            Toast.makeText(MqttConnect.this, "Tópico assinado", Toast.LENGTH_SHORT).show();


        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String textON = "ON";
        String textOFF = "OFF";
        String topicoluz1 = "thorgtbsMQTT/energia/luz1";
        String topicoluz2 = "thorgtbsMQTT/energia/luz2";
        // mensagem que será publicada
        String[] payload1 = {"luz1on", "luz1off"};
        String[] payload2 = {"luz2on", "luz2off"};
        // codificação em bytes do payload
        byte[] payloadCodificado;
        byte[] payloadCode;


        switch (buttonView.getId()) {

            case R.id.switchB1: {
                if (isChecked) {
                    Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
                    lSwitch1.setText(textON);
                    // tentando enviar a mensagem contida na variavel payload 1
                    try {
                        // pergando o conteudo do payload 1 e passando para utf-8
                        payloadCodificado = payload1[0].getBytes("UTF-8");
                        //instanciando o mqttmessage e passando a variavel contentdo a qtd de bytes codificados
                        MqttMessage message = new MqttMessage(payloadCodificado);
                        // utilizando uma instancia do clientemqtt para publicar a mensagem
                        mqttAndroidClient.publish(topicoluz1, message);
                    } catch (UnsupportedEncodingException | MqttException e) {
                        e.printStackTrace();

                    }

                } else {
                    lSwitch1.setText(textOFF);
                    try {
                        payloadCodificado = payload1[1].getBytes("UTF-8");
                        MqttMessage message = new MqttMessage(payloadCodificado);
                        mqttAndroidClient.publish(topicoluz1, message);


                    } catch (UnsupportedEncodingException | MqttException e) {
                        e.printStackTrace();
                    }
                }

                break;
            }
            case R.id.switchB2: {
                if (isChecked) {
                    lSwitch2.setText(textON);

                    try {
                        payloadCode = payload2[0].getBytes("UTF-8");
                        MqttMessage message = new MqttMessage(payloadCode);
                        mqttAndroidClient.publish(topicoluz1, message);
                    } catch (UnsupportedEncodingException | MqttException e) {
                        e.printStackTrace();

                    }
                    Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        lSwitch2.setText(textOFF);
                        payloadCode = payload2[1].getBytes("UTF-8");
                        MqttMessage message = new MqttMessage(payloadCode);
                        mqttAndroidClient.publish(topicoluz1, message);

                    } catch (UnsupportedEncodingException | MqttException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }

    }

    public void recebePush() {

        Intent it =new Intent( this, MqttConnect.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "recebe_push")
        .setSmallIcon(R.mipmap.illumination);
        builder.setTicker("Notificação de Uso");
        builder.setContentTitle("Luz 1");
        builder.setContentText("A Luz 1 foi ligada");
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);

        int id = 1;
        NotificationManager notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifyManager.notify(id, builder.build());

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    public MqttConnectOptions mqttConnectOptions(){

        MqttConnectOptions conectarSenha = new MqttConnectOptions();
        conectarSenha.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        conectarSenha.setUserName(username);
        conectarSenha.setPassword(password.toCharArray());

        return conectarSenha;
    }

    public MqttAndroidClient mqttConnect(Context context, String serverUri, String clientId){

        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);

        try {


                IMqttToken token = mqttAndroidClient.connect(mqttConnectOptions());
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        lSwitch1.setClickable(true);
                        lSwitch2.setClickable(true);
                        Toast.makeText(MqttConnect.this, "conectado", Toast.LENGTH_SHORT).show();
                        assinatura();

                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    }
                });

        } catch(MqttException e){
            e.printStackTrace();
        }

        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                String on = "on";


                if (topic.equals(topicoUmidade)) {

                    dataReceivedUmi.setText(message.toString() + "%");
                    //graficoVHU.addEntry(Float.valueOf(message.toString()));

                }
                if (topic.equals(topicoEnergia)) {
                    dataReceivedEnergia.setText(message.toString() + "A");
                    graficoVHU.addEntry(Float.valueOf(message.toString()));

                }

                if (topic.equals(topicoFeedBackLuzOn1)) {
                    if (on.equals(message.toString())) {
                        recebePush();
                        lSwitch1.setChecked(true);
                    } else {
                        lSwitch1.setChecked(false);
                    }

                }


            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        return mqttAndroidClient;

    }
}

