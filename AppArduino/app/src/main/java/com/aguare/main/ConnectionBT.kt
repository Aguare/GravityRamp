package com.aguare.main

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import java.io.BufferedReader
import java.io.DataInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

const val REQUEST_ENABLE_BT = 1

class ConnectionBT(var bundle: MainActivity) {

    lateinit var btAdapter: BluetoothAdapter
    var addressDevice: ArrayAdapter<String>? = null
    var nameDevices: ArrayAdapter<String>? = null

    companion object {
        var my_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var socketBt: BluetoothSocket? = null

        var isConnected: Boolean = false
        lateinit var addres: String
        lateinit var text_entrys: EditText
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getConnect() {
        addressDevice = ArrayAdapter(this.bundle, android.R.layout.simple_list_item_1)
        nameDevices = ArrayAdapter(this.bundle, android.R.layout.simple_list_item_1)

        val button_on = this.bundle.findViewById<Button>(R.id.button_on)
        val button_off = this.bundle.findViewById<Button>(R.id.button_off)
        val button_connect = this.bundle.findViewById<Button>(R.id.button_connect)
        val button_send = this.bundle.findViewById<Button>(R.id.button_send)
        val button_devices = this.bundle.findViewById<Button>(R.id.button_devices)

        val spinner_avaiable = this.bundle.findViewById<Spinner>(R.id.spinner_avaiable)
        val text_send = this.bundle.findViewById<EditText>(R.id.text_send)
        text_entrys = this.bundle.findViewById<EditText>(R.id.text_entrys)

        val someActivityResultLauncher = this.bundle.registerForActivityResult(
            StartActivityForResult()
        ) { result ->
            if (result.resultCode == REQUEST_ENABLE_BT) {
                Log.i("MainActivity", "Actividad Registrada")
            }
        }
        try {
            btAdapter =
                (this.bundle.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
            if (btAdapter == null) {
                Toast.makeText(this.bundle, "No se puede acceder al BT", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this.bundle, "EL BT está disponible", Toast.LENGTH_SHORT).show()
            }
        } catch (e: java.lang.Exception) {
            Toast.makeText(this.bundle, "No se puede acceder al BT", Toast.LENGTH_LONG).show()
        }

        button_on.setOnClickListener {
            if (btAdapter.isEnabled) {
                Toast.makeText(this.bundle, "EL BT está Activado", Toast.LENGTH_SHORT).show()
            } else {
                val enableBTIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                if (ActivityCompat.checkSelfPermission(
                        this.bundle,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.i("MainActivity", "ActivityCompat#requestPermissions")
                }
                someActivityResultLauncher.launch(enableBTIntent)
            }
        }

        button_off.setOnClickListener {
            if (!btAdapter.isEnabled) {
                Toast.makeText(this.bundle, "EL BT ya está desactivado", Toast.LENGTH_SHORT).show()
            } else {
                btAdapter.disable()
                Toast.makeText(this.bundle, "EL BT se ha desactivado", Toast.LENGTH_SHORT).show()
            }
        }

        button_devices.setOnClickListener {
            if (btAdapter.isEnabled) {
                val pairDevices: Set<BluetoothDevice>? = btAdapter.bondedDevices
                addressDevice!!.clear()
                nameDevices!!.clear()

                pairDevices?.forEach { device ->
                    val deviceName = device.name
                    val deviceHardwareAddres = device.address
                    addressDevice!!.add(deviceHardwareAddres)
                    nameDevices!!.add(deviceName)
                }
                spinner_avaiable.adapter = nameDevices
            } else {
                val noDevices = "Ningún dispositivo pudo ser emparejado"
                addressDevice!!.add(noDevices)
                nameDevices!!.add(noDevices)
                Toast.makeText(this.bundle, "Primero vincule un dispositivo", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        button_connect.setOnClickListener {
            try {
                if (socketBt == null || !isConnected) {
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
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this.bundle, "ERROR DE CONEXIÓN", Toast.LENGTH_SHORT).show()
                Log.i("MainActivity", "ERROR DE CONEXIÓN")
                Log.i("MainActivity", e.toString())
            }
        }

        button_send.setOnClickListener {
            if (text_send.text.toString().isEmpty()) {
                Toast.makeText(this.bundle, "No puede enviar un mensaje vacío", Toast.LENGTH_SHORT)
                    .show()
            } else {
                var message: String = text_send.text.toString()
                text_send.text.clear()
                sendCommand(message)
                text_send.text.clear()
            }
        }

        val button_0 = this.bundle.findViewById<Button>(R.id.button_0)
        val button_15 = this.bundle.findViewById<Button>(R.id.button_15)
        val button_30 = this.bundle.findViewById<Button>(R.id.button_30)
        val button_45 = this.bundle.findViewById<Button>(R.id.button_45)
        val button_start = this.bundle.findViewById<Button>(R.id.button_start)
        val button_getTime = this.bundle.findViewById<Button>(R.id.button_getTime)
        val button_clear = this.bundle.findViewById<Button>(R.id.button_clear)
        val button_chart = this.bundle.findViewById<Button>(R.id.button_chart)
        val button_export = this.bundle.findViewById<Button>(R.id.button_export)

        button_0.setOnClickListener {
            sendCommand("0")
        }

        button_15.setOnClickListener {
            sendCommand("2")
        }

        button_30.setOnClickListener {
            sendCommand("4")
        }

        button_45.setOnClickListener {
            sendCommand("7")
        }

        button_start.setOnClickListener {
            sendCommand("*")
            if (socketBt != null) {
                button_getTime.isEnabled = true
            }
        }

        button_getTime.setOnClickListener {
            if (socketBt != null) {
                receiveDataFromBluetooth("!")
                button_getTime.isEnabled = false
            }
        }

        button_clear.setOnClickListener {
            text_entrys.text.clear()
        }

        button_export.setOnClickListener {
            val chart: BarChart = this.bundle.findViewById(R.id.chart)
            val time: LocalDate = LocalDate.now()
            val nameFile = time.toString()
            if (chart.data != null) {
                chart.saveToGallery(nameFile)
                chart.saveToGallery(nameFile, "Downloads", "Grafica app", Bitmap.CompressFormat.PNG, 100)
                Toast.makeText(this.bundle, "Guardado en la galería", Toast.LENGTH_LONG).show()
            }
        }

        button_chart.setOnClickListener {
            if (!text_entrys.text.isEmpty()) {
                val data = text_entrys.text.split("\n").map { it.toFloat() }
                generateGraphic(data)
            } else {
                Toast.makeText(this.bundle, "No se puede graficar sin datos", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }


    fun receiveDataFromBluetooth(input: String) {
        if (input == "*"){
            Thread.sleep(5000)
        }else{
            Thread.sleep(600)
        }
        try {
            val btInput: InputStream? = socketBt?.inputStream
            val buffer = ByteArray(1024)
            var bytes: Int
            while (true) {
                bytes = btInput!!.read(buffer)
                val message = String(buffer, 0, bytes)

                if (input == "*"){
                    text_entrys.append(message + "\n")
                }else{
                    Toast.makeText(this.bundle, "Angulo ajustado a $message grados", Toast.LENGTH_LONG)
                        .show()
                }
                break
            }
        } catch (e: java.lang.Exception) {
            Log.i("Error", "No se pudo guardar la información")
            Toast.makeText(this.bundle, "Error al recibir el tiempo", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun sendCommand(input: String) {
        if (socketBt != null) {
            try {
                socketBt!!.outputStream.write((input.toByteArray()))
                receiveDataFromBluetooth(input)
            } catch (e: IOException) {
                Toast.makeText(this.bundle, "No se pudo enviar el mensaje", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(this.bundle, "No existe el socket", Toast.LENGTH_SHORT).show()
        }
    }

    fun generateGraphic(nums: List<Float>) {
        val chart: BarChart = this.bundle.findViewById(R.id.chart)
        val left = chart.axisLeft
        val right = chart.axisRight
        right.setDrawLabels(false)

        left.setDrawAxisLine(true)
        left.setDrawGridLines(false)

        val entries = nums.mapIndexed { index, number ->
            BarEntry(index.toFloat(), number)
        }

        val dataSet = BarDataSet(entries, "Tiempo")
        val data = BarData(dataSet)

        chart.description.text = "VELOCIDAD VS TIEMPO"
        chart.data = data
        //chart.description.isEnabled = false
        chart.setDrawValueAboveBar(false)
        chart.setDrawGridBackground(false)
        chart.animateY(2000)
        chart.invalidate()
    }

}