package gei.id.tutelado.model;

import java.time.LocalDateTime;

import javax.persistence.*;

@TableGenerator(name="xeradorIdsEntradas", table="taboa_ids",
pkColumnName="nome_id", pkColumnValue="idEntradaLog",
valueColumnName="ultimo_valor_id",
initialValue=0, allocationSize=1)

@NamedQueries ({
	@NamedQuery (name="EntradaLog.recuperaPorCodigo",
				 query="SELECT el FROM EntradaLog el where el.codigo=:codigo")
})

@Entity
public class EntradaLog implements Comparable<EntradaLog> {
    @Id
    @GeneratedValue(generator="xeradorIdsEntradas")
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigo;

    @Column(unique=false, nullable = false)
    private String descricion;

    @Column(unique=false, nullable = false)
    private LocalDateTime dataHora;

    @ManyToOne(cascade={}, fetch=FetchType.EAGER)
    @JoinColumn (nullable=false, unique=false)
    private Usuario usuario;

	public Long getId() {
		return id;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getDescricion() {
		return descricion;
	}

	public LocalDateTime getDataHora() {
		return dataHora;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public void setDescricion(String descricion) {
		this.descricion = descricion;
	}

	public void setDataHora(LocalDateTime dataHora) {
		this.dataHora = dataHora;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
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
		EntradaLog other = (EntradaLog) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EntradaLog [id=" + id + ", codigo=" + codigo + ", descricion=" + descricion + ", dataHora=" + dataHora
				+ "]";
	}

	@Override
	public int compareTo(EntradaLog other) {
		return (this.dataHora.isBefore(other.getDataHora())? -1:1);
	}

    

}
