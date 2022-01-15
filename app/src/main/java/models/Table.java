package models;

public class Table {

    private Integer id;
    private String name;
    private String created_datetime;
    private String last_modified_datetime;

    public Table(Integer id, String name, String created_datetime, String last_modified_datetime) {
        this.id = id;
        this.name = name;
        this.created_datetime = created_datetime;
        this.last_modified_datetime = last_modified_datetime;
    }

    public Table(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated_datetime() {
        return created_datetime;
    }

    public void setCreated_datetime(String created_datetime) {
        this.created_datetime = created_datetime;
    }

    public String getLast_modified_datetime() {
        return last_modified_datetime;
    }

    public void setLast_modified_datetime(String last_modified_datetime) {
        this.last_modified_datetime = last_modified_datetime;
    }
}
