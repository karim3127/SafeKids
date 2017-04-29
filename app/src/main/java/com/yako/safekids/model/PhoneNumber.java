package com.yako.safekids.model;

import com.backendless.BackendlessUser;

/**
 * Created by macbook on 20/04/16.
 */
public class PhoneNumber {
    String objectId;
    String numero;
    String codeCountry;
    BackendlessUser user;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCodeCountry() {
        return codeCountry;
    }

    public void setCodeCountry(String codeCountry) {
        this.codeCountry = codeCountry;
    }

    public BackendlessUser getUser() {
        return user;
    }

    public void setUser(BackendlessUser user) {
        this.user = user;
    }

}
