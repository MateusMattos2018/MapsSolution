package com.example.solutionmaps.classes;

public class Notificacao {

    String IdSolicitacao;
    float Latitude;
    float Longitude;
    String Status;
    String Observacao;

    public Notificacao(String idSolicitacao, float latitude, float longitude, String status, String observacao) {
        IdSolicitacao = idSolicitacao;
        Latitude = latitude;
        Longitude = longitude;
        Status = status;
        Observacao = observacao;
    }

    public Notificacao() {
    }

    public String getIdSolicitacao() {
        return IdSolicitacao;
    }

    public void setIdSolicitacao(String idSolicitacao) {
        IdSolicitacao = idSolicitacao;
    }

    public float getLatitude() {
        return Latitude;
    }

    public void setLatitude(float latitude) {
        Latitude = latitude;
    }

    public float getLongitude() {
        return Longitude;
    }

    public void setLongitude(float longitude) {
        Longitude = longitude;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getObservacao() {
        return Observacao;
    }

    public void setObservacao(String observacao) {
        Observacao = observacao;
    }
}
