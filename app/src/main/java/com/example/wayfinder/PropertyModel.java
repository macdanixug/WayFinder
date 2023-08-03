package com.example.brokers;

public class PropertyModel {
    String firstname, lastname, phone, email,property_type,propertyname,radioGroup;

    public PropertyModel() {
    }

    public PropertyModel(String firstname, String lastname, String phone, String email, String property_type, String propertyname, String radioGroup) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.email = email;
        this.property_type = property_type;
        this.propertyname = propertyname;
        this.radioGroup = radioGroup;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProperty_type() {
        return property_type;
    }

    public void setProperty_type(String property_type) {
        this.property_type = property_type;
    }

    public String getPropertyname() {
        return propertyname;
    }

    public void setPropertyname(String propertyname) {
        this.propertyname = propertyname;
    }

    public String getRadioGroup() {
        return radioGroup;
    }

    public void setRadioGroup(String radioGroup) {
        this.radioGroup = radioGroup;
    }
}
