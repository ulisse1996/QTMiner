package it.uniba.data;

import java.math.BigDecimal;
import java.sql.Blob;

/**
 * Modella un salvataggio che contiene un Blob utilizzabile per deserializzare un {@link it.uniba.mining.ClusterSet}
 */
public class SaveFile {

    private BigDecimal id;
    private String fileName;
    private Blob data;

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setData(Blob data) {
        this.data = data;
    }

    public Blob getData() {
        return data;
    }

    public String toString() {
        return fileName;
    }
}
