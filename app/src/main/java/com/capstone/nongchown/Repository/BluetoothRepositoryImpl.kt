package com.capstone.nongchown.Repository

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.capstone.nongchown.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class BluetoothRepositoryImpl @Inject constructor(
    private val context: Context, private val bluetoothAdapter: BluetoothAdapter
) : BluetoothRepository {

    //    private var deviceScanReceiver: BroadcastReceiver? = null // null 초기화 : 필요한 시점까지 객체의 생성을 늦춘다.
    private val _discoveredDeviceList = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    private val _pairedDeviceList = MutableStateFlow<List<BluetoothDevice>>(emptyList())

    private var bluetoothSocket: BluetoothSocket? = null
    private var connectedJob: Job? = null


    @SuppressLint("MissingPermission")
    override fun startDiscovery(): MutableStateFlow<List<BluetoothDevice>> {
        if (_discoveredDeviceList.value.isNotEmpty()) {
            Log.d("[로그]", "INIT LIST")
            _discoveredDeviceList.value = emptyList()
        }

        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)                 // 블루투스 상태변화 액션
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)             // 기기 검색 시작
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)            // 기기 검색 종료
        filter.addAction(BluetoothDevice.ACTION_FOUND)                          // 기기 검색됨
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)                  // a low level (ACL) connection 연결 확인
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)             // 원격 블루투스 장치의 페어링 상태가 변경 (페어링?)
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST)                // 페어링 요청
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
        context.registerReceiver(deviceScanReceiver, filter)
        bluetoothAdapter?.startDiscovery()
        return _discoveredDeviceList
    }

    @SuppressLint("MissingPermission")
    override fun getPairedDevices(): StateFlow<List<BluetoothDevice>> {
        Log.d("[로그]", "GET PAIRED DEVICES")

        if (bluetoothAdapter != null) {
            // Ensure Bluetooth is enabled
            if (!bluetoothAdapter.isEnabled) {
                // You might want to prompt the user to enable Bluetooth
            }
            // Get the list of paired devices
            val pairedDevices: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices

            pairedDevices.forEach { device ->
                Log.d("[로그]", "페어링 되어있는 기기 ( Name: ${device.name}, Address: ${device.address} )")
            }

            _pairedDeviceList.value = pairedDevices.toList()
        } else {
            // Bluetooth is not supported on this device
        }
        return _pairedDeviceList
    }

    /***
     * bluetoothDevice.bondState : 10(BOND_NONE_페어링 이전), 11(BOND_BONDING_페어링 중), 12(BOND_BONDED_페어링 완료)
     */
    @SuppressLint("MissingPermission")
    suspend override fun connectToDevice(bluetoothDevice: BluetoothDevice) {
        Log.d("[로그]", "[ ${Thread.currentThread().name} ]")
        Log.d("[로그]", "CONNECT TO DEVICE ( ${bluetoothDevice.name} : ${bluetoothDevice.address}")

        /**  BluetoothDevice 객체가 아직 페어링되지 않았을 때 */
        if (bluetoothDevice.bondState != BluetoothDevice.BOND_BONDED) {
            Log.d("[로그]", "페어링 이전 상태 : ${bluetoothDevice.bondState}")
            bluetoothDevice.createBond()
            /** 페어링 과정 완료 후 ACTION_BOND_STATE_CHANGED 반환 */
        }

        /**
         * 실행환경 변화 : Dispatcher.IO 환경에서 코루틴을 실행  ( 네트워크 작업 )
         * 순차적 실행 표현 가능
         * */
        withContext(Dispatchers.IO) {
            Log.d("[로그]", "[ ${Thread.currentThread().name} ] - [ $coroutineContext ]")
            if (bluetoothSocket != null) {
                disconnect()
            }
            bluetoothSocket = createBluetoothSocket(bluetoothDevice)
            Log.d("[로그]", "연결 시작 전: ${bluetoothDevice.name} : ${bluetoothDevice.address} 페어링 상태 : ${bluetoothDevice.bondState}")
            bluetoothSocket?.connect()
            Log.d("[로그]", "연결 성공: ${bluetoothDevice.name} : ${bluetoothDevice.address} 페어링 상태 : ${bluetoothDevice.bondState}")
        }

        Log.d("[로그]", "[ ${Thread.currentThread().name} ] - [ $coroutineContext ]")
        connectedJob?.cancel()
    }

    @SuppressLint("MissingPermission")
    private fun createBluetoothSocket(device: BluetoothDevice): BluetoothSocket {
        val uuid = UUID.fromString(Constants.SPP_UUID) // SPP UUID

        Log.d("[로그]", "CREATE BLUETOOTH SOCKET")
        return device.createRfcommSocketToServiceRecord(uuid)
    }

    override fun stopDiscovery() {
        Log.d("[로그]", "DISCOVERY STOP")

        context.unregisterReceiver(deviceScanReceiver)
        Log.d("[로그]", "UNREGISTER RECEIVER")
    }

    @SuppressLint("MissingPermission")
    override fun cancelDiscovery() {
        Log.d("[로그]", "DISCOVERY CANCEL")
        bluetoothAdapter?.cancelDiscovery()
    }

    // 데이터 연결 확인 (send)
    override suspend fun sendDataToDevice() {
        withContext(Dispatchers.IO) {
            bluetoothSocket?.let {
                try {
                    val outputStream: OutputStream = it.outputStream
                    val dataString = "E".toByteArray()
                    outputStream.write(dataString)
                    outputStream.flush()                                    // 즉시 전송, 출력
                    Log.d("[로그]", "데이터 전송 성공")
                } catch (e: IOException) {
                    Log.e("[로그]", "전송 실패", e)
                }
            } ?: run {
                Log.d("[로그]", "BluetoothSocket이 연결되어 있지 않습니다.")
            }
        }
    }

    // 단방향으로 data 전달시 Flow로 전달 생산자 (emit)
    override fun readDataFromDevice(): Flow<String> = flow {
        Log.d("[로그]", "DATA 수신 대기")
        bluetoothSocket?.let { socket ->
            val inputStream: InputStream = socket.inputStream
            var buffer = ByteArray(1024)
            var bytes: Int
            while (true) {
                try {
                    bytes = inputStream.read(buffer)                        // 주어진 buffer 크기(1024) 만큼 데이터를 읽고 총 몇 byte를 읽었는지 반환
                    val message = buffer.decodeToString(endIndex = bytes)
                    Log.d("[로그]", "수신된 메시지: $message")

                    emit(message)
                    //sendDataToDevice()
                    // message 방출 -> collect에서 수집
                    /**
                     * 데이터가 1번만 들어오도록 앱에서 처리해야 하는 경우
                     * 1. 30초 delay
                     * 2. 30초 동안 buffer에 들어온 값 초기화
                     * */
                    delay(30000)                                    // 30초 마다 1번씩만 들어오도록 해야한다?
                    while (inputStream.available() > 0) {
                        Log.d("[로그]", "30초 동안 수신 받은 buffer 초기화")
                        inputStream.read(buffer)
                    }
                    Log.d("[로그]", "30초 이후 buffer 초기화 ")

                } catch (e: IOException) {
                    Log.e("[로그]", "데이터 읽기 중 오류 발생 OR Socket 중단", e)
                    break
                }
            }

        } ?: run {
            Log.d("[로그]", "BluetoothSocket이 연결되어 있지 않습니다.")
        }
    }


    override fun isBluetoothEnabled(): Boolean {
        return if (bluetoothAdapter?.isEnabled == false) {                  // 기기의 블루투스 비활성화 상태
            false
        } else {
            true
        }
    }

    override fun isBluetoothSupport(): Boolean {
        return if (bluetoothAdapter == null) {
            Log.d("[로그]", "기기가 블루투스 지원하지 않습니다.")
            false
        } else {
            Log.d("[로그]", "기기가 블루투스를 지원합니다.")
            true
        }
    }

    override fun disconnect() {
        try {
            bluetoothSocket?.close() // 소켓 닫기 시도
        } catch (e: IOException) {
        } finally {
            bluetoothSocket = null // 소켓 참조 제거
            Log.d("[로그]","소켓 닫기")
        }
    }

    @Suppress("DEPRECATION", "MissingPermission")
    private val deviceScanReceiver = object : BroadcastReceiver() {
        val tempDeviceList = mutableListOf<BluetoothDevice>()
        var pairedDeviceAddresses: Set<String>? = null

        override fun onReceive(context: Context?, intent: Intent?) {

            var action = ""
            if (intent != null) {
                action = intent.action.toString() //입력된 action
            }

            when (action) {
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    Log.d("[로그]", "ACTION STATE CHANGED")
                }

                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    Log.d("[로그]", "DISCOVERY STARTED")
                    tempDeviceList.clear()
                    pairedDeviceAddresses = bluetoothAdapter.bondedDevices.map { it.address }.toSet()  // device -> mac 주소로만 변환
                }

                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Log.d("[로그]", "DISCOVERY FINISHED")
                    _discoveredDeviceList.value = tempDeviceList
                    pairedDeviceAddresses = null
                }

                BluetoothDevice.ACTION_FOUND -> {
                    val device = intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {discoveredDevice ->

                        // 로그 찍기 용
                        if (discoveredDevice.name != null) {
                            Log.d("[로그]", "FOUND ( Name: ${discoveredDevice.name}, Address: ${discoveredDevice.address} )")
                        }

                        // 기기 (mac 주소)가 이미 탐색되었는지 확인
                        val isPaired = pairedDeviceAddresses?.contains(discoveredDevice.address) ?: false

                        // 중복 방지
                        if (!isPaired && discoveredDevice.name != null && !tempDeviceList.contains(discoveredDevice)) {
                            tempDeviceList.add(discoveredDevice)
                        }
                    }
                }

                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    Log.d("[로그]", "ACTION ACL CONNECTED")
                }

                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    val device = intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        val bondState = it.bondState
                        Log.d("[로그]", "페어링 상태 변화 ( Name: ${device.name}, Address: ${device.address}, 페어링 상태 : : ${device.bondState} )")
                        // bondState를 이용해 현재 페어링 상태를 로그로 찍거나 다른 처리를 할 수 있습니다.
                    }
                }

                BluetoothDevice.ACTION_PAIRING_REQUEST -> {
                    Log.d("[로그]", "ACTION PAIRING REQUEST")
                }

                BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED -> {
                    Log.d("[로그]", "ACTION CONNECTION STATE CHANGED")
                }

            }

        }
    }


}