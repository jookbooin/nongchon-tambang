package com.capstone.nongchown.View.Activity

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
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
import androidx.core.widget.doOnTextChanged
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.nongchown.Adapter.DeviceAdapter
import com.capstone.nongchown.Model.Enum.BluetoothState
import com.capstone.nongchown.R
import com.capstone.nongchown.Utils.moveActivity
import com.capstone.nongchown.Utils.showToast
import com.capstone.nongchown.ViewModel.BluetoothViewModel
import com.capstone.nongchown.ViewModel.UserProfileViewModel
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserProfileActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val bluetoothViewModel by viewModels<BluetoothViewModel>()
    lateinit var deviceAdapter: DeviceAdapter
    lateinit var recyclerView: RecyclerView

    private lateinit var pageScroll: ScrollView
    private lateinit var drawerLayout: DrawerLayout

    private lateinit var name: String
    private lateinit var email: String
    private lateinit var age: String
    private lateinit var gender: String

    private val emergencyContactList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)

        pageScroll = findViewById(R.id.user_profile_scroll)
        val emergencyAddButton = findViewById<Button>(R.id.emergency_contact_addButton)
        val saveButton = findViewById<Button>(R.id.user_profile_saveButton)

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
        userGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (userGender.selectedItem.toString() != gender) {
                    saveButton.isEnabled = true
                }
                Log.d("[로그]", "gender changed")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        val emergencyContacts = findViewById<LinearLayout>(R.id.emergency_contact_list)
        emergencyContacts.children.filterIsInstance<EditText>().forEach { emergencyContact ->
            emergencyContact.addTextChangedListener {
                Log.d("[로그]", "eContact added")
                saveButton.isEnabled = true
            }
        }

        emergencyAddButton.setOnClickListener {
            addEmergencyContact(emergencyContacts)
        }

        saveButton.setOnClickListener {
            try {
                emergencyContacts.children.filterIsInstance<EditText>()
                    .forEach { emergencyContact ->
                        emergencyContactList.add(emergencyContact.text.toString())
                    }
                Log.d("[로그]", "저장 버튼 클릭")
                val userinfo = UserProfileViewModel().userProfileSave(
                    userName.text.toString(),
                    userEmail.text.toString(),
                    userAge.text.toString(),
                    userGender.selectedItem.toString(),
                    emergencyContactList
                )
                this.name = userinfo.name
                this.email = userinfo.email
                this.age = userinfo.age
                this.gender = userinfo.gender
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

        name = "김농촌"
        email = "sanghoo1023@gmail.com"
        age = "23"
        gender = "여"
        emergencyContactList.add("010-5341-3270")

        userName.setText(name)
        userEmail.setText(email)
        userAge.setText(age)
        userGender.setSelection((if (gender == "남") 0 else 1))


        for (i: Int in 0..<emergencyContactList.size) {
            addEmergencyContact(emergencyContacts)
            Log.d("[로그]", "addEmergencyContact(emergencyContacts)")
            val eContact = emergencyContacts.getChildAt(i)
            if (eContact is EditText) {
                eContact.setText(emergencyContactList[i])
            } else {
                Log.d("[에러]", "비상 연락망 위젯 개수 오류")
            }
        }
        Log.d("[로그]", "initializing complete")
    }

    private fun addEmergencyContact(emergencyContacts: LinearLayout) {
        val inflater = LayoutInflater.from(this)
        val eContact = inflater.inflate(R.layout.emergency_contact_item, emergencyContacts, false)
        setupFocusListener(eContact as EditText)
        emergencyContacts.addView(eContact, emergencyContacts.childCount - 1)
    }

    //    form 입력 시 해당 form 으로 스크롤 이동
    private fun setupFocusListener(editText: EditText) {
        editText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                pageScroll.post {
                    pageScroll.scrollTo(0, v.top)
                }
            }
        }

        // Optional: Adjusting scroll when text changes
        editText.doOnTextChanged { _, _, _, _ ->
            if (editText.hasFocus()) {
                pageScroll.post {
                    pageScroll.scrollTo(0, editText.top)
                }
            }
        }
    }

    /** sideBar */
    override fun onNavigationItemSelected(item: MenuItem): Boolean { // X
        when (item.itemId) {
        }
        return false
    }

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
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

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
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
        recyclerView = navHeader.findViewById(R.id.paireddevice);
        deviceAdapter = DeviceAdapter(emptyList())

        recyclerView.apply {
            adapter = deviceAdapter
            layoutManager = LinearLayoutManager(this@UserProfileActivity)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                bluetoothViewModel.pairedDevices.collect { devices ->
                    deviceAdapter.updateDevices(devices)
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

}



