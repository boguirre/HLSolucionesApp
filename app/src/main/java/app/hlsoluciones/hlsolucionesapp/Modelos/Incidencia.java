package app.hlsoluciones.hlsolucionesapp.Modelos;

public class Incidencia {
    private int id;
    private String descripcion;
    private String fecha;
    private String imgIncidente1, imgIncidente2, imgIncidente3;
    private Modelo modelo;
    private Marca marca;
    private User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImgIncidente1() {
        return imgIncidente1;
    }

    public void setImgIncidente1(String imgIncidente1) {
        this.imgIncidente1 = imgIncidente1;
    }

    public String getImgIncidente2() {
        return imgIncidente2;
    }

    public void setImgIncidente2(String imgIncidente2) {
        this.imgIncidente2 = imgIncidente2;
    }

    public String getImgIncidente3() {
        return imgIncidente3;
    }

    public void setImgIncidente3(String imgIncidente3) {
        this.imgIncidente3 = imgIncidente3;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
