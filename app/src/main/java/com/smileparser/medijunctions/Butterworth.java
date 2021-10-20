package com.smileparser.medijunctions;

import java.io.PrintStream;

/**
Class to implement digital Butterworth filters.
*/
public class Butterworth extends IIRFilter {


/**
 * Instantiates a new Butterworth digital filter.
 *
 * @param order      int specifying the order (number of poles) of the filter.
 * @param type       PassbandType specifying whether the filter is a lowpass, bandpass or highpass filter.
 * @param f1         double specifying the low cutoff frequency (must always be present, but used only for 
 *                   bandpass and highpass filters).
 * @param f2         double specifying the high cutoff frequency (must always be present, but used only for
 *                   bandpass and lowpass filters).
 * @param delta      double specifying the sampling interval of the data to which this filter will be applied.
 */
public Butterworth(int order, PassbandType type, double f1, double f2, double delta ) {
  
  super( new AnalogButterworth( order ), type, f1, f2, delta );
    
}



public double[] filter_output(double[] raw_data) {
        
      //Butterworth B = new Butterworth( 3, PassbandType.BANDPASS, 2.0, 5.0, 0.025 );
      //B.print( System.out );
      double[] tmp = new double[201];
      for ( int i = 0;  i < 201;  i++ ) {
              Complex C = evaluate( Math.PI/200.0*i );
              tmp[i] = (double) Complex.abs( C );
      }
      
      double[] x = new double[ raw_data.length ];
      filter(raw_data,x );
      
      PrintStream ps;
     /* try {
              ps = new PrintStream( new FileOutputStream( "Response.m" ) );
              ps.print( "R = [ " );
              for ( int i = 0;  i < 200;  i++ ) {
                      ps.println( tmp[i] + "; ..." );
              }
              ps.println( tmp[200] + "];" );
              ps.close();
      } catch (FileNotFoundException e) {
              e.printStackTrace();
      }
      
      try {
              ps = new PrintStream( new FileOutputStream( "ImpulseResponse.m" ) );
              ps.print( "IR = [ " );
              for ( int i = 0;  i < 1000;  i++ ) {
                      ps.println( y[i] + "; ..." );
              }
              ps.println( y[1000] + "];" );
              ps.close();
      } catch (FileNotFoundException e) {
              e.printStackTrace();
      }*/

      return x;
}

}

