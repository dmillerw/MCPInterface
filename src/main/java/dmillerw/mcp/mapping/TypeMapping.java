package dmillerw.mcp.mapping;

/**
 * @author dmillerw
 */
public class TypeMapping {

    public final String srg;
    public final String obf;

    public TypeMapping(String srg, String obf) {
        this.srg = srg;
        this.obf = obf;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypeMapping that = (TypeMapping) o;

        if (!obf.equals(that.obf)) return false;
        if (!srg.equals(that.srg)) return false;

        return true;
    }

    @Override
    public String toString() {
        return obf + " => " + srg;
    }
}
