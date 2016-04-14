package utils;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.signum;
import static java.lang.Math.sqrt;

public class RootFinder {
	
	public static double linearRoot(double a, double b) {
		
		return -b / a;
		
	}
	
	public static double[] quadraticRoots(double a, double b, double c) {
		
		return new double[] { (-b + sqrt((b * b) - (4 * a * c))) / (2 * a), (-b - sqrt((b * b) - (4 * a * c))) / (2 * a) };
		
	}
	
	public static double[] cubicRoots(double a, double b, double c, double d) {
		
		double A = b / a;
		double B = c / a;
		double C = d / a;
		
		double Q, R, D, S, T, Im;
		
		Q = (3 * B - pow(A, 2)) / 9;
		R = (9 * A * B - 27 * C - 2 * pow(A, 3)) / 54;
		D = pow(Q, 3) + pow(R, 2);
		
		double[] t = new double[3];
		
		if (D >= 0) {
			
			S = signum(R + sqrt(D)) * pow(abs(R + sqrt(D)), (1 / 3.));
			T = signum(R - sqrt(D)) * pow(abs(R - sqrt(D)), (1 / 3.));
			
			t[0] = -A / 3 + (S + T); // real root
			t[1] = -A / 3 - (S + T) / 2; // real part of complex root
			t[2] = -A / 3 - (S + T) / 2; // real part of complex root
			Im = abs(sqrt(3) * (S - T) / 2); // complex part of root
												// pair
			
			if (Im != 0) {
				t[1] = -1;
				t[2] = -1;
			}
			
		} else {
			
			double th = acos(R / sqrt(-pow(Q, 3)));
			
			t[0] = 2 * sqrt(-Q) * cos(th / 3) - A / 3;
			t[1] = 2 * sqrt(-Q) * cos((th + 2 * PI) / 3) - A / 3;
			t[2] = 2 * sqrt(-Q) * cos((th + 4 * PI) / 3) - A / 3;
			Im = 0.0;
			
		}
		
		return t;
		
	}
	
}
