package com.capstone.nongchown.View.Activity


import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.nongchown.Adapter.ConnectedDeviceAdapter
import com.capstone.nongchown.Model.Enum.BluetoothState
import com.capstone.nongchown.Model.ForegroundService
import com.capstone.nongchown.Model.UserInfo
import com.capstone.nongchown.R
import com.capstone.nongchown.Utils.moveActivity
import com.capstone.nongchown.Utils.showToast
import com.capstone.nongchown.ViewModel.BluetoothViewModel
import com.capstone.nongchown.ViewModel.UserProfileViewModel
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserProfileActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val bluetoothViewModel by viewModels<BluetoothViewModel>()
    lateinit var connectedDeviceAdapter: ConnectedDeviceAdapter
    lateinit var recyclerView: RecyclerView

    private val userprofileViewModel = UserProfileViewModel()

    private lateinit var pageScroll: ScrollView
    private lateinit var drawerLayout: DrawerLayout

    private lateinit var name: String
    private lateinit var email: String
    private lateinit var age: String
    private lateinit var gender: String
    private val emergencyContactList = mutableListOf<String>()

    private val emergencyAddButton: Button by lazy {
        findViewById(R.id.emergency_contact_addButton)
    }
    private val saveButton: Button by lazy {
        findViewById(R.id.user_profile_saveButton)
    }

    private val PERMISSIONS_REQUEST_SEND_SMS = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)

        // 문자 전송 관련 권한
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.SEND_SMS
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {//권한이 없다면
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.SEND_SMS),
//                PERMISSIONS_REQUEST_SEND_SMS
//            )
//        }

        pageScroll = findViewById(R.id.user_profile_scroll)

        val userName = findViewById<EditText>(R.id.user_name)
        userName.addTextChangedListener {
            Log.d("[로그]", "name changed")
            saveButton.isEnabled = true
        }

        val userEmail = findViewById<EditText>(R.id.user_email)
        userEmail.addTextChangedListener {
            Log.d("[로그]", "email changed")
            saveButton.isEnabled = true
        }

        val userAge = findViewById<EditText>(R.id.user_age)
        userAge.addTextChangedListener {
            Log.d("[로그]", "age changed")
            saveButton.isEnabled = true
        }

        val userGender = findViewById<Spinner>(R.id.gender)
