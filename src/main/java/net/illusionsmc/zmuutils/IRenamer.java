package net.illusionsmc.zmuutils;

import net.illusionsmc.zmuutils.IMappingFile.IClass;
import net.illusionsmc.zmuutils.IMappingFile.IPackage;
import net.illusionsmc.zmuutils.IMappingFile.IField;
import net.illusionsmc.zmuutils.IMappingFile.IMethod;
import net.illusionsmc.zmuutils.IMappingFile.IParameter;

public interface IRenamer {
    default String rename(IPackage value) {
        return value.getMapped();
    }

    default String rename(IClass value) {
        return value.getMapped();
    }

    default String rename(IField value) {
        return value.getMapped();
    }

    default String rename(IMethod value) {
        return value.getMapped();
    }

    default String rename(IParameter value) {
        return value.getMapped();
    }
}
