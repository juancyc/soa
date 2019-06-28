package com.example.smartender;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BTHandler {

    Handler bluetoothIn;
    final int handlerState = 0;
    public BluetoothAdapter btAdapter = null;
    public BluetoothSocket btSocket = null;
    private StringBuilder DataStringIN = new StringBuilder();
    public ConnectedThread MyConexionBT;
    // Identificador unico de servicio - SPP UUID
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String para la direccion MAC
    private static String address = null;
    public boolean btConeccted;

    public BTHandler(){
        bluetoothIn = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == handlerState){
                    String readMessage = (String) msg.obj;
                    DataStringIN.append(readMessage);
                    /* ACA VA LA LECTURA DEL MSJ DEL BT
                    int endOfLineIndex = DataStringIN.indexOf("#");
                    if (endOfLineIndex > 0) {
                        String dataInPrint = DataStringIN.substring(0, endOfLineIndex);

                        DataStringIN.delete(0, DataStringIN.length());
                    }*/
                }
            }

        };
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean Conectar(){
        address = "98:D3:51:FD:A1:E3"; // cambiar por la de nuestro BT
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        try
        {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            //progressDialog.dismiss();
            btConeccted = false;
            return false;
        }
        // Establece la conexión con el socket Bluetooth.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try {
                //progressDialog.dismiss();
                btSocket.close();
                btConeccted = false;
                return false;
            } catch (IOException e2) {
                //progressDialog.dismiss();
                return false;
            }
        }
        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();
        MyConexionBT.write("0");
        btConeccted = true;
        return true;

    }

    public boolean VerificarEstadoBT() {

        if(btAdapter==null) {
            return false;
        } else if (btAdapter.isEnabled()){
            return true;
        }

        return false;
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException
    {
        //crea un conexion de salida segura para el dispositivo
        //usando el servicio UUID
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }


    //Crea la clase que permite crear el evento de conexion
    public class ConnectedThread extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run()
        {
            byte[] buffer = new byte[256];
            int bytes;

            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    // Envia los datos obtenidos hacia el evento via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //Envio de trama
        public void write(String input)
        {
            try {
                mmOutStream.write(input.getBytes());
            }
            catch (IOException e)
            {
                Log.i("BLUETOOHE","ERROR");

            }
        }
    }
}
