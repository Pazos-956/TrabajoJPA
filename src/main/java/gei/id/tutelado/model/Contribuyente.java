package gei.id.tutelado.model;

import javax.persistence.*;
import java.util.Set;

@TableGenerator(name="xeradorIdsContribuyente", table="taboa_ids",
        pkColumnName="nome_id", pkColumnValue="idContribuyente",
        valueColumnName="ultimo_valor_id",
        initialValue=0, allocationSize=1)

@NamedQueries ({
        @NamedQuery (name="Contribuyente.recuperaPorNif",
                query="SELECT c FROM Contribuyente c where c.nif=:nif")
})

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_contribuyente", discriminatorType = DiscriminatorType.STRING)
@Table(name="t_contribuyente")
public abstract class Contribuyente {
    @Id
    @GeneratedValue(generator="xeradorIdsContribuyente")
    private Long id;
    @Column(unique = true, nullable = false)
    private String nif;
    @Column(unique = false, nullable = false)
    private String nombre;
    @Column(unique = false, nullable = false)
    private String direccion;
    @OneToMany (mappedBy="contribuyente",fetch = FetchType.EAGER)
    private Set<Declaracion> declaraciones;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Set<Declaracion> getDeclaraciones() {
        return declaraciones;
    }

    public void setDeclaraciones(Set<Declaracion> declaraciones) {
        this.declaraciones = declaraciones;
    }

    public void addDeclaracion(Declaracion declaracion) {
        if (declaracion == null){
            throw new RuntimeException("Error intentando añadir un contribuyente nulo");
        }
        if (declaracion.getContribuyente() != null) {
            declaracion.getContribuyente().getDeclaraciones().remove(declaracion);
        }
        this.declaraciones.add(declaracion);
        declaracion.setContribuyente(this);
    }
    public void removeDeclaracion(Declaracion declaracion) {
        if (declaracion == null){
            throw new RuntimeException("Error intentando eliminar un contribuyente nulo");
        }
        if (! this.declaraciones.contains(declaracion)){
            throw new RuntimeException("Error intentando eliminar una declaracion que no pertenece a este contribuyente");
        }
        this.declaraciones.remove(declaracion);
        declaracion.setContribuyente(null);
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nif == null) ? 0 : nif.hashCode());
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
        Contribuyente other = (Contribuyente) obj;
        if (nif == null) {
            return other.nif == null;
        } else if (!nif.equals(other.nif))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Contribuyente [id=" + id + ", nif=" + nif + ", nombre=" + nombre + ", direccion=" + direccion +", declaraciones=" + declaraciones + "]";
    }

}
