package com.aguare.main

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.*

const val REQUEST_ENABLE_BT = 1
class ConnectionBT(var bundle: MainActivity) {

    lateinit var btAdapter: BluetoothAdapter
    var addressDevice: ArrayAdapter<String>? = null
    var nameDevices: ArrayAdapter<String>? =  null

    companion object{
        var my_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        private var socketBt: BluetoothSocket? = null

        var isConnected: Boolean = false
        lateinit var addres: String
    }

    fun getConnect(){
        addressDevice = ArrayAdapter(this.bundle, android.R.layout.simple_list_item_1)
        nameDevices = ArrayAdapter(this.bundle, android.R.layout.simple_list_item_1)

        val button_on = this.bundle.findViewById<Button>(R.id.button_On)
        val button_off = this.bundle.findViewById<Button>(R.id.button_off)
        val button_connect = this.bundle.findViewById<Button>(R.id.button_connect)
        val button_send = this.bundle.findViewById<Button>(R.id.button_send)
        val button_devices = this.bundle.findViewById<Button>(R.id.button_devices)
        val button_arduino = this.bundle.findViewById<Button>(R.id.button_arduino)

        val spinner_avaiable = this.bundle.findViewById<Spinner>(R.id.spinner_avaiable)
        val text_send = this.bundle.findViewById<EditText>(R.id.text_send)

        val someActivityResultLauncher = this.bundle.registerForActivityResult(
            StartActivityForResult()
        ){ result ->
            if (result.resultCode == REQUEST_ENABLE_BT){
                Log.i("MainActivity", "Actividad Registrada")
            }
        }
        try {
            btAdapter = (this.bundle.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
            if (btAdapter == null){
                Toast.makeText(this.bundle, "No se puede acceder al BT", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this.bundle, "EL BT está disponible", Toast.LENGTH_SHORT).show()
            }
        }catch (e: java.lang.Exception){
            Toast.makeText(this.bundle, "No se puede acceder al BT", Toast.LENGTH_LONG).show()
        }




        button_on.setOnClickListener{
            if (btAdapter.isEnabled){
                Toast.makeText(this.bundle, "EL BT está Activado", Toast.LENGTH_SHORT).show()
            }else{
                val enableBTIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                if (ActivityCompat.checkSelfPermission(this.bundle, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ){
                    Log.i("MainActivity", "ActivityCompat#requestPermissions")
                }
                someActivityResultLauncher.launch(enableBTIntent)
            }
        }

        button_off.setOnClickListener{
            if (!btAdapter.isEnabled){
                Toast.makeText(this.bundle, "EL BT ya está desactivado", Toast.LENGTH_SHORT).show()
            }else{
                btAdapter.disable()
                Toast.makeText(this.bundle, "EL BT se ha desactivado", Toast.LENGTH_SHORT).show()
            }
        }

        button_devices.setOnClickListener{
            if (btAdapter.isEnabled){
                val pairDevices : Set<BluetoothDevice>? = btAdapter.bondedDevices
                addressDevice!!.clear()
                nameDevices!!.clear()

                pairDevices?.forEach{device ->
                    val deviceName = device.name
                    val deviceHardwareAddres = device.address
                    addressDevice!!.add(deviceHardwareAddres)
                    nameDevices!!.add(deviceName)
                }

                spinner_avaiable.adapter = nameDevices
            }else{
                val noDevices = "Ningún dispositivo pudo ser emparejado"
                addressDevice!!.add(noDevices)
                nameDevices!!.add(noDevices)
                Toast.makeText(this.bundle, "Primero vincule un dispositivo", Toast.LENGTH_SHORT).show()
            }
        }

        button_connect.setOnClickListener{
            try {
                if (socketBt == null || ! isConnected){
                    val intValSpin = spinner_avaiable.selectedItemPosition
                    addres = addressDevice!!.getItem(intValSpin).toString()
                    Toast.makeText(this.bundle, addres, Toast.LENGTH_SHORT).show()
                    btAdapter?.cancelDiscovery()
                    val device: BluetoothDevice = btAdapter.getRemoteDevice(addres)
                    socketBt = device.createInsecureRfcommSocketToServiceRecord(my_UUID)
                    socketBt!!.connect()
                }
                Toast.makeText(this.bundle, "Conexión Exitosa!", Toast.LENGTH_SHORT).show()
                Log.i("MainActivity", "CONEXIÓN EXITOSA")
            }catch (e: IOException){
                e.printStackTrace()
                Toast.makeText(this.bundle, "ERROR DE CONEXIÓN", Toast.LENGTH_SHORT).show()
                Log.i("MainActivity", "ERROR DE CONEXIÓN")
                Log.i("MainActivity", e.toString())
            }
        }

        button_send.setOnClickListener{
            if (text_send.text.toString().isEmpty()){
                Toast.makeText(this.bundle, "No puede enviar un mensaje vacío", Toast.LENGTH_SHORT).show()
            }else{
                var message: String = text_send.text.toString()
                sendCommand(message)
            }
        }

        button_arduino.setOnClickListener{
            this.bundle.setContentView(R.layout.activity_main)
            this.bundle.changeUpdateChart()
        }
    }

    private fun sendCommand(input: String){
        if (socketBt != null){
            try {
                socketBt!!.outputStream.write((input.toByteArray()))
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }
}