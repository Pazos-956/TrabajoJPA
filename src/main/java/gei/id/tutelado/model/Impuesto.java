package gei.id.tutelado.model;

import javax.persistence.*;

@TableGenerator(name="xeradorIdsImpuesto", table="taboa_ids",
        pkColumnName="nome_id", pkColumnValue="idImpuesto",
        valueColumnName="ultimo_valor_id",
        initialValue=0, allocationSize=1)

@NamedQueries ({
        @NamedQuery (name="Impuesto.recuperaPorCodigo",
                query="SELECT i FROM Impuesto i where i.codigo=:codigo")
})

@Entity
public class Impuesto {
    @Id
    @GeneratedValue(generator="XeradorIdsImpuesto")
    private Long id;
    @Column(unique = true, nullable = false)
    private Long codigo;
    @Column(unique = false, nullable = true)
    private String descripcion;
    @Column(unique = false, nullable = false)
    private TipoImpuesto tipoImpuesto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public TipoImpuesto getTipo() {
        return tipoImpuesto;
    }

    public void setTipo(TipoImpuesto tipoImpuesto) {
        this.tipoImpuesto = tipoImpuesto;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
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
        Impuesto other = (Impuesto) obj;
        if (codigo == null) {
            return other.codigo == null;
        } else if (!codigo.equals(other.codigo))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Contribuyente [id=" + id + ", codigo=" + codigo + ", descripcion=" + descripcion + ", tipoImpuesto=" + tipoImpuesto + "]";
    }

}
