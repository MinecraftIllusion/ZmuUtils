package net.illusionsmc.zmuutils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

public interface IMappingFile {
    public static IMappingFile load(File path) throws IOException {
        try (InputStream in = new FileInputStream(path)) {
            return load(in);
        }
    }

    public static IMappingFile load(InputStream in) throws IOException {
        return InternalUtils.load(in);
    }

    public enum Format {
        ZMU(false, false, false),
        XZMU(false, true, false),
        CZMU(false, false, false),
        TZMU(true, false, false),
        TZMU2(true, true, true),
        PG(true, true, false),
        TINY1(false, true, true),
        TINY(true, true, false)
        ;

        private final boolean ordered;
        private final boolean hasFieldTypes;
        private final boolean hasNames;

        private Format(boolean ordered, boolean hasFieldTypes, boolean hasNames) {
            this.ordered = ordered;
            this.hasFieldTypes = hasFieldTypes;
            this.hasNames = hasNames;
        }
        public boolean isOrdered() {
            return this.ordered;
        }

        public boolean hasFieldTypes() {
            return this.hasFieldTypes;
        }

        public boolean hasNames() {
            return this.hasNames;
        }

        public static Format get(String name) {
            name = name.toUpperCase(Locale.ENGLISH);
            for (Format value : values())
                if (value.name().equals(name))
                    return value;
            return null;
        }
    }

    Collection<? extends IPackage> getPackages();
    IPackage getPackage(String original);

    Collection<? extends IClass> getClasses();
    IClass getClass(String original);

    String remapPackage(String pkg);
    String remapClass(String desc);
    String remapDescriptor(String desc);

    void write(Path path, Format format, boolean reversed) throws IOException;

    IMappingFile reverse();
    IMappingFile rename(IRenamer renamer);
    IMappingFile chain(IMappingFile other);

    public interface INode {
        String getOriginal();
        String getMapped();
        @Nullable
            // Returns null if the specified format doesn't support this node type
        String write(Format format, boolean reversed);

        /*
         * A unmodifiable map of various metadata that is attached to this node.
         * This is very dependent on the format. Some examples:
         * Tiny v1/v2:
         *   "comment": Javadoc comment to insert into source
         * TSRG:
         *   On Methods:
         *     "is_static": Value means nothing, just a marker if it exists.
         * Proguard:
         *   On Methods:
         *     "start_line": The source line that this method starts on
         *     "end_line": The source line for the end of this method
         */
        Map<String, String> getMetadata();
    }

    public interface IPackage extends INode {}

    public interface IClass extends INode {
        Collection<? extends IField> getFields();
        Collection<? extends IMethod> getMethods();

        String remapField(String field);
        String remapMethod(String name, String desc);

        @Nullable
        IField getField(String name);
        @Nullable
        IMethod getMethod(String name, String desc);
    }

    public interface IOwnedNode<T> extends INode {
        T getParent();
    }

    public interface IField extends IOwnedNode<IClass> {
        @Nullable
        String getDescriptor();
        @Nullable
        String getMappedDescriptor();
    }

    public interface IMethod extends IOwnedNode<IClass> {
        String getDescriptor();
        String getMappedDescriptor();
        Collection<? extends IParameter> getParameters();
        String remapParameter(int index, String name);
    }

    public interface IParameter extends IOwnedNode<IMethod> {
        int getIndex();
    }
}
