package com.masterteknoloji.net.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

import com.masterteknoloji.net.domain.enumeration.AlarmType;

/**
 * A Alarm.
 */
@Entity
@Table(name = "alarm")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Alarm implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "insert_date")
    private ZonedDateTime insertDate;

    @Lob
    @Column(name = "image")
    private byte[] image;

    @Column(name = "image_content_type")
    private String imageContentType;

    @Lob
    @Column(name = "back_ground_image")
    private byte[] backGroundImage;

    @Column(name = "back_ground_image_content_type")
    private String backGroundImageContentType;

    @Column(name = "image_file")
    private String imageFile;

    @Column(name = "alarm_type")
    private String alarmType;

    @Enumerated(EnumType.STRING)
    @Column(name = "alarm_type_value")
    private AlarmType alarmTypeValue;

    @Column(name = "false_alarm")
    private Boolean falseAlarm;

    @Column(name = "processed")
    private Boolean processed;

    @Column(name = "note")
    private String note;

    @ManyToOne
    private Device device;

    @ManyToOne
    private Person person;

    @ManyToOne
    private Camera camera;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getInsertDate() {
        return insertDate;
    }

    public Alarm insertDate(ZonedDateTime insertDate) {
        this.insertDate = insertDate;
        return this;
    }

    public void setInsertDate(ZonedDateTime insertDate) {
        this.insertDate = insertDate;
    }

    public byte[] getImage() {
        return image;
    }

    public Alarm image(byte[] image) {
        this.image = image;
        return this;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public Alarm imageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
        return this;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public byte[] getBackGroundImage() {
        return backGroundImage;
    }

    public Alarm backGroundImage(byte[] backGroundImage) {
        this.backGroundImage = backGroundImage;
        return this;
    }

    public void setBackGroundImage(byte[] backGroundImage) {
        this.backGroundImage = backGroundImage;
    }

    public String getBackGroundImageContentType() {
        return backGroundImageContentType;
    }

    public Alarm backGroundImageContentType(String backGroundImageContentType) {
        this.backGroundImageContentType = backGroundImageContentType;
        return this;
    }

    public void setBackGroundImageContentType(String backGroundImageContentType) {
        this.backGroundImageContentType = backGroundImageContentType;
    }

    public String getImageFile() {
        return imageFile;
    }

    public Alarm imageFile(String imageFile) {
        this.imageFile = imageFile;
        return this;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public Alarm alarmType(String alarmType) {
        this.alarmType = alarmType;
        return this;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public AlarmType getAlarmTypeValue() {
        return alarmTypeValue;
    }

    public Alarm alarmTypeValue(AlarmType alarmTypeValue) {
        this.alarmTypeValue = alarmTypeValue;
        return this;
    }

    public void setAlarmTypeValue(AlarmType alarmTypeValue) {
        this.alarmTypeValue = alarmTypeValue;
    }

    public Boolean isFalseAlarm() {
        return falseAlarm;
    }

    public Alarm falseAlarm(Boolean falseAlarm) {
        this.falseAlarm = falseAlarm;
        return this;
    }

    public void setFalseAlarm(Boolean falseAlarm) {
        this.falseAlarm = falseAlarm;
    }

    public Boolean isProcessed() {
        return processed;
    }

    public Alarm processed(Boolean processed) {
        this.processed = processed;
        return this;
    }

    public void setProcessed(Boolean processed) {
        this.processed = processed;
    }

    public String getNote() {
        return note;
    }

    public Alarm note(String note) {
        this.note = note;
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Device getDevice() {
        return device;
    }

    public Alarm device(Device device) {
        this.device = device;
        return this;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Person getPerson() {
        return person;
    }

    public Alarm person(Person person) {
        this.person = person;
        return this;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Camera getCamera() {
        return camera;
    }

    public Alarm camera(Camera camera) {
        this.camera = camera;
        return this;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Alarm alarm = (Alarm) o;
        if (alarm.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), alarm.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Alarm{" +
            "id=" + getId() +
            ", insertDate='" + getInsertDate() + "'" +
            ", image='" + getImage() + "'" +
            ", imageContentType='" + getImageContentType() + "'" +
            ", backGroundImage='" + getBackGroundImage() + "'" +
            ", backGroundImageContentType='" + getBackGroundImageContentType() + "'" +
            ", imageFile='" + getImageFile() + "'" +
            ", alarmType='" + getAlarmType() + "'" +
            ", alarmTypeValue='" + getAlarmTypeValue() + "'" +
            ", falseAlarm='" + isFalseAlarm() + "'" +
            ", processed='" + isProcessed() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
