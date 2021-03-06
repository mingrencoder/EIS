package com.jk.eis.commen.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "FIELD".
 */
public class Field {
    @Override
    public String toString() {
        return "Field{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }

    private Long id;
    /** Not-null value. */
    private String name;
    private int type;

    public Field() {
    }

    public Field(Long id) {
        this.id = id;
        if(id != null){
            String s = String.valueOf(id).substring(0,1);
            this.type = Integer.valueOf(s);
        }
    }

    public Field(Long id, String name, int type) {
        this.id = id;
        this.name = name;
        this.type = type;
        if(id != null){
            String s = String.valueOf(id).substring(0,1);
            this.type = Integer.valueOf(s);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getName() {
        return name;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