//        userGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                if (userGender.selectedItem.toString() != gender) {
//                    saveButton.isEnabled = true
//                }
//                Log.d("[로그]", "gender changed")
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // Do nothing
//            }
//        }

        val emergencyContacts = findViewById<LinearLayout>(R.id.emergency_contact_list)

        emergencyAddButton.setOnClickListener {
            addEmergencyContact(emergencyContacts, "")
        }

        saveButton.setOnClickListener {
            try {
                Log.d("[로그]", "저장 버튼 클릭")
                emergencyContactList.clear()
                for (i in emergencyContacts.childCount - 1 downTo 0) {
                    val eContact = emergencyContacts.getChildAt(i)
                    if (eContact is EditText && eContact.text.isEmpty()) {
                        emergencyContacts.removeView(eContact)
                    }
                }
                emergencyContacts.children.forEach { emergencyContact ->
                    if (emergencyContact is EditText) {
                        emergencyContactList.add(emergencyContact.text.toString())
                    }
                }

                val userInfo = UserProfileViewModel().userProfileSave(
                    UserInfo(
                        userName.text.toString(),
                        userEmail.text.toString(),
                        userAge.text.toString(),
                        userGender.selectedItem.toString(),
                        emergencyContactList
                    )
                )
                name = userInfo.name
                email = userInfo.email
                age = userInfo.age
                gender = userInfo.gender
                emergencyContactList.clear()
                Log.d("[로그]", "emergencyContactList.clear(): $emergencyContactList")
                emergencyContactList.addAll(userInfo.emergencyContactList)

                userName.setText(userInfo.name)
                userEmail.setText(userInfo.email)
                userAge.setText(userInfo.age)
                userGender.setSelection((if (userInfo.gender == "남") 0 else 1))
                emergencyContacts.removeViews(0, emergencyContacts.childCount - 1)
                for (i: Int in 0..<userInfo.emergencyContactList.size) {
                    addEmergencyContact(emergencyContacts, userInfo.emergencyContactList[i])
                }

                saveButton.isEnabled = false
            } catch (e: IllegalArgumentException) {
                Toast.makeText(this, "입력 오류: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        //        앱 시작 시 데이터베이스로부터 사용자 데이터를 받아온다.(있다고 가정)
        initUserInfo(
            userName,
            userEmail,
            userAge,
            userGender,
            emergencyContacts
        )

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_profile)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /** sideBar */
        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        clickAndOpenSideBar()
        sideBarInnerAction()

    }

    private fun initUserInfo(
        userName: EditText,
        userEmail: EditText,
        userAge: EditText,
        userGender: Spinner,
        emergencyContacts: LinearLayout
    ) {

        Log.d("[로그]", "initializing")

        email = "sanghoo1023@gmail.com"

        lifecycleScope.launch {
            val userInfo = userprofileViewModel.loadStoredData(email)

            userName.setText(userInfo.name)
            userEmail.setText(userInfo.email)
            userAge.setText(userInfo.age)
            userGender.setSelection((if (userInfo.gender == "남") 0 else 1))
            for (i: Int in 0..<userInfo.emergencyContactList.size) {
                addEmergencyContact(emergencyContacts, userInfo.emergencyContactList[i])
            }
        }
        Log.d("[로그]", "initializing complete")
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addEmergencyContact(emergencyContacts: LinearLayout, emergencyContact: String) {
        val inflater = LayoutInflater.from(this)
        val eContact =
            inflater.inflate(R.layout.emergency_contact_item, emergencyContacts, false) as EditText

        eContact.addTextChangedListener {
            Log.d("[로그]", "emergencyContact changed")
            saveButton.isEnabled = true

            eContact.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val clearDrawable = eContact.compoundDrawablesRelative[2]
                    if (clearDrawable != null && event.rawX >= (eContact.right - clearDrawable.bounds.width())) {
                        eContact.setText("")
                        return@setOnTouchListener true
                    }
                }
                false
            }
        }
        eContact.setText(emergencyContact)
        emergencyContacts.addView(eContact, emergencyContacts.childCount - 1)
    }


    /** sideBar */
    override fun onNavigationItemSelected(item: MenuItem): Boolean { // X
        when (item.itemId) {
        }
        return false
    }

    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                Log.d("[로그]", "블루투스 활성화")
            } else if (result.resultCode == RESULT_CANCELED) {
                Log.d("[로그]", "사용자 블루투스 활성화 거부")
            }
        }

    private fun clickAndOpenSideBar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_nav,
            R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        toolbar.setNavigationOnClickListener {
            checkBluetoothEnabledState { // 밑의 동작 람다식으로 넣음
                drawerLayout.openDrawer(GravityCompat.START)
                bluetoothViewModel.getPairedDevices()
            }
        }
    }

    private fun sideBarInnerAction() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val navHeader = navigationView.getHeaderView(0)

        /** 내부 동작 */
        addNewDevices(navHeader)
        pairedDevices(navHeader)
        connectDevice()
        showConnectSuccessMessage()
    }

    private fun connectDevice() {
        connectedDeviceAdapter.itemClick = object : ConnectedDeviceAdapter.ItemClick {

            override fun onClick(view: View, position: Int) {
                checkBluetoothEnabledState {
                    // 1. 우선 실행중인 service 제거
                    val serviceIntent =
                        Intent(this@UserProfileActivity, ForegroundService::class.java)
                    stopService(serviceIntent)

                    // 2. 연결
                    val device = connectedDeviceAdapter.getDeviceAtPosition(position)
                    bluetoothViewModel.connectToDevice(device)
                }
            }
        }
    }

    private fun addNewDevices(navHeader: View) {
        val btnDeviceDiscovery = navHeader.findViewById<Button>(R.id.btndevicediscovery)
        btnDeviceDiscovery.setOnClickListener {
            checkBluetoothEnabledState {
                drawerLayout.closeDrawer(GravityCompat.START)
                moveActivity(DeviceDiscoveryActivity::class.java)
            }
        }
    }

    fun pairedDevices(navHeader: View) {
        recyclerView = navHeader.findViewById(R.id.paireddevice)
        connectedDeviceAdapter = ConnectedDeviceAdapter(emptyList())

        recyclerView.apply {
            adapter = connectedDeviceAdapter
            layoutManager = LinearLayoutManager(this@UserProfileActivity)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                bluetoothViewModel.pairedDevices.collect { devices ->
                    connectedDeviceAdapter.updateDevices(devices)
                }
            }
        }
    }

    /** 블루투스 활성화 상태 템플릿 */
    fun checkBluetoothEnabledState(enabledAction: () -> Unit) {
        when (bluetoothViewModel.checkBluetoothState()) {
            BluetoothState.ENABLED -> {
                enabledAction()
            }

            BluetoothState.DISABLED -> {
                Log.d("[로그]", "블루투스 활성화 되어있지 않습니다.")
                val bluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startForResult.launch(bluetoothIntent)
            }

            else -> showToast("블루투스를 지원하지 않는 장비입니다.")
        }
    }

    private fun showConnectSuccessMessage() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                bluetoothViewModel.connectionStatus.collect { isConnected ->
                    if (isConnected) {
                        showToast("연결되었습니다.")
                        delay(1000)
                        startBluetoothService()
                    }
                }
            }
        }
    }

    private fun startBluetoothService() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

}



