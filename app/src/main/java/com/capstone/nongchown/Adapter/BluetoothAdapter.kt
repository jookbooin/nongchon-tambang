package com.capstone.nongchown.Adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.nongchown.databinding.DiscoveryDeviceListBinding

class BluetoothAdapter(var deviceList: List<BluetoothDevice>) : RecyclerView.Adapter<BluetoothAdapter.Holder>() {

    // RecyclerView에 몇가지의 아이템이 떠야되는지 == deviceList 사이즈를 반환
    override fun getItemCount(): Int {
        return deviceList.size
    }

    /**
     * viewHolder가 생성되는 함수
     * 여기서 반환한 뷰 홀더 객체는 자동으로 onBindViewHolder() 함수의 매개변수로 전달
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothAdapter.Holder {
        val binding = DiscoveryDeviceListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    /**
     * 매개변수로 전달된 뷰 홀더 객체의 뷰에 데이터를 출력하거나 필요한 이벤트를 등록
     * position은 아이템중 지금 아이템이 몇번째 아이템인지 알려줌
     * */
    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: BluetoothAdapter.Holder, position: Int) {
        holder.name.text = deviceList[position].name;
        holder.address.text = deviceList[position].address;
    }


    inner class Holder(val binding: DiscoveryDeviceListBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.name
        val address = binding.address
    }
}