package com.model;

/**
 * @author Michael
 */
public class skill {

    int id;
    String name, alias;

    public skill(int id, String name, String alias) {
        this.id = id;
        this.name = name;
        this.alias = alias;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

}
