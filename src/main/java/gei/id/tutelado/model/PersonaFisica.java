package gei.id.tutelado.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("PF")
@Table(name="t_contribuyente_pf")
@PrimaryKeyJoinColumn(name="id_contribuyente")
public class PersonaFisica extends Contribuyente {
    @Column(unique = false, nullable = false)
    private LocalDate fechaNacimiento;
    @Column(unique = false, nullable = true)
    private String estadoCivil;

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }



}
