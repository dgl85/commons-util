package org.dgl.commons.util;

public class YAMLBuilderTestStructure {
    private String name = "";
    private String lastName = "";
    private String address = "";
    private int postalCode = 0;
    private int[] childrenAge = new int[0];
    private boolean isMarried = false;
    private int privateID = 7;

    public String getName() {
        return name;
    }

    public YAMLBuilderTestStructure setName(String name) {
        this.name = name;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public YAMLBuilderTestStructure setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public YAMLBuilderTestStructure setAddress(String address) {
        this.address = address;
        return this;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public YAMLBuilderTestStructure setPostalCode(int postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public int[] getChildrenAge() {
        return childrenAge;
    }

    public YAMLBuilderTestStructure setChildrenAge(int[] childrenAge) {
        this.childrenAge = childrenAge;
        return this;
    }

    public boolean isMarried() {
        return isMarried;
    }

    public YAMLBuilderTestStructure setMarried(boolean married) {
        isMarried = married;
        return this;
    }
}
