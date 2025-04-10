package gei.id.tutelado.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("PF")
@Table(name="t_contribuyente_pf")
@PrimaryKeyJoinColumn(name="id_contribuyente")
public class PersonaFisica extends Contribuyente {
    @Column(unique = false, nullable = false)
    private LocalDate fechaNacimiento;
    @Column(unique = false, nullable = true)
    private String estadoCivil;

    @ManyToMany(fetch = FetchType.EAGER,cascade =CascadeType.PERSIST)
    @JoinTable(name="t_pf_pj",
            joinColumns=@JoinColumn(name="id_pf"),
            inverseJoinColumns=@JoinColumn(name="id_pj"))
    private Set<PersonaJuridica> personasJuridicas = new HashSet<PersonaJuridica>();

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

    public Set<PersonaJuridica> getPersonaJuridicas() {
        return personasJuridicas;
    }

    public void setPersonaJuridicas(Set<PersonaJuridica> personaJuridicas) {
        this.personasJuridicas = personaJuridicas;
    }





}
