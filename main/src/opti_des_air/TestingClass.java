package opti_des_air;

import java.math.BigInteger;

public class TestingClass {
	public static void main(String[] args) {
		 String test = Long.toBinaryString(Double.doubleToRawLongBits(5.744));
		 double doubleVal = Double.longBitsToDouble(new BigInteger(test, 2).longValue());
		 System.out.println(test);
		 System.out.println(doubleVal);
	}	 
	
	
	

}
