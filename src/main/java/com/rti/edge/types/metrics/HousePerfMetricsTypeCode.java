
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package com.rti.edge.types.metrics;

import com.rti.dds.typecode.*;

public class  HousePerfMetricsTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        StructMember sm[]=new StructMember[6];

        sm[__i]=new  StructMember("ts", false, (short)-1,  false,(TypeCode) TypeCode.TC_ULONG,0 , false);__i++;
        sm[__i]=new  StructMember("throughput_per_sec", false, (short)-1,  false,(TypeCode) TypeCode.TC_FLOAT,1 , false);__i++;
        sm[__i]=new  StructMember("avg_latency_mili", false, (short)-1,  false,(TypeCode) TypeCode.TC_FLOAT,2 , false);__i++;
        sm[__i]=new  StructMember("min_latency_mili", false, (short)-1,  false,(TypeCode) TypeCode.TC_FLOAT,3 , false);__i++;
        sm[__i]=new  StructMember("max_latency_mili", false, (short)-1,  false,(TypeCode) TypeCode.TC_FLOAT,4 , false);__i++;
        sm[__i]=new  StructMember("house_id", false, (short)-1,  false,(TypeCode) TypeCode.TC_ULONG,5 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_struct_tc("HousePerfMetrics",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY,  sm);        
        return tc;
    }
}

