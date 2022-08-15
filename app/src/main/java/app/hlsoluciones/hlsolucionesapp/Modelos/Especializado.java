package app.hlsoluciones.hlsolucionesapp.Modelos;

public class Especializado {

    private int id;
    private String ot, oc, status;
    private String horaIngreso, horaSalida;
    private String imgIngreso1, imgIngreso2, imgIngreso3, imgIngreso4;
    private String imgSalida1, imgSalida2, imgSalida3, imgSalida4;
    private Vehiculo vehiculo;
    private Servicio servicio;
    private User user;
    private Especialista especialista;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOt() {
        return ot;
    }

    public void setOt(String ot) {
        this.ot = ot;
    }

    public String getOc() {
        return oc;
    }

    public void setOc(String oc) {
        this.oc = oc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHoraIngreso() {
        return horaIngreso;
    }

    public void setHoraIngreso(String horaIngreso) {
        this.horaIngreso = horaIngreso;
    }

    public String getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(String horaSalida) {
        this.horaSalida = horaSalida;
    }

    public String getImgIngreso1() {
        return imgIngreso1;
    }

    public void setImgIngreso1(String imgIngreso1) {
        this.imgIngreso1 = imgIngreso1;
    }

    public String getImgIngreso2() {
        return imgIngreso2;
    }

    public void setImgIngreso2(String imgIngreso2) {
        this.imgIngreso2 = imgIngreso2;
    }

    public String getImgIngreso3() {
        return imgIngreso3;
    }

    public void setImgIngreso3(String imgIngreso3) {
        this.imgIngreso3 = imgIngreso3;
    }

    public String getImgIngreso4() {
        return imgIngreso4;
    }

    public void setImgIngreso4(String imgIngreso4) {
        this.imgIngreso4 = imgIngreso4;
    }

    public String getImgSalida1() {
        return imgSalida1;
    }

    public void setImgSalida1(String imgSalida1) {
        this.imgSalida1 = imgSalida1;
    }

    public String getImgSalida2() {
        return imgSalida2;
    }

    public void setImgSalida2(String imgSalida2) {
        this.imgSalida2 = imgSalida2;
    }

    public String getImgSalida3() {
        return imgSalida3;
    }

    public void setImgSalida3(String imgSalida3) {
        this.imgSalida3 = imgSalida3;
    }

    public String getImgSalida4() {
        return imgSalida4;
    }

    public void setImgSalida4(String imgSalida4) {
        this.imgSalida4 = imgSalida4;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Especialista getEspecialista() {
        return especialista;
    }

    public void setEspecialista(Especialista especialista) {
        this.especialista = especialista;
    }
}
