
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package com.rti.edge.types.reading;

import com.rti.dds.typecode.*;

public class  ReadingTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        StructMember sm[]=new StructMember[7];

        sm[__i]=new  StructMember("id", false, (short)-1,  false,(TypeCode) TypeCode.TC_ULONGLONG,0 , false);__i++;
        sm[__i]=new  StructMember("ts", false, (short)-1,  false,(TypeCode) TypeCode.TC_ULONG,1 , false);__i++;
        sm[__i]=new  StructMember("value", false, (short)-1,  false,(TypeCode) TypeCode.TC_FLOAT,2 , false);__i++;
        sm[__i]=new  StructMember("property", false, (short)-1,  false,(TypeCode) TypeCode.TC_BOOLEAN,3 , false);__i++;
        sm[__i]=new  StructMember("plug_id", false, (short)-1,  false,(TypeCode) TypeCode.TC_ULONG,4 , false);__i++;
        sm[__i]=new  StructMember("household_id", false, (short)-1,  false,(TypeCode) TypeCode.TC_ULONG,5 , false);__i++;
        sm[__i]=new  StructMember("house_id", false, (short)-1,  false,(TypeCode) TypeCode.TC_ULONG,6 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_struct_tc("Reading",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY,  sm);        
        return tc;
    }
}

