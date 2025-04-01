package gei.id.tutelado.model;

import javax.persistence.*;

import java.time.LocalDate;
@Entity
@DiscriminatorValue("PJ")
@Table(name="t_contribuyente_pj")
@PrimaryKeyJoinColumn(name="id_contribuyente")
public class PersonaJuridica extends Contribuyente{
    @Column(unique = true, nullable = false)
    private String razonSocial;
    @Column(unique = false, nullable = false)
    private LocalDate fechaConstitucion;

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public LocalDate getFechaConstitucion() {
        return fechaConstitucion;
    }

    public void setFechaConstitucion(LocalDate fechaConstitucion) {
        this.fechaConstitucion = fechaConstitucion;
    }

}
