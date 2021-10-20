package com.smileparser.medijunctions;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainFilter {
	public void filter(String path, int option) {
		try {
			String full_path = path+".wav";
			File input_file = new File(full_path);
			FileInputStream fis = new FileInputStream(input_file);
			FileOutputStream fos = new FileOutputStream(new File(path+"_filter.wav"));

			Log.d( "BluetoothPlugin", "filter1");
			byte[] file_data = new byte[(int)input_file.length()];
			fis.read(file_data,0,file_data.length);

			ByteArrayOutputStream baos=null;
			Log.d( "BluetoothPlugin", "filter4: "+file_data.length);
			if(option==1){
				Log.d( "BluetoothPlugin", "filter34");
				baos = filter_bytes(file_data, 700);
				Log.d( "BluetoothPlugin", "filter5");
			}else if(option==2){
				baos = filter_bytes(file_data, 1000);
			}
			Log.d( "BluetoothPlugin", "filter2");
			fos.write(baos.toByteArray());
			baos.close();
			fos.flush();
			fos.close();
			Log.d( "BluetoothPlugin", "filter3");
			//fis.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void filterLiveStreaming(String output, ByteArrayOutputStream byte_buf, int option) {
		try {
			FileOutputStream fos = new FileOutputStream(new File(output+"_filter.wav"));


			byte[] file_data = byte_buf.toByteArray();

			ByteArrayOutputStream baos=null;
			if(option==1){
				baos = filter_bytes(file_data, 600);
			}else if(option==2){
				baos = filter_bytes(file_data, 1000);
			}
			fos.write(baos.toByteArray());
			baos.close();
			fos.flush();
			fos.close();
			//fis.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ByteArrayOutputStream filter_bytes(byte[] file_bytes, int cut_off_freq) {
		Log.d( "BluetoothPlugin", "filter37");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Log.d( "BluetoothPlugin", "filter35");
		try {
			Log.d( "BluetoothPlugin", "filter36");
			Butterworth butter = new Butterworth(7, PassbandType.BANDPASS, (double) 1/1000,((double)cut_off_freq/1000), 0.025);
			Log.d( "BluetoothPlugin", "filter41");
			double[] raw_data = new double[(int) (file_bytes.length / 2)];
			Log.d( "BluetoothPlugin", "filter42");
			baos.write(file_bytes, 0, 44);
			Log.d( "BluetoothPlugin", "filter30");
			int j = 0;
			for (int k = 44; k < file_bytes.length - 1; k = k + 2) {
				raw_data[j++] = (double) (file_bytes[k + 1] << 8 | file_bytes[k] & 0xFF) / 32767;
			}
			Log.d( "BluetoothPlugin", "filter31");
			double[] filtered_ouput = butter.filter_output(raw_data);
			for (int k = 0; k < filtered_ouput.length; k++) {

				short value = (short) (filtered_ouput[k] * 32767);
				baos.write((byte) (value & 0xFF));
				baos.write((value >> 8 & 0xFF));
			}
			Log.d( "BluetoothPlugin", "filter32");
			baos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("FIltered");
		return baos;

	}
}
