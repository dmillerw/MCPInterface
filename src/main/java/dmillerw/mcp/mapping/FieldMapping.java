package dmillerw.mcp.mapping;

/**
 * @author dmillerw
 */
public class FieldMapping {

    public final TypeMapping owner;

    public final String deobf;
    public final String srg;
    public final String obf;

    public final String humanDescription;

    public FieldMapping(TypeMapping owner, String deobf, String srg, String obf, String humanDescription) {
        this.owner = owner;
        this.deobf = deobf;
        this.srg = srg;
        this.obf = obf;
        this.humanDescription = humanDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldMapping that = (FieldMapping) o;

        if (!deobf.equals(that.deobf)) return false;
        if (!obf.equals(that.obf)) return false;
        if (!owner.equals(that.owner)) return false;
        if (!srg.equals(that.srg)) return false;

        return true;
    }

    @Override
    public String toString() {
        return "{owner: " + owner + ", deobf: " + deobf + ", srg: " + srg + ", obf: " + obf + ", human: " + humanDescription + "}";
    }

    public String[] toPrettyString() {
        return new String[] {
                "Mapping: " + obf + " => " + srg + " => " + deobf,
                "Owner: " + owner.toString()
        };
    }
}
