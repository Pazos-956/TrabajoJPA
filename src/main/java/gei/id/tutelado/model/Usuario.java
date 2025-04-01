package gei.id.tutelado.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.SortedSet;
import java.util.TreeSet;

@TableGenerator(name="xeradorIdsUsuarios", table="taboa_ids",
pkColumnName="nome_id", pkColumnValue="idUsuario",
valueColumnName="ultimo_valor_id",
initialValue=0, allocationSize=1)

@NamedQueries ({
	@NamedQuery (name="Usuario.recuperaPorNif",
				 query="SELECT u FROM Usuario u where u.nif=:nif")
})

@Entity
public class Usuario {
    @Id
    @GeneratedValue (generator="xeradorIdsUsuarios")
    private Long id;

    @Column(nullable = false, unique = true)
    private String nif;

    @Column(nullable = false, unique=false)
    private String nome;

    @Column(nullable = false, unique=false)
    private LocalDate dataAlta;
     
    @OneToMany (mappedBy="usuario", fetch=FetchType.LAZY, cascade={CascadeType.PERSIST, CascadeType.REMOVE} )
    @OrderBy("dataHora ASC")
    private SortedSet<EntradaLog> entradasLog = new TreeSet<>();
    // NOTA: necesitamos @OrderBy, ainda que a colección estea definida como LAZY, por se nalgun momento accedemos á propiedade DENTRO de sesión.
    // Garantimos así que cando Hibernate cargue a colección, o faga na orde axeitada na consulta que lanza contra a BD
    public Long getId() {
		return id;
	}

	public String getNif() {
		return nif;
	}

	public String getNome() {
		return nome;
	}

	public LocalDate getDataAlta() {
		return dataAlta;
	}

	public SortedSet<EntradaLog> getEntradasLog() {
		return entradasLog;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setNif(String nif) {
		this.nif = nif;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setDataAlta(LocalDate dataAlta) {
		this.dataAlta = dataAlta;
	}

	public void setEntradasLog(SortedSet<EntradaLog> entradasLog) {
		this.entradasLog = entradasLog;
	}
	
	// Metodo de conveniencia para asegurarnos de que actualizamos os dous extremos da asociación ao mesmo tempo
	public void engadirEntradaLog(EntradaLog entrada) {
		if (entrada.getUsuario() != null) throw new RuntimeException ("");
		entrada.setUsuario(this);
		// É un sorted set, engadimos sempre por orde de data (ascendente)
		this.entradasLog.add(entrada);
	}

	// Metodo de conveniencia para asegurarnos de que actualizamos os dous extremos da asociación ao mesmo tempo
	public void eliminarEntradaLog(EntradaLog entrada) {
		if (!this.entradasLog.contains(entrada)) throw new RuntimeException ("");
		if (!entrada.getUsuario().equals(this)) throw new RuntimeException ("");
		entrada.setUsuario(null);
		this.entradasLog.remove(entrada);
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
		Usuario other = (Usuario) obj;
		if (nif == null) {
            return other.nif == null;
        } else if (!nif.equals(other.nif))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Usuario [id=" + id + ", nif=" + nif + ", nome=" + nome + ", dataAlta=" + dataAlta + "]";
	}

    



    
}
