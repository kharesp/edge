
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package com.rti.edge.types.houseAvg;

import com.rti.dds.typecode.*;

public class  HouseAvgTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        StructMember sm[]=new StructMember[5];

        sm[__i]=new  StructMember("ts", false, (short)-1,  false,(TypeCode) TypeCode.TC_ULONG,0 , false);__i++;
        sm[__i]=new  StructMember("average", false, (short)-1,  false,(TypeCode) TypeCode.TC_FLOAT,1 , false);__i++;
        sm[__i]=new  StructMember("property", false, (short)-1,  false,(TypeCode) TypeCode.TC_BOOLEAN,2 , false);__i++;
        sm[__i]=new  StructMember("window_size", false, (short)-1,  false,(TypeCode) TypeCode.TC_ULONG,3 , false);__i++;
        sm[__i]=new  StructMember("house_id", false, (short)-1,  false,(TypeCode) TypeCode.TC_ULONG,4 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_struct_tc("HouseAvg",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY,  sm);        
        return tc;
    }
}

