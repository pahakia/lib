package com.pahakia.annotation.registry;

import pahakia.fault.FaultCode;

public class AnnotationRegistryFaultCodes {
    public static final FaultCode InvalidEntry = new FaultCode("com.pahakia.annotation.registry.InvalidEntry", 2,
            "Invalid annotation entry in file {0}: \"{1}\"");
}
