package tarea2AATA;

public class Transicion {
    private String destino;
    private String apilar;
    
    public Transicion(String destino, String apilar) {
        this.setDestino(destino);
        this.setApilar(apilar);
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getApilar() {
        return apilar;
    }

    public void setApilar(String apilar) {
        this.apilar = apilar;
    }
}
