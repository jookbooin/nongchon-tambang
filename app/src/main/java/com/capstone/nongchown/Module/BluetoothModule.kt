package com.capstone.nongchown.Module

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.capstone.nongchown.Repository.BluetoothRepository
import com.capstone.nongchown.Repository.BluetoothRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BluetoothModule {

    @Singleton
    @Provides
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideBluetoothManager(@ApplicationContext context: Context): BluetoothManager {
        return context.getSystemService(BluetoothManager::class.java)
    }

    @Provides
    @Singleton
    fun provideBluetoothAdapter(bluetoothManager: BluetoothManager): BluetoothAdapter {
        return bluetoothManager.adapter ?: throw IllegalStateException("Bluetooth not supported on this device.")
    }

    @Provides
    @Singleton
    fun provideBluetoothRepository(
        context: Context,
        bluetoothAdapter: BluetoothAdapter
    ): BluetoothRepository {
        return BluetoothRepositoryImpl(context, bluetoothAdapter)
    }

}