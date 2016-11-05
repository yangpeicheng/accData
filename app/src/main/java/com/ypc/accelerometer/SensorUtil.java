package com.ypc.accelerometer;

import java.util.List;
import android.support.v7.app.AppCompatActivity;
/**
 * Created by user on 2016/10/23.
 */

public class SensorUtil {
    public static double[] smooth(double [] in,int span){
        double[] result=new double[in.length];
        int left, begin, right,end;
        left = span/2+span%2;
        right=span/2;
        for( int i= 0; i<in.length; i++){
            begin=(i-left+1>0)?(i-left+1):0;
            end=(i+right<in.length)?(i+right):in.length-1;
            for(int j=begin;j<=end;j++)
                result[i]+=in[j];
            result[i]/=(end-begin+1);
        }
        return result;
    }
    public static float realTimeSmooth(float[] in){
        if(in.length==0)
            return 0;
        float result=0;
        for(float s:in){
            result+=s;
        }
        return result/in.length;
    }

}
