package ua.tennis.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "settings")
public class Settings implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "key")
    private String key;

    @Column(name = "value")
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        Settings settings = (Settings) object;
        return java.util.Objects.equals(id, settings.id) &&
            java.util.Objects.equals(key, settings.key) &&
            java.util.Objects.equals(value, settings.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, key, value);
    }

    @Override
    public java.lang.String toString() {
        return "Settings{" +
            "id=" + id +
            ", key='" + key + '\'' +
            ", value='" + value + '\'' +
            '}';
    }
}
