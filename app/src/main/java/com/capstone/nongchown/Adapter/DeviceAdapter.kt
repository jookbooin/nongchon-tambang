package com.capstone.nongchown.Adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.nongchown.databinding.DiscoveryDeviceListBinding

class DeviceAdapter(var deviceList: List<BluetoothDevice>) : RecyclerView.Adapter<DeviceAdapter.Holder>() {

    /** interface 객체 생성 */
    var itemClick : ItemClick? = null

    /** RecyclerView에 몇가지의 아이템이 떠야되는지 == deviceList 사이즈를 반환 */
    override fun getItemCount(): Int {
        return deviceList.size
    }
    /**
     * viewHolder가 생성되는 함수
     * 여기서 반환한 뷰 홀더 객체는 자동으로 onBindViewHolder() 함수의 매개변수로 전달
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceAdapter.Holder {
        val binding = DiscoveryDeviceListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    /**
     * 매개변수로 전달된 뷰 홀더 객체의 뷰에 데이터를 출력하거나 필요한 이벤트를 등록
     * position은 아이템중 지금 아이템이 몇번째 아이템인지 알려줌
     * */
    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: DeviceAdapter.Holder, position: Int) {
        /**
         * 클릭이벤트 - 클릭 리스너를 설정
         * 클릭 이벤트가 발생할 때 itemClick 콜백을 통해 외부로 이벤트를 전달
         * */
        holder.itemView.setOnClickListener {
            Log.d("[로그]"," position : $position name : ${deviceList[position].name} ")
            itemClick?.onClick(it, position)
        }
        holder.name.text = deviceList[position].name?: "알 수 없는 기기"
        holder.address.text = deviceList[position].address
    }

    fun getDeviceAtPosition(position: Int): BluetoothDevice {
        return deviceList[position]
    }

    inner class Holder(binding: DiscoveryDeviceListBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.name
        val address = binding.address
    }

    /** 클릭 이벤트를 위한 inf */
    interface ItemClick {
        fun onClick(view : View, position : Int)  /** 클릭시 발생시킬 이벤트 */
    }
}