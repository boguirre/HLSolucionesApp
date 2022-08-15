package app.hlsoluciones.hlsolucionesapp.Modelos;

public class Vehiculo {

    private int id;
    private String numplaca, cifravin, status, fotoplaca;
    private Modelo modelo;
    private Marca marca;
    private Area area;
    private SubArea subArea;
    private User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumplaca() {
        return numplaca;
    }

    public void setNumplaca(String numplaca) {
        this.numplaca = numplaca;
    }

    public String getCifravin() {
        return cifravin;
    }

    public void setCifravin(String cifravin) {
        this.cifravin = cifravin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFotoplaca() {
        return fotoplaca;
    }

    public void setFotoplaca(String fotoplaca) {
        this.fotoplaca = fotoplaca;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public SubArea getSubArea() {
        return subArea;
    }

    public void setSubArea(SubArea subArea) {
        this.subArea = subArea;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
