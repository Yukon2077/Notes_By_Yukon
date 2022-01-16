package models;

public class Entry {

    private Integer id;
    private String entry;
    private String created_datetime;
    private String last_modified_datetime;

    public Entry(Integer id, String entry, String created_datetime, String last_modified_datetime) {
        this.id = id;
        this.entry = entry;
        this.created_datetime = created_datetime;
        this.last_modified_datetime = last_modified_datetime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
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
