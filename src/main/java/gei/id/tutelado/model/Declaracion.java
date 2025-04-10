package gei.id.tutelado.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@TableGenerator(name="xeradorIdsDeclaracion", table="taboa_ids",
        pkColumnName="nome_id", pkColumnValue="idDeclaracion",
        valueColumnName="ultimo_valor_id",
        initialValue=0, allocationSize=1)

@NamedQueries ({
        @NamedQuery (name="Declaracion.recuperaPorNumeroReferencia",
                query="SELECT d FROM Declaracion d where d.numeroReferencia=:numeroReferencia")
})
@Entity
public class Declaracion {
    @Id
    @GeneratedValue(generator="xeradorIdsDeclaracion")
    private Long id;
    @Column(unique = true, nullable = false)
    private Long numeroReferencia;
    @Column(unique = false, nullable = false)
    private LocalDate fechaPresentacion;
    @Column(unique = false, nullable = false)
    private float importe;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="contribuyente_declaracion", nullable=true, unique=false)
    private Contribuyente contribuyente;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumeroReferencia() {
        return numeroReferencia;
    }

    public void setNumeroReferencia(Long numeroReferencia) {
        this.numeroReferencia = numeroReferencia;
    }

    public LocalDate getFechaPresentacion() {
        return fechaPresentacion;
    }

    public void setFechaPresentacion(LocalDate fechaPresentacion) {
        this.fechaPresentacion = fechaPresentacion;
    }

    public float getImporte() {
        return importe;
    }

    public void setImporte(float importe) {
        this.importe = importe;
    }

    public Contribuyente getContribuyente() {
        return contribuyente;
    }

    public void setContribuyente(Contribuyente contribuyente) {
        this.contribuyente = contribuyente;
    }




    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((numeroReferencia == null) ? 0 : numeroReferencia.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Declaracion other = (Declaracion) obj;
        if (numeroReferencia == null) {
            return other.numeroReferencia == null;
        } else if (!numeroReferencia.equals(other.numeroReferencia))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Contribuyente [id=" + id + ", numeroReferencia=" + numeroReferencia + ", fechaPresentacion=" + fechaPresentacion + ", importe=" + importe +", contribuyente]";
    }


}
