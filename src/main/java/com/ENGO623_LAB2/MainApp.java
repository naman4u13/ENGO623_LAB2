package com.ENGO623_LAB2;

import java.io.File;

import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.ENGO623_LAB2.Util.Parser;





public class MainApp {

	// Local gravity at Calgary city
	private final static double g = 9.80841;
	// Calgary city latitude
	private final static double lat = Math.toRadians(51.0447);
	// WGS-84 value of the Earth's rotation rate
	private final static double OMEGA_E_DOT = Math.toDegrees(7.2921151467E-5);

	public static void main(String args[]) {
		try {

			System.out.println("STARTED");
			File output = new File("C:\\Users\\naman.agarwal\\Downloads\\my work\\ENGO 623\\Lab2\\result4.txt");
			PrintStream stream;
			stream = new PrintStream(output);
			System.setOut(stream);
			// Parsing IMU's binary and text files
			ArrayList<double[]> ltn_up = Parser.getLTNData("x_LN100_down.ins");
			ArrayList<double[]> ltn_down = Parser.getLTNData("x_LN100_up.ins");
			ArrayList<double[]> mems_up = Parser.getMEMSData("x_adi_down.txt");
			ArrayList<double[]> mems_down = Parser.getMEMSData("x_adi_up.txt");
			System.out.println("LTN and MEMS file up and down record size");
			System.out
					.println(ltn_down.size() + "  " + ltn_up.size() + "  " + mems_down.size() + "  " + mems_up.size());
			double[][] ltn_down_mean_var = new double[6][2];
			double[][] ltn_up_mean_var = new double[6][2];
			double[][] mems_down_mean_var = new double[6][2];
			double[][] mems_up_mean_var = new double[6][2];
			// Computing mean and variance error of the mean
			for (int i = 1; i < 7; i++) {
				ltn_down_mean_var[i - 1] = getMeanVar(ltn_down, i);
				ltn_up_mean_var[i - 1] = getMeanVar(ltn_up, i);
				mems_down_mean_var[i - 1] = getMeanVar(mems_down, i);
				mems_up_mean_var[i - 1] = getMeanVar(mems_up, i);
			}
			// Computing Bias and Scale factors along with their standard error of the mean 
			double[] ltn_x_acc_bias_scale = getAccBiasScale(ltn_down_mean_var[3], ltn_up_mean_var[3]);
			double[] mems_x_acc_bias_scale = getAccBiasScale(mems_down_mean_var[3], mems_up_mean_var[3]);
			double[] ltn_x_gyro_bias_scale = getGyroBiasScale(ltn_down_mean_var[0], ltn_up_mean_var[0]);
			double[] mems_x_gyro_bias_scale = getGyroBiasScale(mems_down_mean_var[0], mems_up_mean_var[0]);
			// Square rooting Variance to Std. dev
			for (int i = 0; i < 6; i++) {
				ltn_down_mean_var[i][1] = Math.sqrt(ltn_down_mean_var[i][1]);
				ltn_up_mean_var[i][1] = Math.sqrt(ltn_up_mean_var[i][1]);
				mems_down_mean_var[i][1] = Math.sqrt(mems_down_mean_var[i][1]);
				mems_up_mean_var[i][1] = Math.sqrt(mems_up_mean_var[i][1]);
			}
			for (int i = 0; i < 6; i++) {
				System.out.println("LTN down " + i);
				print(ltn_down_mean_var[i]);
				System.out.println("LTN up " + i);
				print(ltn_up_mean_var[i]);
				System.out.println("MEMS down " + i);
				print(mems_down_mean_var[i]);
				System.out.println("MEMS up " + i);
				print(mems_up_mean_var[i]);
			}

			System.out.println("LTN Accelerometer X-axis Bias and Scale Params(Mean and Var):");
			print(ltn_x_acc_bias_scale);
			System.out.println("LTN Gyroscope X-axis Bias and Scale Params(Mean and Var):");
			print(ltn_x_gyro_bias_scale);
			System.out.println("MEMS Accelerometer X-axis Bias and Scale Params(Mean and Var):");
			print(mems_x_acc_bias_scale);
			System.out.println("MEMS Gyroscope X-axis Bias and Scale Params(Mean and Var):");
			print(mems_x_gyro_bias_scale);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private static double[] getAccBiasScale(double[] down, double[] up) {
		
		double biasMean = (down[0] + up[0]) / 2;
		double scaleMean = (down[0] - up[0] - (2 * g)) / (2 * g);
		double biasVar = (down[1] + up[1]) / 4;
		double scaleVar = (down[1] + up[1]) / (4 * g * g);
		return new double[] {biasMean, Math.sqrt(biasVar), scaleMean, Math.sqrt(scaleVar) };
	}

	private static double[] getGyroBiasScale(double[] down, double[] up) {
		
		double biasMean = (down[0] + up[0]) / 2;
		double val = 2 * OMEGA_E_DOT * Math.sin(lat);
		double scaleMean = (up[0] - down[0] - val) / val;
		double biasVar = (down[1] + up[1]) / 4;
		double scaleVar = (down[1] + up[1]) / (val * val);
		return new double[] { biasMean, Math.sqrt(biasVar), scaleMean, Math.sqrt(scaleVar) };
	}

	private static double[] getMeanVar(ArrayList<double[]> dataList, int index) throws Exception {
		if (dataList.size() == 0) {
			throw new Exception("Fatal Error: Data list is empty");
		}
		double mean = dataList.stream().mapToDouble(i -> i[index]).average().orElse(0.0);
		int n = dataList.size();
		double var = dataList.stream().mapToDouble(i -> i[index] - mean).map(i -> i * i).sum() / ((n - 1)*n);
		return new double[] { mean, var };
	}

	private static void print(double[] data) {
		int n = data.length;
		for (int i = 0; i < n; i++) {
			System.out.print(data[i] + "  ");
		}
		System.out.println("\n");
	}
}


