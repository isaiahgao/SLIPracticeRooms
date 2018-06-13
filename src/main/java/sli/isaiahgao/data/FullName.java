package sli.isaiahgao.data;

/**
 * Represents a person's full name.
 */
public class FullName {

    public FullName(String first, String last) {
        this.first = first;
        this.last = last;
    }

    private String first;
    private String last;

    public String getFirstName() {
        return this.first;
    }

    public String getLastName() {
        return this.last;
    }
    
    @Override
    public String toString() {
        return this.first + "\t" + this.last;
    }

}
