package dmillerw.mcp.mapping;

/**
 * @author dmillerw
 */
public class MethodMapping {

    public final TypeMapping owner;

    public final String deobf;
    public final String srg;
    public final String obf;

    public final String srgDesc;
    public final String obfDesc;

    public final String humanDescription;

    public MethodMapping(TypeMapping owner, String deobf, String srg, String obf, String srgDesc, String obfDesc, String humanDescription) {
        this.owner = owner;
        this.deobf = deobf;
        this.srg = srg;
        this.obf = obf;
        this.srgDesc = srgDesc;
        this.obfDesc = obfDesc;
        this.humanDescription = humanDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodMapping that = (MethodMapping) o;

        if (!deobf.equals(that.deobf)) return false;
        if (!obf.equals(that.obf)) return false;
        if (!obfDesc.equals(that.obfDesc)) return false;
        if (!owner.equals(that.owner)) return false;
        if (!srg.equals(that.srg)) return false;
        if (!srgDesc.equals(that.srgDesc)) return false;

        return true;
    }

    @Override
    public String toString() {
        return "{owner: " + owner + ", deobf: " + deobf + ", srg: " + srg + ", obf: " + obf + ", srgDesc: " + srgDesc + ", obfDesc: " + obfDesc + ", human: " + humanDescription + "}";
    }

    public String[] toPrettyString() {
        return new String[] {
                "Mapping: " + obf + " => " + srg + " => " + deobf,
                "Description: " + obfDesc + " => " + srgDesc,
                "Owner: " + owner.toString()
        };
    }
}
